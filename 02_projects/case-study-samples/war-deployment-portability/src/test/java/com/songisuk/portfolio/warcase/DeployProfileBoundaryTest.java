package com.songisuk.portfolio.warcase;

import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeployProfileBoundaryTest {

    @Test
    void deployProfileFailsClosedWithoutExternalRuntimeToken() {
        assertThatThrownBy(() -> new SpringApplicationBuilder(PortableWarApplication.class)
                .web(WebApplicationType.NONE)
                .profiles("deploy")
                .properties("spring.main.banner-mode=off", "spring.main.log-startup-info=false")
                .run("--portfolio.runtime-token="))
                .hasRootCauseInstanceOf(IllegalStateException.class)
                .hasRootCauseMessage("deploy profile requires an external runtime token");
    }

    @Test
    void deployProfileStartsWhenRuntimeTokenIsProvidedExternally() {
        try (ConfigurableApplicationContext context = new SpringApplicationBuilder(PortableWarApplication.class)
                .web(WebApplicationType.NONE)
                .profiles("deploy")
                .properties("spring.main.banner-mode=off", "spring.main.log-startup-info=false")
                .run("--portfolio.runtime-token=synthetic-ci-token")) {
            context.getBean(DeployRuntimeGuard.class);
        }
    }
}
