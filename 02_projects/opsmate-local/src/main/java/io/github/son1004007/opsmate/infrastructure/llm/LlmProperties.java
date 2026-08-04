package io.github.son1004007.opsmate.infrastructure.llm;

import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 허용된 Ollama 호환 endpoint의 host·model·timeout과 서버 전용 Bearer token 설정.
 * token은 환경변수에서만 주입하며 화면, 감사 이벤트와 오류 응답에 노출하지 않는다.
 */
@ConfigurationProperties(prefix = "opsmate.llm")
public class LlmProperties {

    private boolean enabled;
    private String baseUrl = "http://127.0.0.1:11434";
    private Set<String> allowedHosts = new LinkedHashSet<>(Set.of("127.0.0.1", "localhost"));
    private String model = "";
    private String authToken = "";
    private Duration connectTimeout = Duration.ofSeconds(2);
    private Duration readTimeout = Duration.ofSeconds(30);
    private int maxOutputTokens = 512;
    private int maxResponseBytes = 65536;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Set<String> getAllowedHosts() {
        return allowedHosts;
    }

    public void setAllowedHosts(Set<String> allowedHosts) {
        this.allowedHosts = allowedHosts;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getMaxOutputTokens() {
        return maxOutputTokens;
    }

    public void setMaxOutputTokens(int maxOutputTokens) {
        this.maxOutputTokens = maxOutputTokens;
    }

    public int getMaxResponseBytes() {
        return maxResponseBytes;
    }

    public void setMaxResponseBytes(int maxResponseBytes) {
        this.maxResponseBytes = maxResponseBytes;
    }
}
