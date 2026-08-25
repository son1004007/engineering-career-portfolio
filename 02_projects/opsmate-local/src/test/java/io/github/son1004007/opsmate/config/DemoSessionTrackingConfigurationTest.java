package io.github.son1004007.opsmate.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import jakarta.servlet.SessionTrackingMode;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.mock.web.MockServletContext;

class DemoSessionTrackingConfigurationTest {

    @Test
    void initializerForcesCookieOnlyEffectiveTrackingMode() throws Exception {
        DemoSessionTrackingConfiguration configuration = new DemoSessionTrackingConfiguration();
        ServletContextInitializer initializer = configuration.cookieOnlySessionTrackingInitializer();
        MockServletContext servletContext = new MockServletContext();

        initializer.onStartup(servletContext);

        assertThat(servletContext.getEffectiveSessionTrackingModes())
                .containsExactlyElementsOf(Set.of(SessionTrackingMode.COOKIE));
    }
}
