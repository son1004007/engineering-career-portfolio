package io.github.son1004007.opsmate.infrastructure.llm;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import io.github.son1004007.opsmate.agent.DraftPrompt;
import io.github.son1004007.opsmate.agent.PolicyEvidence;
import io.github.son1004007.opsmate.domain.PurchaseCategory;
import io.github.son1004007.opsmate.domain.ErrorCode;
import io.github.son1004007.opsmate.domain.OpsMateException;
import org.junit.jupiter.api.Test;

class LlmGatewayConfigurationTest {

    @Test
    void rejectsBaseUrlWhoseHostIsNotExplicitlyAllowed() {
        LlmProperties properties = new LlmProperties();
        properties.setBaseUrl("https://model-host.example.invalid:11434");
        properties.setAllowedHosts(Set.of("127.0.0.1", "localhost"));

        assertThatThrownBy(() -> new LlmGatewayConfiguration().validateBaseUri(properties))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not in OPSMATE_LLM_ALLOWED_HOSTS");
    }

    @Test
    void rejectsUnsafeOutputLimitsBeforeCreatingTheHttpClient() {
        LlmProperties properties = new LlmProperties();
        properties.setBaseUrl("http://127.0.0.1:11434");
        properties.setAllowedHosts(Set.of("127.0.0.1"));
        properties.setModel("local-test-model");
        properties.setMaxResponseBytes(10_000_000);

        assertThatThrownBy(() -> new LlmGatewayConfiguration().ollamaLocalLlmGateway(
                properties, new ObjectMapper()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("safe range");
    }

    @Test
    void sendsConfiguredBearerTokenOnlyFromServerSideConfiguration() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        AtomicReference<String> authorization = new AtomicReference<>();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/api/chat", exchange -> {
            authorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
            exchange.getRequestBody().readAllBytes();
            String proposal = objectMapper.writeValueAsString(Map.of(
                    "title", "개발용 노트북 구매",
                    "purpose", "개발 환경 개선",
                    "amount", 2500000,
                    "currency", "KRW",
                    "category", "IT_EQUIPMENT",
                    "policyIds", List.of("POL-IT-001")));
            byte[] body = objectMapper.writeValueAsBytes(Map.of("message", Map.of("content", proposal)));
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
        try {
            LlmProperties properties = new LlmProperties();
            properties.setBaseUrl("http://127.0.0.1:" + server.getAddress().getPort());
            properties.setAllowedHosts(Set.of("127.0.0.1"));
            properties.setModel("local-test-model");
            properties.setAuthToken("synthetic-proxy-token");

            var gateway = new LlmGatewayConfiguration().ollamaLocalLlmGateway(properties, objectMapper);
            gateway.propose(new DraftPrompt(
                    "개발용 노트북 1대 구매",
                    List.of(new PolicyEvidence(
                            "POL-IT-001",
                            "IT 장비 구매 기준",
                            "승인 후 발주",
                            new BigDecimal("5000000"),
                            Set.of(PurchaseCategory.IT_EQUIPMENT)))));

            assertThat(authorization).hasValue("Bearer synthetic-proxy-token");
        } finally {
            server.stop(0);
        }
    }

    @Test
    void readTimeoutFailsClosedAsModelUnavailable() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/api/chat", exchange -> {
            try {
                Thread.sleep(200);
                exchange.sendResponseHeaders(503, -1);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            } finally {
                exchange.close();
            }
        });
        server.start();
        try {
            LlmProperties properties = new LlmProperties();
            properties.setBaseUrl("http://127.0.0.1:" + server.getAddress().getPort());
            properties.setAllowedHosts(Set.of("127.0.0.1"));
            properties.setModel("local-timeout-model");
            properties.setReadTimeout(Duration.ofMillis(30));

            var gateway = new LlmGatewayConfiguration().ollamaLocalLlmGateway(properties, new ObjectMapper());

            assertThatThrownBy(() -> gateway.propose(new DraftPrompt("노트북 구매", List.of())))
                    .isInstanceOf(OpsMateException.class)
                    .satisfies(exception -> assertThat(((OpsMateException) exception).getCode())
                            .isEqualTo(ErrorCode.MODEL_UNAVAILABLE));
        } finally {
            server.stop(0);
        }
    }
}
