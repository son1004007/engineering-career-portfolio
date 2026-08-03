package com.songisuk.portfolio.authbridge;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.songisuk.portfolio.authbridge.web.SessionCookieCsrfTokenRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.security.web.csrf.CsrfTokenRepository;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.demo.analyst-password=test-only-analyst-password-01",
        "app.demo.admin-password=test-only-admin-password-01",
        "app.demo.user-password=test-only-user-password-01",
        "app.demo.disabled-password=test-only-disabled-password-01",
        "app.sso.shared-secret=test-only-sso-shared-secret-that-is-long-enough-01"
})
@AutoConfigureMockMvc
class AuthenticationFlowTest {

    private static final String SSO_SECRET = "test-only-sso-shared-secret-that-is-long-enough-01";
    private static final String SSO_ISSUER = "portfolio-demo-idp";
    private static final String SSO_AUDIENCE = "spring-security-auth-bridge";
    private static final String SSO_KEY_ID = "demo-key-v1";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CsrfTokenRepository configuredCsrfTokenRepository;

    @Test
    void databaseLoginCreatesSessionAndAllowsRoleProtectedRead() throws Exception {
        MockHttpSession session = databaseLogin("analyst", "test-only-analyst-password-01")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("analyst"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_ANALYST"))
                .andExpect(jsonPath("$.roles[1]").value("ROLE_USER"))
                .andReturn()
                .getRequest()
                .getSession(false) instanceof MockHttpSession found ? found : null;

        assertThat(session).isNotNull();
        mockMvc.perform(get("/api/reports/monthly").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("READY"));
    }

    @Test
    void databaseLoginRejectsWrongPasswordWithoutRevealingAccountDetails() throws Exception {
        databaseLogin("analyst", "wrong-password-value")
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_FAILED"))
                .andExpect(jsonPath("$.message").value("Credentials or SSO assertion could not be verified"));
    }

    @Test
    void disabledDatabaseAccountIsRejected() throws Exception {
        databaseLogin("disabled", "test-only-disabled-password-01")
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_FAILED"));
    }

    @Test
    void loginRequiresCsrfProtection() throws Exception {
        mockMvc.perform(post("/auth/db")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "analyst",
                                "password", "test-only-analyst-password-01"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("INVALID_CSRF"));
    }

    @Test
    void successfulLoginRotatesAnExistingSessionId() throws Exception {
        MockHttpSession existingSession = new MockHttpSession();
        CsrfExchange csrf = fetchCsrf(existingSession);
        String beforeLogin = csrf.session().getId();

        MvcResult result = mockMvc.perform(post("/auth/db")
                        .session(csrf.session())
                        .cookie(csrf.cookie())
                        .header(csrf.headerName(), csrf.token())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "analyst",
                                "password", "test-only-analyst-password-01"))))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getRequest().getSession(false)).isNotNull();
        assertThat(result.getRequest().getSession(false).getId()).isNotEqualTo(beforeLogin);
    }

    @Test
    void unauthenticatedRequestReturnsJson401() throws Exception {
        mockMvc.perform(get("/api/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHENTICATED"));
    }

    @Test
    void analystCannotCallAdminApi() throws Exception {
        MockHttpSession session = session(databaseLogin("analyst", "test-only-analyst-password-01")
                .andExpect(status().isOk())
                .andReturn());

        postWithCsrf("/api/admin/reindex", session)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    void adminWriteRequiresCsrfAndSucceedsWithValidToken() throws Exception {
        MockHttpSession session = session(databaseLogin("admin", "test-only-admin-password-01")
                .andExpect(status().isOk())
                .andReturn());
        CsrfExchange csrf = fetchCsrf(session);

        mockMvc.perform(post("/api/admin/reindex").session(session))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("INVALID_CSRF"));

        mockMvc.perform(post("/api/admin/reindex")
                        .session(session)
                        .cookie(csrf.cookie())
                        .header(csrf.headerName(), csrf.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }

    @Test
    void plainUserCannotReadReportsButAdminCan() throws Exception {
        MockHttpSession userSession = session(databaseLogin("user", "test-only-user-password-01")
                .andExpect(status().isOk())
                .andReturn());
        MockHttpSession adminSession = session(databaseLogin("admin", "test-only-admin-password-01")
                .andExpect(status().isOk())
                .andReturn());

        mockMvc.perform(get("/api/reports/monthly").session(userSession))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
        mockMvc.perform(get("/api/reports/monthly").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("READY"));
    }

    @Test
    void csrfEndpointTokenIsRotatedOnLoginAndTheOldTokenIsRejected() throws Exception {
        CsrfExchange beforeLogin = fetchCsrf(null);
        String sessionIdBeforeLogin = beforeLogin.session().getId();

        MvcResult login = mockMvc.perform(post("/auth/db")
                        .session(beforeLogin.session())
                        .cookie(beforeLogin.cookie())
                        .header(beforeLogin.headerName(), beforeLogin.token())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "admin",
                                "password", "test-only-admin-password-01"))))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession authenticatedSession = session(login);
        assertThat(authenticatedSession.getId()).isNotEqualTo(sessionIdBeforeLogin);
        Cookie clearedCookie = login.getResponse().getCookie(SessionCookieCsrfTokenRepository.COOKIE_NAME);
        assertThat(clearedCookie).isNotNull();
        assertThat(clearedCookie.getMaxAge()).isZero();

        CsrfExchange afterLogin = fetchCsrf(authenticatedSession);
        assertThat(afterLogin.token()).isNotEqualTo(beforeLogin.token());
        assertThat(afterLogin.cookie().getValue()).isNotEqualTo(beforeLogin.cookie().getValue());

        mockMvc.perform(post("/api/admin/reindex")
                        .session(authenticatedSession)
                        .cookie(beforeLogin.cookie())
                        .header(beforeLogin.headerName(), beforeLogin.token()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("INVALID_CSRF"));

        mockMvc.perform(post("/api/admin/reindex")
                        .session(authenticatedSession)
                        .cookie(afterLogin.cookie())
                        .header(afterLogin.headerName(), afterLogin.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }

    @Test
    void validSsoAssertionCreatesTheSameLocalRoleModel() throws Exception {
        long issuedAt = Instant.now().getEpochSecond();
        String nonce = nonce();
        MvcResult login = ssoLogin("sso-analyst-001", issuedAt, nonce,
                        signature("sso-analyst-001", issuedAt, nonce), null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("analyst"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_ANALYST"))
                .andExpect(jsonPath("$.roles[1]").value("ROLE_USER"))
                .andReturn();

        mockMvc.perform(get("/api/reports/monthly").session(session(login)))
                .andExpect(status().isOk());
    }

    @Test
    void ssoRequestRejectsAnUnexpectedRoleField() throws Exception {
        long issuedAt = Instant.now().getEpochSecond();
        String nonce = nonce();
        ssoLogin("sso-analyst-001", issuedAt, nonce,
                        signature("sso-analyst-001", issuedAt, nonce), new String[]{"ADMIN"})
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @Test
    void ssoRejectsInvalidSignatureExpiredAssertionUnknownAndDisabledAccounts() throws Exception {
        long now = Instant.now().getEpochSecond();

        ssoLogin("sso-analyst-001", now, nonce(),
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_FAILED"));

        String expiredNonce = nonce();
        ssoLogin("sso-analyst-001", now - 180, expiredNonce,
                        signature("sso-analyst-001", now - 180, expiredNonce), null)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_FAILED"));

        String unknownNonce = nonce();
        ssoLogin("sso-unknown-001", now, unknownNonce,
                        signature("sso-unknown-001", now, unknownNonce), null)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_FAILED"));

        String disabledNonce = nonce();
        ssoLogin("sso-disabled-001", now, disabledNonce,
                        signature("sso-disabled-001", now, disabledNonce), null)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_FAILED"));
    }

    @Test
    void ssoNonceCannotBeReplayed() throws Exception {
        long issuedAt = Instant.now().getEpochSecond();
        String nonce = nonce();
        String signature = signature("sso-admin-001", issuedAt, nonce);

        ssoLogin("sso-admin-001", issuedAt, nonce, signature, null)
                .andExpect(status().isOk());
        ssoLogin("sso-admin-001", issuedAt, nonce, signature, null)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_FAILED"));
    }

    @Test
    void logoutRequiresCsrfAndInvalidatesTheSession() throws Exception {
        MockHttpSession session = session(databaseLogin("admin", "test-only-admin-password-01")
                .andExpect(status().isOk())
                .andReturn());
        CsrfExchange csrf = fetchCsrf(session);

        mockMvc.perform(post("/auth/logout").session(session))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("INVALID_CSRF"));

        mockMvc.perform(get("/api/me").session(session))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/logout")
                        .session(session)
                        .cookie(csrf.cookie())
                        .header(csrf.headerName(), csrf.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("LOGGED_OUT"));

        assertThat(session.isInvalid()).isTrue();
    }

    @Test
    void anonymousLogoutIsRejectedEvenWithAValidCsrfToken() throws Exception {
        CsrfExchange anonymous = fetchCsrf(null);

        mockMvc.perform(post("/auth/logout")
                        .session(anonymous.session())
                        .cookie(anonymous.cookie())
                        .header(anonymous.headerName(), anonymous.token()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHENTICATED"));
    }

    private org.springframework.test.web.servlet.ResultActions databaseLogin(String username, String password)
            throws Exception {
        CsrfExchange csrf = fetchCsrf(null);
        return mockMvc.perform(post("/auth/db")
                .session(csrf.session())
                .cookie(csrf.cookie())
                .header(csrf.headerName(), csrf.token())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("username", username, "password", password))));
    }

    private org.springframework.test.web.servlet.ResultActions ssoLogin(
            String subject,
            long issuedAt,
            String nonce,
            String signature,
            String[] requestedRoles
    ) throws Exception {
        CsrfExchange csrf = fetchCsrf(null);
        var body = objectMapper.createObjectNode()
                .put("issuer", SSO_ISSUER)
                .put("audience", SSO_AUDIENCE)
                .put("keyId", SSO_KEY_ID)
                .put("subject", subject)
                .put("issuedAtEpochSeconds", issuedAt)
                .put("nonce", nonce)
                .put("signature", signature);
        if (requestedRoles != null) {
            body.putArray("roles").add(requestedRoles[0]);
        }
        return mockMvc.perform(post("/auth/sso")
                .session(csrf.session())
                .cookie(csrf.cookie())
                .header(csrf.headerName(), csrf.token())
                .contentType(APPLICATION_JSON)
                .content(body.toString()));
    }

    private org.springframework.test.web.servlet.ResultActions postWithCsrf(
            String path,
            MockHttpSession session
    ) throws Exception {
        CsrfExchange csrf = fetchCsrf(session);
        return mockMvc.perform(post(path)
                .session(session)
                .cookie(csrf.cookie())
                .header(csrf.headerName(), csrf.token()));
    }

    private static MockHttpSession session(MvcResult result) {
        return (MockHttpSession) result.getRequest().getSession(false);
    }

    private static String nonce() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private static String signature(String subject, long issuedAt, String nonce) throws Exception {
        String canonical = SSO_ISSUER + "\n" + SSO_AUDIENCE + "\n" + SSO_KEY_ID + "\n"
                + subject + "\n" + issuedAt + "\n" + nonce;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(SSO_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(mac.doFinal(canonical.getBytes(StandardCharsets.UTF_8)));
    }

    private CsrfExchange fetchCsrf(MockHttpSession existingSession) throws Exception {
        assertThat(configuredCsrfTokenRepository).isInstanceOf(SessionCookieCsrfTokenRepository.class);
        var request = get("/auth/csrf");
        if (existingSession != null) {
            request.session(existingSession);
        }
        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.headerName").value(SessionCookieCsrfTokenRepository.HEADER_NAME))
                .andReturn();

        String token = objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
        Cookie cookie = result.getResponse().getCookie(SessionCookieCsrfTokenRepository.COOKIE_NAME);
        assertThat(token).isNotBlank();
        assertThat(cookie).isNotNull();
        assertThat(cookie.getValue()).isNotBlank();
        return new CsrfExchange(session(result), cookie,
                SessionCookieCsrfTokenRepository.HEADER_NAME, token);
    }

    private record CsrfExchange(
            MockHttpSession session,
            Cookie cookie,
            String headerName,
            String token
    ) {
    }
}
