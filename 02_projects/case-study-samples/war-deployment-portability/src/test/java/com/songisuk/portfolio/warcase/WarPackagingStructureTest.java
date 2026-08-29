package com.songisuk.portfolio.warcase;

import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class WarPackagingStructureTest {

    @Test
    void projectBuildsAWarWithProvidedTomcat() throws Exception {
        String pom = Files.readString(Path.of("pom.xml"));

        assertThat(pom).contains("<packaging>war</packaging>");
        assertThat(pom).contains("<artifactId>spring-boot-starter-tomcat</artifactId>");
        assertThat(pom).contains("<scope>provided</scope>");
    }

    @Test
    void servletInitializerSupportsExternalContainerBootstrapping() {
        assertThat(SpringBootServletInitializer.class).isAssignableFrom(ServletInitializer.class);
    }
}
