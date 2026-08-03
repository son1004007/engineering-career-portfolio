package io.github.son1004007.opsmate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.son1004007.opsmate.OpsMateTestConfiguration.ControllableOrderPostPersistHook;
import io.github.son1004007.opsmate.OpsMateTestConfiguration.StubLocalLlmGateway;
import io.github.son1004007.opsmate.agent.DraftProposal;
import io.github.son1004007.opsmate.domain.PurchaseCategory;
import io.github.son1004007.opsmate.domain.PurchaseRequestStatus;
import io.github.son1004007.opsmate.infrastructure.persistence.AuditEventRepository;
import io.github.son1004007.opsmate.infrastructure.persistence.PurchaseOrderRepository;
import io.github.son1004007.opsmate.infrastructure.persistence.PurchaseRequestRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.RollbackException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(OpsMateTestConfiguration.class)
class OpsMateIntegrationTest {

    private static final String LAPTOP_REQUEST = "개발용 노트북 1대를 2500000원에 구매하고 싶습니다.";

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PurchaseRequestRepository requestRepository;

    @Autowired
    PurchaseOrderRepository orderRepository;

    @Autowired
    AuditEventRepository auditRepository;

    @Autowired
    StubLocalLlmGateway llmGateway;

    @Autowired
    ControllableOrderPostPersistHook orderHook;

    @Autowired
    EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void resetState() {
        orderHook.reset();
        llmGateway.reset();
        auditRepository.deleteAll();
        orderRepository.deleteAll();
        requestRepository.deleteAll();
    }

