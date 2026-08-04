package io.github.son1004007.opsmate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import java.util.ArrayList;
import java.time.Duration;
import java.time.Instant;
import java.math.BigDecimal;
import java.util.Set;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import io.github.son1004007.opsmate.OpsMateTestConfiguration.StubLocalLlmGateway;
import io.github.son1004007.opsmate.demo.DemoPersona;
import io.github.son1004007.opsmate.demo.DemoPrincipal;
import io.github.son1004007.opsmate.demo.DemoWorkspace;
import io.github.son1004007.opsmate.demo.DemoWorkspaceRepository;
import io.github.son1004007.opsmate.demo.DemoWorkspaceService;
import io.github.son1004007.opsmate.domain.PurchaseCategory;
import io.github.son1004007.opsmate.domain.PurchaseRequest;
import io.github.son1004007.opsmate.domain.PurchaseRequestStatus;
import io.github.son1004007.opsmate.infrastructure.persistence.AuditEventRepository;
import io.github.son1004007.opsmate.infrastructure.persistence.PurchaseOrderRepository;
import io.github.son1004007.opsmate.infrastructure.persistence.PurchaseRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.mock.web.MockHttpSession;
import jakarta.servlet.http.Cookie;

@SpringBootTest(properties = {
        "opsmate.demo.enabled=true",
        "opsmate.demo.start-enabled=true",
        "opsmate.demo.max-active-workspaces=20",
        "opsmate.security.basic-enabled=false",
        "opsmate.model-guard.max-requests-per-workspace=100",
        "server.servlet.session.cookie.secure=false"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(OpsMateTestConfiguration.class)
class DemoWebIntegrationTest {

    private static final String LAPTOP_REQUEST = "개발용 노트북 1대를 2500000원에 구매하고 싶습니다.";
    private static final Pattern CSRF_TOKEN_PATTERN = Pattern.compile("name=\"_csrf\"[^>]*value=\"([^\"]+)\"");

    @Autowired
    MockMvc mvc;

    @Autowired
    DemoWorkspaceRepository workspaceRepository;

    @Autowired
    PurchaseRequestRepository requestRepository;

    @Autowired
    PurchaseOrderRepository orderRepository;

    @Autowired
    AuditEventRepository auditRepository;

    @Autowired
    StubLocalLlmGateway llmGateway;

    @Autowired
    DemoWorkspaceService workspaceService;

    @BeforeEach
    void clearSyntheticData() {
        SecurityContextHolder.clearContext();
        llmGateway.reset();
        orderRepository.deleteAll();
        auditRepository.deleteAll();
        requestRepository.deleteAll();
        workspaceRepository.deleteAll();
    }

    @Test
    void browserCompletesRequesterApproverBuyerAndAuditorFlow() throws Exception {
        BrowserSession browser = startDemo();
        UUID workspaceId = principal(browser).workspaceId();

        createDraft(browser, LAPTOP_REQUEST);
        PurchaseRequest request = requestRepository
                .findTop100ByWorkspaceIdOrderByUpdatedAtDesc(workspaceId)
                .getFirst();

        mvc.perform(postWithCsrf("/demo/requests/{id}/submit", browser, request.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/demo"));

        switchPersona(browser, DemoPersona.APPROVER);
        mvc.perform(get("/demo").session(browser.session()))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("개발용 노트북 구매")));
        mvc.perform(postWithCsrf("/demo/requests/{id}/decisions", browser, request.getId())
                        .param("decision", "APPROVE"))
                .andExpect(status().is3xxRedirection());

        switchPersona(browser, DemoPersona.BUYER);
        mvc.perform(postWithCsrf("/demo/orders", browser)
                        .param("purchaseRequestId", request.getId().toString()))
                .andExpect(status().is3xxRedirection());
        mvc.perform(postWithCsrf("/demo/orders", browser)
                        .param("purchaseRequestId", request.getId().toString()))
                .andExpect(status().is3xxRedirection());

        switchPersona(browser, DemoPersona.AUDITOR);
        mvc.perform(get("/demo").session(browser.session()))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("DRAFT_CREATED")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("ORDER_CREATED")));

        assertThat(orderRepository.countByWorkspaceId(workspaceId)).isEqualTo(1);
        assertThat(auditRepository.countByWorkspaceId(workspaceId)).isEqualTo(4);
        assertThat(requestRepository.findByIdAndWorkspaceId(request.getId(), workspaceId).orElseThrow().getStatus())
                .isEqualTo(PurchaseRequestStatus.ORDERED);
    }

    @Test
    void twoBrowserSessionsCannotReadOrMutateEachOthersWorkspace() throws Exception {
        BrowserSession firstBrowser = startDemo();
        UUID firstWorkspace = principal(firstBrowser).workspaceId();
        createDraft(firstBrowser, LAPTOP_REQUEST);
        PurchaseRequest firstRequest = requestRepository
                .findTop100ByWorkspaceIdOrderByUpdatedAtDesc(firstWorkspace)
                .getFirst();

        BrowserSession secondBrowser = startDemo();
        UUID secondWorkspace = principal(secondBrowser).workspaceId();
        assertThat(secondWorkspace).isNotEqualTo(firstWorkspace);

        mvc.perform(postWithCsrf("/demo/requests/{id}/submit", secondBrowser, firstRequest.getId()))
                .andExpect(status().is3xxRedirection());

        assertThat(requestRepository.findByIdAndWorkspaceId(firstRequest.getId(), firstWorkspace)
                .orElseThrow().getStatus()).isEqualTo(PurchaseRequestStatus.DRAFT);
        assertThat(requestRepository.countByWorkspaceId(secondWorkspace)).isZero();

        switchPersona(secondBrowser, DemoPersona.AUDITOR);
        mvc.perform(get("/demo").session(secondBrowser.session()))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("개발용 노트북 구매"))));
    }

    @Test
    void webWritesRequireCsrfAndDemoProfileDeniesBasicApi() throws Exception {
        BrowserSession browser = startDemo();

        mvc.perform(post("/demo/personas")
                        .session(browser.session())
                        .param("persona", "APPROVER"))
                .andExpect(status().isForbidden());

        mvc.perform(get("/api/audit-events")
                        .with(httpBasic("auditor", "test-only-auditor")))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertThat(result.getResponse().getHeader(HttpHeaders.WWW_AUTHENTICATE))
                        .isNull());
    }

    @Test
    void landingPageUsesCsrfCookieWithoutAllocatingServerSession() throws Exception {
        MvcResult result = mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getRequest().getSession(false)).isNull();
        assertThat(result.getResponse().getHeader(HttpHeaders.SET_COOKIE))
                .contains("XSRF-TOKEN")
                .contains("HttpOnly");
        // Servlet 6 stores SameSite as a Cookie attribute; MockHttpServletResponse의
        // 문자열 encoder는 이 속성을 생략하므로 객체와 실제 HTTPS smoke를 함께 검증한다.
        assertThat(result.getResponse().getCookie("XSRF-TOKEN").getAttribute("SameSite"))
                .isEqualTo("Lax");
    }

    @Test
    void anonymousProtectedGetRedirectsWithoutAllocatingServerSession() throws Exception {
        MvcResult result = mvc.perform(get("/demo"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/"))
                .andReturn();

        assertThat(result.getRequest().getSession(false)).isNull();
        assertThat(result.getResponse().getHeaders(HttpHeaders.SET_COOKIE))
                .noneMatch(header -> header.startsWith("JSESSIONID="));
    }

    @Test
    void renderedCsrfCookieAndFormTokenCanStartARealBrowserSession() throws Exception {
        CsrfContext csrf = landingCsrf();

        mvc.perform(post("/demo/sessions")
                        .cookie(csrf.cookie())
                        .param("_csrf", csrf.token()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/demo"));
        assertThat(workspaceRepository.count()).isEqualTo(1);
    }

    @Test
    void rejectedFreshStartDoesNotAllocateServerSession() throws Exception {
        for (int index = 0; index < 20; index++) {
            workspaceService.start();
        }
        CsrfContext csrf = landingCsrf();

        MvcResult rejected = mvc.perform(post("/demo/sessions")
                        .cookie(csrf.cookie())
                        .param("_csrf", csrf.token()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?status=busy"))
                .andReturn();

        assertThat(rejected.getRequest().getSession(false)).isNull();
    }

    @Test
    void resetRotatesWorkspaceAndDeletesAllPreviousSyntheticRows() throws Exception {
        BrowserSession browser = startDemo();
        UUID oldWorkspace = principal(browser).workspaceId();
        createDraft(browser, LAPTOP_REQUEST);
        assertThat(requestRepository.countByWorkspaceId(oldWorkspace)).isEqualTo(1);

        mvc.perform(postWithCsrf("/demo/reset", browser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/demo"));

        UUID newWorkspace = principal(browser).workspaceId();
        assertThat(newWorkspace).isNotEqualTo(oldWorkspace);
        assertThat(workspaceRepository.findById(oldWorkspace)).isEmpty();
        assertThat(requestRepository.countByWorkspaceId(oldWorkspace)).isZero();
        assertThat(orderRepository.countByWorkspaceId(oldWorkspace)).isZero();
        assertThat(auditRepository.countByWorkspaceId(oldWorkspace)).isZero();
    }

    @Test
    void oversizedRejectionReasonIsRejectedBeforeDatabaseCommit() throws Exception {
        BrowserSession browser = startDemo();
        UUID workspaceId = principal(browser).workspaceId();
        createDraft(browser, LAPTOP_REQUEST);
        PurchaseRequest request = requestRepository
                .findTop100ByWorkspaceIdOrderByUpdatedAtDesc(workspaceId)
                .getFirst();
        mvc.perform(postWithCsrf("/demo/requests/{id}/submit", browser, request.getId()))
                .andExpect(status().is3xxRedirection());
        switchPersona(browser, DemoPersona.APPROVER);

        mvc.perform(postWithCsrf("/demo/requests/{id}/decisions", browser, request.getId())
                        .param("decision", "REJECT")
                        .param("reason", "가".repeat(501)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/demo"));

        PurchaseRequest unchanged = requestRepository
                .findByIdAndWorkspaceId(request.getId(), workspaceId)
                .orElseThrow();
        assertThat(unchanged.getStatus()).isEqualTo(PurchaseRequestStatus.PENDING_APPROVAL);
        assertThat(unchanged.getRejectionReason()).isNull();
        assertThat(auditRepository.countByWorkspaceId(workspaceId)).isEqualTo(2);
    }

    @Test
    void modelOutageShowsFailClosedMessageAndPersistsNothing() throws Exception {
        BrowserSession browser = startDemo();
        UUID workspaceId = principal(browser).workspaceId();
        llmGateway.failModel();

        createDraft(browser, LAPTOP_REQUEST);

        mvc.perform(get("/demo").session(browser.session()))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "현재 오픈웨이트 모델을 사용할 수 없습니다")));
        assertThat(requestRepository.countByWorkspaceId(workspaceId)).isZero();
        assertThat(orderRepository.countByWorkspaceId(workspaceId)).isZero();
        assertThat(auditRepository.countByWorkspaceId(workspaceId)).isZero();
    }

    @Test
    void repeatedBrowserDraftSubmissionReusesOneModelResultAndOneRow() throws Exception {
        BrowserSession browser = startDemo();
        UUID workspaceId = principal(browser).workspaceId();

        createDraft(browser, LAPTOP_REQUEST);
        createDraft(browser, LAPTOP_REQUEST);

        assertThat(llmGateway.callCount()).isEqualTo(1);
        assertThat(requestRepository.countByWorkspaceId(workspaceId)).isEqualTo(1);
        assertThat(auditRepository.countByWorkspaceId(workspaceId)).isEqualTo(1);
    }

    @Test
    void repeatedStartFromOneBrowserReusesTheActiveWorkspace() throws Exception {
        BrowserSession browser = startDemo();
        UUID workspaceId = principal(browser).workspaceId();

        mvc.perform(postWithCsrf("/demo/sessions", browser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/demo"));

        assertThat(principal(browser).workspaceId()).isEqualTo(workspaceId);
        assertThat(workspaceRepository.count()).isEqualTo(1);
    }

    @Test
    void concurrentNewSessionsCannotExceedConfiguredCapacity() {
        int attempts = 30;
        ExecutorService executor = Executors.newFixedThreadPool(attempts);
        try {
            List<CompletableFuture<String>> starts = new ArrayList<>();
            for (int index = 0; index < attempts; index++) {
                starts.add(CompletableFuture.supplyAsync(() -> {
                    try {
                        CsrfContext csrf = landingCsrf();
                        return mvc.perform(post("/demo/sessions")
                                        .cookie(csrf.cookie())
                                        .param("_csrf", csrf.token()))
                                .andExpect(status().is3xxRedirection())
                                .andReturn()
                                .getResponse()
                                .getRedirectedUrl();
                    } catch (Exception exception) {
                        throw new AssertionError(exception);
                    }
                }, executor));
            }

            List<String> redirects = starts.stream().map(CompletableFuture::join).toList();
            assertThat(redirects).filteredOn("/demo"::equals).hasSize(20);
            assertThat(redirects).filteredOn("/?status=busy"::equals).hasSize(10);
            assertThat(workspaceRepository.count()).isEqualTo(20);
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void cleanupRemovesExpiredWorkspaceAndEverySyntheticRow() {
        DemoWorkspace expired = workspaceRepository.saveAndFlush(DemoWorkspace.active(
                Instant.now().minus(Duration.ofHours(2)),
                Duration.ofMinutes(1)));
        requestRepository.saveAndFlush(PurchaseRequest.draft(
                expired.getId(),
                LAPTOP_REQUEST,
                "expired-fingerprint",
                "개발용 노트북 구매",
                "개발 환경 개선",
                new BigDecimal("2500000"),
                "KRW",
                PurchaseCategory.IT_EQUIPMENT,
                Set.of("POL-IT-001"),
                "requester-expired",
                "expired-draft-key",
                Instant.now().minus(Duration.ofHours(2))));

        assertThat(workspaceService.cleanupExpired()).isEqualTo(1);

        assertThat(workspaceRepository.findById(expired.getId())).isEmpty();
        assertThat(requestRepository.countByWorkspaceId(expired.getId())).isZero();
        assertThat(orderRepository.countByWorkspaceId(expired.getId())).isZero();
        assertThat(auditRepository.countByWorkspaceId(expired.getId())).isZero();
    }

    private BrowserSession startDemo() throws Exception {
        CsrfContext csrf = landingCsrf();
        MvcResult result = mvc.perform(post("/demo/sessions")
                        .cookie(csrf.cookie())
                        .param("_csrf", csrf.token()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/demo"))
                .andReturn();
        MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);
        BrowserSession browser = new BrowserSession(session, csrf.cookie(), csrf.token());
        assertThat(principal(browser).persona()).isEqualTo(DemoPersona.REQUESTER);
        return browser;
    }

    private void createDraft(BrowserSession browser, String requestText) throws Exception {
        mvc.perform(postWithCsrf("/demo/drafts", browser)
                        .param("requestText", requestText))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/demo"));
    }

    private void switchPersona(BrowserSession browser, DemoPersona persona) throws Exception {
        mvc.perform(postWithCsrf("/demo/personas", browser)
                        .param("persona", persona.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/demo"));
        assertThat(principal(browser).persona()).isEqualTo(persona);
    }

    private CsrfContext landingCsrf() throws Exception {
        MvcResult landing = mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andReturn();
        Cookie cookie = landing.getResponse().getCookie("XSRF-TOKEN");
        String token = CSRF_TOKEN_PATTERN
                .matcher(landing.getResponse().getContentAsString())
                .results()
                .map(result -> result.group(1))
                .findFirst()
                .orElseThrow();
        assertThat(cookie).isNotNull();
        assertThat(landing.getRequest().getSession(false)).isNull();
        return new CsrfContext(cookie, token);
    }

    private MockHttpServletRequestBuilder postWithCsrf(
            String path,
            BrowserSession browser,
            Object... uriVariables) {
        return post(path, uriVariables)
                .session(browser.session())
                .cookie(browser.csrfCookie())
                .param("_csrf", browser.csrfToken());
    }

    private DemoPrincipal principal(BrowserSession browser) {
        SecurityContext context = (SecurityContext) browser.session().getAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        assertThat(context).isNotNull();
        return (DemoPrincipal) context.getAuthentication().getPrincipal();
    }

    private record CsrfContext(Cookie cookie, String token) {
    }

    private record BrowserSession(MockHttpSession session, Cookie csrfCookie, String csrfToken) {
    }
}
