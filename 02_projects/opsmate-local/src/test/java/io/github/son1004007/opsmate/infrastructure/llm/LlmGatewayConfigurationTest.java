package io.github.son1004007.opsmate.infrastructure.llm;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;

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
}