    @Test
    void normalFlowRequiresHumanApprovalBeforeOneOrderAndWritesAuditEvents() throws Exception {
        UUID requestId = createDraft("draft-normal-001", LAPTOP_REQUEST);

        mvc.perform(post("/api/purchase-requests/{id}/submit", requestId)
                        .with(user("requester").roles("REQUESTER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING_APPROVAL"));

        mvc.perform(post("/api/purchase-requests/{id}/decisions", requestId)
                        .with(user("approver").roles("APPROVER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"decision\":\"APPROVE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.decidedBy").value("approver"));

        UUID orderId = createOrder(requestId, "order-normal-001");

        assertThat(orderId).isNotNull();
        assertThat(orderRepository.count()).isEqualTo(1);
        assertThat(requestRepository.findById(requestId).orElseThrow().getStatus())
                .isEqualTo(PurchaseRequestStatus.ORDERED);
        assertThat(auditRepository.findAllByOrderByOccurredAtAsc())
                .extracting("action")
                .containsExactly("DRAFT_CREATED", "SUBMITTED", "APPROVED", "ORDER_CREATED");

        mvc.perform(get("/api/audit-events").with(user("auditor").roles("AUDITOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[3].action").value("ORDER_CREATED"));
    }

    @Test
    void readPolicyUsesOwnerRoleAndWorkflowStateAndMasksObjectProbes() throws Exception {
        UUID requestId = createDraft("draft-read-policy-001", LAPTOP_REQUEST);

        mvc.perform(get("/api/purchase-requests/{id}", requestId)
                        .with(user("requester").roles("REQUESTER")))
                .andExpect(status().isOk());
        mvc.perform(get("/api/purchase-requests/{id}", requestId)
                        .with(user("other-requester").roles("REQUESTER")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED_ACTION"));
        mvc.perform(get("/api/purchase-requests/{id}", UUID.randomUUID())
                        .with(user("other-requester").roles("REQUESTER")))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/purchase-requests/{id}", requestId)
                        .with(user("approver").roles("APPROVER")))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/purchase-requests/{id}", requestId)
                        .with(user("auditor").roles("AUDITOR")))
                .andExpect(status().isOk());

        mvc.perform(post("/api/purchase-requests/{id}/submit", requestId)
                        .with(user("requester").roles("REQUESTER")))
                .andExpect(status().isOk());
        mvc.perform(get("/api/purchase-requests/{id}", requestId)
                        .with(user("approver").roles("APPROVER")))
                .andExpect(status().isOk());
        mvc.perform(get("/api/purchase-requests/{id}", requestId)
                        .with(user("buyer").roles("BUYER")))
                .andExpect(status().isForbidden());

        mvc.perform(post("/api/purchase-requests/{id}/decisions", requestId)
                        .with(user("approver").roles("APPROVER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"decision\":\"APPROVE\"}"))
                .andExpect(status().isOk());
        mvc.perform(get("/api/purchase-requests/{id}", requestId)
                        .with(user("approver").roles("APPROVER")))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/purchase-requests/{id}", requestId)
                        .with(user("buyer").roles("BUYER")))
                .andExpect(status().isOk());

        mvc.perform(get("/api/purchase-requests/{id}", UUID.randomUUID())
                        .with(user("auditor").roles("AUDITOR")))
                .andExpect(status().isNotFound());
    }

    @Test
    void wrongRoleCannotCreateDraft() throws Exception {
        mvc.perform(post("/api/purchase-requests/drafts")
                        .with(user("buyer").roles("BUYER"))
                        .header("Idempotency-Key", "draft-denied-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("requestText", LAPTOP_REQUEST))))
                .andExpect(status().isForbidden());

        assertThat(requestRepository.count()).isZero();
        assertThat(auditRepository.count()).isZero();
    }

    @Test
    void modelOutageFailsClosedWithoutPersistingDraft() throws Exception {
        llmGateway.failModel();

        mvc.perform(post("/api/purchase-requests/drafts")
                        .with(user("requester").roles("REQUESTER"))
                        .header("Idempotency-Key", "draft-outage-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("requestText", LAPTOP_REQUEST))))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("MODEL_UNAVAILABLE"));

        assertThat(requestRepository.count()).isZero();
        assertThat(orderRepository.count()).isZero();
        assertThat(auditRepository.count()).isZero();
    }

    @Test
    void duplicateOrderRetryIsIdempotentAndDifferentKeyIsBlocked() throws Exception {
        UUID requestId = createApprovedRequest("draft-duplicate-001");

        UUID firstOrderId = createOrder(requestId, "order-duplicate-001");
        UUID replayOrderId = createOrder(requestId, "order-duplicate-001");

        assertThat(replayOrderId).isEqualTo(firstOrderId);
        assertThat(orderRepository.count()).isEqualTo(1);
        assertThat(auditRepository.countByAction("ORDER_CREATED")).isEqualTo(1);

        mvc.perform(post("/api/purchase-orders")
                        .with(user("buyer").roles("BUYER"))
                        .header("Idempotency-Key", "order-duplicate-002")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("purchaseRequestId", requestId))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_ORDER"));

        assertThat(orderRepository.count()).isEqualTo(1);
    }

    @Test
    void postPersistFailureRollsBackOrderStateAndSuccessAudit() throws Exception {
        UUID requestId = createApprovedRequest("draft-rollback-001");
        orderHook.failNext();

        mvc.perform(post("/api/purchase-orders")
                        .with(user("buyer").roles("BUYER"))
                        .header("Idempotency-Key", "order-rollback-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("purchaseRequestId", requestId))))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("ORDER_FINALIZATION_FAILED"));

        assertThat(orderRepository.count()).isZero();
        assertThat(requestRepository.findById(requestId).orElseThrow().getStatus())
                .isEqualTo(PurchaseRequestStatus.APPROVED);
        assertThat(auditRepository.countByAction("ORDER_CREATED")).isZero();
        assertThat(auditRepository.count()).isEqualTo(3);
    }

    @Test
    void staleJpaVersionIsRejectedDeterministically() throws Exception {
        UUID requestId = createDraft("draft-optimistic-001", LAPTOP_REQUEST);
        EntityManager first = entityManagerFactory.createEntityManager();
        EntityManager stale = entityManagerFactory.createEntityManager();
        try {
            first.getTransaction().begin();
            stale.getTransaction().begin();
            var firstCopy = first.find(io.github.son1004007.opsmate.domain.PurchaseRequest.class, requestId);
            var staleCopy = stale.find(io.github.son1004007.opsmate.domain.PurchaseRequest.class, requestId);
            firstCopy.submit("requester", Instant.parse("2026-08-03T00:00:00Z"));
            staleCopy.submit("requester", Instant.parse("2026-08-03T00:00:01Z"));

            first.getTransaction().commit();
            assertThatThrownBy(stale.getTransaction()::commit)
                    .isInstanceOf(RollbackException.class)
                    .hasCauseInstanceOf(OptimisticLockException.class)
                    .hasRootCauseInstanceOf(org.hibernate.StaleObjectStateException.class);
        } finally {
            if (first.getTransaction().isActive()) {
                first.getTransaction().rollback();
            }
            if (stale.getTransaction().isActive()) {
                stale.getTransaction().rollback();
            }
            first.close();
            stale.close();
        }

        assertThat(requestRepository.findById(requestId).orElseThrow().getStatus())
                .isEqualTo(PurchaseRequestStatus.PENDING_APPROVAL);
    }

    @Test
    void rejectedRequestCannotBeOrdered() throws Exception {
        UUID requestId = createDraft("draft-reject-001", LAPTOP_REQUEST);
        mvc.perform(post("/api/purchase-requests/{id}/submit", requestId)
                        .with(user("requester").roles("REQUESTER")))
                .andExpect(status().isOk());
        mvc.perform(post("/api/purchase-requests/{id}/decisions", requestId)
                        .with(user("approver").roles("APPROVER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"decision\":\"REJECT\",\"reason\":\"예산 재검토 필요\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(jsonPath("$.decidedBy").value("approver"));

        mvc.perform(post("/api/purchase-orders")
                        .with(user("buyer").roles("BUYER"))
                        .header("Idempotency-Key", "order-rejected-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("purchaseRequestId", requestId))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INVALID_STATE"));

        assertThat(orderRepository.count()).isZero();
    }

    @Test
    void modelCannotCitePolicyThatWasNotRetrieved() throws Exception {
        llmGateway.returnProposal(new DraftProposal(
                "개발용 노트북 구매",
                "개발 환경 개선",
                new BigDecimal("2500000"),
                "KRW",
                PurchaseCategory.IT_EQUIPMENT,
                List.of("POL-FABRICATED-999")));

        mvc.perform(post("/api/purchase-requests/drafts")
                        .with(user("requester").roles("REQUESTER"))
                        .header("Idempotency-Key", "draft-invalid-policy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("requestText", LAPTOP_REQUEST))))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code").value("INVALID_MODEL_OUTPUT"));

        assertThat(requestRepository.count()).isZero();
    }

    @Test
    void draftIdempotencyKeyCannotBeReusedWithDifferentInput() throws Exception {
        createDraft("draft-idempotent-001", LAPTOP_REQUEST);

        mvc.perform(post("/api/purchase-requests/drafts")
                        .with(user("requester").roles("REQUESTER"))
                        .header("Idempotency-Key", "draft-idempotent-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("requestText", "노트북 2대를 구매하고 싶습니다."))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("IDEMPOTENCY_CONFLICT"));

        assertThat(requestRepository.count()).isEqualTo(1);
    }

    private UUID createApprovedRequest(String draftKey) throws Exception {
        UUID requestId = createDraft(draftKey, LAPTOP_REQUEST);
        mvc.perform(post("/api/purchase-requests/{id}/submit", requestId)
                        .with(user("requester").roles("REQUESTER")))
                .andExpect(status().isOk());
        mvc.perform(post("/api/purchase-requests/{id}/decisions", requestId)
                        .with(user("approver").roles("APPROVER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"decision\":\"APPROVE\"}"))
                .andExpect(status().isOk());
        return requestId;
    }

    private UUID createDraft(String key, String requestText) throws Exception {
        MvcResult result = mvc.perform(post("/api/purchase-requests/drafts")
                        .with(user("requester").roles("REQUESTER"))
                        .header("Idempotency-Key", key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("requestText", requestText))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.policyEvidenceIds[0]").value("POL-IT-001"))
                .andReturn();
        return responseId(result);
    }

    private UUID createOrder(UUID requestId, String key) throws Exception {
        MvcResult result = mvc.perform(post("/api/purchase-orders")
                        .with(user("buyer").roles("BUYER"))
                        .header("Idempotency-Key", key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("purchaseRequestId", requestId))))
                .andExpect(status().isCreated())
                .andReturn();
        return responseId(result);
    }

    private UUID responseId(MvcResult result) throws Exception {
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return UUID.fromString(body.path("id").asText());
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

}
