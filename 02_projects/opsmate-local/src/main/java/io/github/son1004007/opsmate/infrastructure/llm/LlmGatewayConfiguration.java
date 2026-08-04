package io.github.son1004007.opsmate.infrastructure.llm;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.son1004007.opsmate.agent.LocalOpenWeightLlmGateway;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

/**
 * 로컬 오픈웨이트 모델 endpoint의 안전한 기본값과 허용 범위를 구성한다.
 *
 * <p>gateway는 명시적으로 활성화할 때만 생성된다. base URL은 scheme·host·port만
 * 허용하고 host allowlist를 통과해야 하며, 비활성 상태에서는 항상 fail-closed
 * gateway를 사용한다.
 */
@Configuration
public class LlmGatewayConfiguration {

    @Bean
    @ConditionalOnProperty(name = "opsmate.llm.enabled", havingValue = "true")
    LocalOpenWeightLlmGateway ollamaLocalLlmGateway(LlmProperties properties, ObjectMapper objectMapper) {
        URI baseUri = validateBaseUri(properties);
        if (!StringUtils.hasText(properties.getModel())) {
            throw new IllegalStateException("OPSMATE_LLM_MODEL is required when the LLM gateway is enabled");
        }
        if (properties.getMaxOutputTokens() < 1
                || properties.getMaxOutputTokens() > 4096
                || properties.getMaxResponseBytes() < 1024
                || properties.getMaxResponseBytes() > 1_048_576
                || properties.getConnectTimeout() == null
                || properties.getConnectTimeout().isZero()
                || properties.getConnectTimeout().isNegative()
                || properties.getReadTimeout() == null
                || properties.getReadTimeout().isZero()
                || properties.getReadTimeout().isNegative()) {
            throw new IllegalStateException("LLM time and output limits are outside the safe range");
        }

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(properties.getConnectTimeout())
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(properties.getReadTimeout());
        RestClient.Builder restClientBuilder = RestClient.builder()
                .baseUrl(baseUri.toString())
                .requestFactory(requestFactory);
        if (StringUtils.hasText(properties.getAuthToken())) {
            // Ollama를 공인망에 직접 노출하지 않더라도 private tunnel 내부 proxy에서
            // 애플리케이션 한 곳만 허용하도록 별도 bearer 인증을 적용한다.
            restClientBuilder.defaultHeader(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + properties.getAuthToken());
        }
        RestClient restClient = restClientBuilder.build();
        return new OllamaLocalLlmGateway(
                restClient,
                objectMapper,
                properties.getModel(),
                properties.getMaxOutputTokens(),
                properties.getMaxResponseBytes());
    }

    @Bean
    @ConditionalOnMissingBean(LocalOpenWeightLlmGateway.class)
    LocalOpenWeightLlmGateway failClosedLocalLlmGateway() {
        return new FailClosedLocalLlmGateway();
    }

    /** 요청별 URL 주입과 비허용 내부·외부 host 접근을 시작 단계에서 차단한다. */
    URI validateBaseUri(LlmProperties properties) {
        URI uri;
        try {
            uri = URI.create(properties.getBaseUrl());
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("Invalid LLM base URL", exception);
        }

        if (!("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
                || uri.getHost() == null
                || uri.getUserInfo() != null
                || uri.getQuery() != null
                || uri.getFragment() != null
                || (StringUtils.hasText(uri.getPath()) && !"/".equals(uri.getPath()))) {
            throw new IllegalStateException("LLM base URL must contain only an http(s) scheme, host and optional port");
        }

        Set<String> allowed = properties.getAllowedHosts().stream()
                .map(host -> host.toLowerCase(Locale.ROOT))
                .collect(Collectors.toUnmodifiableSet());
        if (!allowed.contains(uri.getHost().toLowerCase(Locale.ROOT))) {
            throw new IllegalStateException("LLM base URL host is not in OPSMATE_LLM_ALLOWED_HOSTS");
        }
        return uri;
    }
}
