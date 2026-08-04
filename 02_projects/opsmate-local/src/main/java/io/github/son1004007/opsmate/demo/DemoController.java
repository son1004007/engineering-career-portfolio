package io.github.son1004007.opsmate.demo;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.List;
import java.util.UUID;

import io.github.son1004007.opsmate.application.ActorContext;
import io.github.son1004007.opsmate.application.ActorProvider;
import io.github.son1004007.opsmate.application.AuditQueryService;
import io.github.son1004007.opsmate.application.PurchaseOrderQueryService;
import io.github.son1004007.opsmate.application.PurchaseOrderService;
import io.github.son1004007.opsmate.application.PurchaseRequestQueryService;
import io.github.son1004007.opsmate.application.PurchaseRequestService;
import io.github.son1004007.opsmate.domain.ApprovalDecision;
import io.github.son1004007.opsmate.domain.ErrorCode;
import io.github.son1004007.opsmate.domain.OpsMateException;
import io.github.son1004007.opsmate.infrastructure.llm.LlmProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 하나의 격리된 workspace에서 네 persona의 합성 구매 흐름을 체험하게 하는 MVC 진입점.
 *
 * <p>Controller는 actor와 workspace를 form 값으로 받지 않는다. 서버 세션의
 * DemoPrincipal을 Spring Security 인증으로 복원한 뒤 기존 service method RBAC와
 * 도메인 상태 전이를 그대로 통과한다.
 */
@Controller
@ConditionalOnProperty(name = "opsmate.demo.enabled", havingValue = "true")
public class DemoController {

    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();
    private final DemoWorkspaceService workspaceService;
    private final DemoAdmissionCoordinator admissionCoordinator;
    private final PurchaseRequestService requestService;
    private final PurchaseRequestQueryService requestQueryService;
    private final PurchaseOrderService orderService;
    private final PurchaseOrderQueryService orderQueryService;
    private final AuditQueryService auditQueryService;
    private final ActorProvider actorProvider;
    private final LlmProperties llmProperties;

    public DemoController(
            DemoWorkspaceService workspaceService,
            DemoAdmissionCoordinator admissionCoordinator,
            PurchaseRequestService requestService,
            PurchaseRequestQueryService requestQueryService,
            PurchaseOrderService orderService,
            PurchaseOrderQueryService orderQueryService,
            AuditQueryService auditQueryService,
            ActorProvider actorProvider,
            LlmProperties llmProperties) {
        this.workspaceService = workspaceService;
        this.admissionCoordinator = admissionCoordinator;
        this.requestService = requestService;
        this.requestQueryService = requestQueryService;
        this.orderService = orderService;
        this.orderQueryService = orderQueryService;
        this.auditQueryService = auditQueryService;
        this.actorProvider = actorProvider;
        this.llmProperties = llmProperties;
    }

    @PostMapping("/demo/sessions")
    public synchronized String start(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        DemoPrincipal existing = session == null ? null : persistedPrincipal(session);
        if (existing != null) {
            try {
                // 같은 브라우저의 중복 제출은 새 workspace를 만들지 않고 기존 세션으로 수렴시킨다.
                workspaceService.requireActive(existing.workspaceId());
                authenticate(existing, request, response);
                return "redirect:/demo";
            } catch (OpsMateException exception) {
                if (exception.getCode() != ErrorCode.SESSION_EXPIRED) {
                    throw exception;
                }
                workspaceService.close(existing.workspaceId());
            }
        }
        DemoWorkspace workspace = admissionCoordinator.start();
        // admission이 성공한 뒤에만 server session을 만들어 거부된 익명 요청의 heap 점유를 막는다.
        request.getSession(true);
        request.changeSessionId();
        authenticate(new DemoPrincipal(workspace.getId(), DemoPersona.REQUESTER), request, response);
        return "redirect:/demo";
    }

    @GetMapping("/demo")
    public String dashboard(Model model) {
        ActorContext context = actorProvider.currentContext();
        DemoPrincipal principal = requireDemoPrincipal(context.authentication());
        DemoWorkspace workspace = workspaceService.requireActive(context.workspaceId());
        model.addAttribute("persona", principal.persona());
        model.addAttribute("personas", DemoPersona.values());
        model.addAttribute("workspaceExpiresAt", workspace.getExpiresAt());
        model.addAttribute("requests", requestQueryService.findVisible());
        model.addAttribute("orders", principal.persona() == DemoPersona.BUYER
                || principal.persona() == DemoPersona.AUDITOR ? orderQueryService.findVisible() : List.of());
        model.addAttribute("audits", principal.persona() == DemoPersona.AUDITOR
                ? auditQueryService.findAll() : List.of());
        model.addAttribute("modelEnabled", llmProperties.isEnabled());
        model.addAttribute("modelName", llmProperties.isEnabled() ? llmProperties.getModel() : "사용 불가");
        return "demo/dashboard";
    }

    @PostMapping("/demo/personas")
    public String switchPersona(
            @RequestParam DemoPersona persona,
            HttpServletRequest request,
            HttpServletResponse response) {
        ActorContext context = actorProvider.currentContext();
        workspaceService.requireActive(context.workspaceId());
        authenticate(new DemoPrincipal(context.workspaceId(), persona), request, response);
        return "redirect:/demo";
    }

