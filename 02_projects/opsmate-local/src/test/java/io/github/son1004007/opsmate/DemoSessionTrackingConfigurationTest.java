package io.github.son1004007.opsmate;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

class DemoSessionTrackingConfigurationTest {

    @Test
    void publicDemoProfileUsesCookieOnlySessionTracking() throws IOException {
        List<PropertySource<?>> sources = new YamlPropertySourceLoader()
                .load("application-demo", new ClassPathResource("application-demo.yml"));

        Object trackingModes = sources.stream()
                .map(source -> source.getProperty("server.servlet.session.tracking-modes"))
                .filter(value -> value != null)
                .findFirst()
                .orElse(null);

        assertThat(trackingModes)
                .as("public demo must not permit URL-based ;jsessionid session tracking")
                .isEqualTo("cookie");
    }
}
