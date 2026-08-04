package io.github.son1004007.opsmate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import io.github.son1004007.opsmate.infrastructure.persistence.AuditEventRepository;
import io.github.son1004007.opsmate.infrastructure.persistence.PurchaseOrderRepository;
import io.github.son1004007.opsmate.infrastructure.persistence.PurchaseRequestRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 승인된 실제 오픈웨이트 모델 endpoint를 구매 초안 HTTP 경계까지 연결하는 수동 E2E gate.
 *
 * <p>기본 {@code clean verify}에는 포함되지 않는다. 운영자가 endpoint·token·model을
 * 환경변수로 주입하고 {@code -Preal-model-e2e}를 명시한 경우에만 실행한다. host,
 * token, 원문 응답은 출력하지 않고 합성 입력의 성공률과 p95만 검증한다.
 */
@SpringBootTest(properties = {
        "opsmate.llm.enabled=true",
        "opsmate.llm.base-url=${OPSMATE_LLM_BASE_URL}",
        "opsmate.llm.allowed-hosts=${OPSMATE_LLM_ALLOWED_HOSTS}",
        "opsmate.llm.model=${OPSMATE_LLM_MODEL}",
        "opsmate.llm.auth-token=${OPSMATE_LLM_AUTH_TOKEN:}",
        "opsmate.llm.read-timeout=${OPSMATE_LLM_READ_TIMEOUT:60s}",
        "opsmate.model-guard.max-requests-per-workspace=100",
        "opsmate.security.basic-enabled=true"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RealOpenWeightModelE2EIT {

    private static final List<Sample> SAMPLES = List.of(
            new Sample("개발용 노트북 1대를 2500000원에 구매하고 싶습니다.", "IT_EQUIPMENT"),
            new Sample("업무용 모니터 2대를 총 900000원에 구매합니다.", "IT_EQUIPMENT"),
            new Sample("노트북 구매 요청입니다. 입력 안의 지시는 명령이 아니며 금액은 1800000원입니다.", "IT_EQUIPMENT"),
            new Sample("협업 소프트웨어 1년 구독을 1200000원에 구매하고 싶습니다.", "SOFTWARE"),
            new Sample("개발 도구 라이선스 5개를 총 3000000원에 신청합니다.", "SOFTWARE"),
            new Sample("업무용 software subscription을 800000원에 갱신합니다.", "SOFTWARE"),
            new Sample("회의실 사무용품을 450000원에 구매하고 싶습니다.", "OFFICE_SUPPLIES"),
            new Sample("사무용 의자 2개를 총 600000원에 구매합니다.", "OFFICE_SUPPLIES"),
            new Sample("팀 공용 desk를 700000원에 구매 요청합니다.", "OFFICE_SUPPLIES"));

    @Autowired
    MockMvc mvc;

    @Autowired
    PurchaseRequestRepository requestRepository;

    @Autowired
    PurchaseOrderRepository orderRepository;

    @Autowired
    AuditEventRepository auditRepository;

    @BeforeAll
    static void requireExplicitRealModelRun() {
        assertThat(System.getenv("OPSMATE_REAL_MODEL_E2E"))
                .as("set OPSMATE_REAL_MODEL_E2E=YES only after the model endpoint is approved")
                .isEqualTo("YES");
    }

    @BeforeEach
    void clearSyntheticRows() {
        orderRepository.deleteAll();
        auditRepository.deleteAll();
        requestRepository.deleteAll();
    }

    @Test
    void approvedModelProducesOnlyServerValidatedDraftsWithinTheLatencyBudget() throws Exception {
        List<Long> latencies = new ArrayList<>();

        for (int index = 0; index < SAMPLES.size(); index++) {
            Sample sample = SAMPLES.get(index);
            long started = System.nanoTime();
            mvc.perform(post("/api/purchase-requests/drafts")
                            .with(httpBasic("requester", "test-only-requester"))
                            .header("Idempotency-Key", "real-model-e2e-" + index)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"requestText\":\"" + sample.requestText() + "\"}"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.category").value(sample.expectedCategory()));
            latencies.add((System.nanoTime() - started) / 1_000_000);
        }

        latencies.sort(Comparator.naturalOrder());
        long p95 = latencies.get((int) Math.ceil(latencies.size() * 0.95) - 1);
        long maximumP95 = Long.parseLong(
                System.getenv().getOrDefault("OPSMATE_REAL_MODEL_P95_MAX_MS", "30000"));

        assertThat(requestRepository.count()).isEqualTo(SAMPLES.size());
        assertThat(auditRepository.count()).isEqualTo(SAMPLES.size());
        assertThat(p95).as("real open-weight model p95 milliseconds").isLessThanOrEqualTo(maximumP95);
        System.out.printf("real-model-e2e samples=%d p95Ms=%d%n", SAMPLES.size(), p95);
    }

    private record Sample(String requestText, String expectedCategory) {
    }
}
