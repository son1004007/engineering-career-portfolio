package io.github.son1004007.opsmate.infrastructure.llm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.son1004007.opsmate.agent.DraftPrompt;
import io.github.son1004007.opsmate.agent.PolicyEvidence;
import io.github.son1004007.opsmate.domain.ErrorCode;
import io.github.son1004007.opsmate.domain.OpsMateException;
import io.github.son1004007.opsmate.domain.PurchaseCategory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class OllamaLocalLlmGatewayTest {

    private static final String BASE_URL = "http://127.0.0.1:11434";

    private ObjectMapper objectMapper;
    private MockRestServiceServer server;
    private OllamaLocalLlmGateway gateway;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE_URL);
        server = MockRestServiceServer.bindTo(builder).build();
        gateway = new OllamaLocalLlmGateway(builder.build(), objectMapper, "local-test-model");
    }

    @AfterEach
    void verifyServer() {
        server.verify();
    }

    @Test
    void postsOnlyToFixedChatPathAndAcceptsValidStructuredResponse() throws Exception {
        expectJsonResponse(validProposalJson());

        var proposal = gateway.propose(prompt());

        assertThat(proposal.title()).isEqualTo("개발용 노트북 구매");
        assertThat(proposal.amount()).isEqualByComparingTo("2500000");
        assertThat(proposal.category()).isEqualTo(PurchaseCategory.IT_EQUIPMENT);
        assertThat(proposal.policyIds()).containsExactly("POL-IT-001");
    }

    @Test
    void server5xxIsNormalizedAsModelUnavailable() {
        server.expect(once(), requestTo(BASE_URL + "/api/chat"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        assertError(ErrorCode.MODEL_UNAVAILABLE, () -> gateway.propose(prompt()));
    }

    @Test
    void emptyContentIsRejected() throws Exception {
        expectJsonResponse("");

        assertError(ErrorCode.INVALID_MODEL_OUTPUT, () -> gateway.propose(prompt()));
    }

    @Test
    void malformedJsonContentIsRejected() throws Exception {
        expectJsonResponse("{not-json");

        assertError(ErrorCode.INVALID_MODEL_OUTPUT, () -> gateway.propose(prompt()));
    }

    @Test
    void unknownOutputFieldIsRejectedEvenWhenSharedMapperIsPermissive() throws Exception {
        Map<String, Object> proposal = new LinkedHashMap<>();
        proposal.put("title", "개발용 노트북 구매");
        proposal.put("purpose", "개발 환경 개선");
        proposal.put("amount", 2500000);
        proposal.put("currency", "KRW");
        proposal.put("category", "IT_EQUIPMENT");
        proposal.put("policyIds", List.of("POL-IT-001"));
        proposal.put("arbitraryTool", "database.write");
        expectJsonResponse(objectMapper.writeValueAsString(proposal));

        assertError(ErrorCode.INVALID_MODEL_OUTPUT, () -> gateway.propose(prompt()));
    }

    private void expectJsonResponse(String content) throws Exception {
        String response = objectMapper.writeValueAsString(Map.of("message", Map.of("content", content)));
        server.expect(once(), requestTo(BASE_URL + "/api/chat"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.model").value("local-test-model"))
                .andExpect(jsonPath("$.stream").value(false))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
    }

    private String validProposalJson() throws Exception {
        return objectMapper.writeValueAsString(Map.of(
                "title", "개발용 노트북 구매",
                "purpose", "개발 환경 개선",
                "amount", 2500000,
                "currency", "KRW",
                "category", "IT_EQUIPMENT",
                "policyIds", List.of("POL-IT-001")));
    }

    private DraftPrompt prompt() {
        return new DraftPrompt(
                "개발용 노트북 1대를 구매합니다.",
                List.of(new PolicyEvidence(
                        "POL-IT-001",
                        "IT 장비 구매 기준",
                        "승인 후 발주",
                        new BigDecimal("5000000"),
                        Set.of(PurchaseCategory.IT_EQUIPMENT))));
    }

    private void assertError(ErrorCode expected, ThrowingCall call) {
        assertThatThrownBy(call::run)
                .isInstanceOf(OpsMateException.class)
                .satisfies(exception -> assertThat(((OpsMateException) exception).getCode()).isEqualTo(expected));
    }

    @FunctionalInterface
    private interface ThrowingCall {
        void run();
    }
}
