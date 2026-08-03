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
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Configuration
public class LlmGatewayConfiguration {

    @Bean
    @ConditionalOnProperty(name = "opsmate.llm.enabled", havingValue = "true")
    LocalOpenWeightLlmGateway ollamaLocalLlmGateway(LlmProperties properties, ObjectMapper objectMapper) {
        URI baseUri = validateBaseUri(properties);
        if (!StringUtils.hasText(properties.getModel())) {
            throw new IllegalStateException("OPSMATE_LLM_MODEL is required when the LLM gateway is enabled");
        }

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(properties.getConnectTimeout())
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(properties.getReadTimeout());
        RestClient restClient = RestClient.builder()
                .baseUrl(baseUri.toString())
                .requestFactory(requestFactory)
                .build();
        return new OllamaLocalLlmGateway(restClient, objectMapper, properties.getModel());
    }

    @Bean
    @ConditionalOnMissingBean(LocalOpenWeightLlmGateway.class)
    LocalOpenWeightLlmGateway failClosedLocalLlmGateway() {
        return new FailClosedLocalLlmGateway();
    }

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