    @PostMapping("/demo/drafts")
    @PreAuthorize("hasRole('REQUESTER')")
    public String createDraft(@RequestParam String requestText, RedirectAttributes redirectAttributes) {
        return execute(
                () -> requestService.createDraft(formIdempotencyKey("draft", requestText), requestText),
                "정책 근거가 연결된 구매 초안을 생성했습니다.",
                redirectAttributes);
    }

    @PostMapping("/demo/requests/{requestId}/submit")
    @PreAuthorize("hasRole('REQUESTER')")
    public String submit(@PathVariable UUID requestId, RedirectAttributes redirectAttributes) {
        return execute(
                () -> requestService.submit(requestId),
                "승인 대기 작업함으로 제출했습니다.",
                redirectAttributes);
    }

    @PostMapping("/demo/requests/{requestId}/decisions")
    @PreAuthorize("hasRole('APPROVER')")
    public String decide(
            @PathVariable UUID requestId,
            @RequestParam ApprovalDecision decision,
            @RequestParam(required = false) String reason,
            RedirectAttributes redirectAttributes) {
        return execute(
                () -> requestService.decide(requestId, decision, reason),
                decision == ApprovalDecision.APPROVE ? "구매 요청을 승인했습니다." : "구매 요청을 반려했습니다.",
                redirectAttributes);
    }

    @PostMapping("/demo/orders")
    @PreAuthorize("hasRole('BUYER')")
    public String createOrder(
            @RequestParam UUID purchaseRequestId,
            RedirectAttributes redirectAttributes) {
        return execute(
                () -> orderService.createOrder(
                        purchaseRequestId,
                        formIdempotencyKey("order", purchaseRequestId.toString())),
                "승인된 요청으로 합성 발주를 생성했습니다.",
                redirectAttributes);
    }

    @PostMapping("/demo/reset")
    public synchronized String reset(
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {
        ActorContext context = actorProvider.currentContext();
        DemoWorkspace replacement;
        try {
            replacement = admissionCoordinator.reset(context.workspaceId());
        } catch (OpsMateException exception) {
            redirectAttributes.addFlashAttribute("error", publicMessage(exception.getCode()));
            return "redirect:/demo";
        }
        request.changeSessionId();
        authenticate(new DemoPrincipal(replacement.getId(), DemoPersona.REQUESTER), request, response);
        redirectAttributes.addFlashAttribute("message", "합성 데이터를 삭제하고 새 데모를 시작했습니다.");
        return "redirect:/demo";
    }

    @PostMapping("/demo/end")
    public String end(HttpServletRequest request) {
        ActorContext context = actorProvider.currentContext();
        workspaceService.close(context.workspaceId());
        SecurityContextHolder.clearContext();
        request.getSession(false).invalidate();
        return "redirect:/";
    }

    private String execute(Action action, String successMessage, RedirectAttributes redirectAttributes) {
        try {
            action.run();
            redirectAttributes.addFlashAttribute("message", successMessage);
        } catch (OpsMateException exception) {
            redirectAttributes.addFlashAttribute("error", publicMessage(exception.getCode()));
        }
        return "redirect:/demo";
    }

    private String publicMessage(ErrorCode code) {
        return switch (code) {
            case MODEL_UNAVAILABLE -> "현재 오픈웨이트 모델을 사용할 수 없습니다. 다른 API로 우회하지 않습니다.";
            case INVALID_MODEL_OUTPUT -> "모델 응답이 안전한 구조 검증을 통과하지 못했습니다.";
            case RATE_LIMITED -> "공개 데모의 시간당 사용 한도에 도달했습니다. 잠시 후 다시 시도해 주세요.";
            case MODEL_BUSY -> "모델이 다른 요청을 처리 중입니다. 잠시 후 다시 시도해 주세요.";
            case SESSION_EXPIRED -> "데모가 만료되었습니다. 새로 시작해 주세요.";
            default -> "요청을 처리하지 않았습니다. 현재 역할과 업무 상태를 확인해 주세요.";
        };
    }

    private DemoPrincipal requireDemoPrincipal(Authentication authentication) {
        if (authentication.getPrincipal() instanceof DemoPrincipal principal) {
            return principal;
        }
        throw new OpsMateException(ErrorCode.UNAUTHORIZED_ACTION, "A demo session is required");
    }

    private DemoPrincipal persistedPrincipal(HttpSession session) {
        Object stored = session.getAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        if (stored instanceof SecurityContext context
                && context.getAuthentication() != null
                && context.getAuthentication().getPrincipal() instanceof DemoPrincipal principal) {
            return principal;
        }
        Authentication current = SecurityContextHolder.getContext().getAuthentication();
        return current != null && current.getPrincipal() instanceof DemoPrincipal principal ? principal : null;
    }

    private String formIdempotencyKey(String operation, String stableInput) {
        // name-based UUID는 암호 용도가 아니라 브라우저 재전송을 같은 업무 명령으로 묶는 식별자다.
        UUID value = UUID.nameUUIDFromBytes((operation + ":" + stableInput).getBytes(UTF_8));
        return operation + "-" + value;
    }

    private void authenticate(
            DemoPrincipal principal,
            HttpServletRequest request,
            HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authentication =
                UsernamePasswordAuthenticationToken.authenticated(principal, null, principal.authorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);
    }

    @FunctionalInterface
    private interface Action {
        void run();
    }
}
