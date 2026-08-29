package com.songisuk.portfolio.warcase;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class DeploymentInfoController {

    private final Environment environment;

    public DeploymentInfoController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping(path = "/healthz", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> health() {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("status", "UP");
        response.put("profile", activeProfile());
        return response;
    }

    @GetMapping(path = "/entry", produces = MediaType.TEXT_HTML_VALUE)
    public String entry(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        return "<!doctype html><html><body>"
                + "<h1>Portable WAR</h1>"
                + "<a id=\"health-link\" href=\"" + contextPath + "/healthz\">health</a>"
                + "</body></html>";
    }

    private String activeProfile() {
        return Arrays.stream(environment.getActiveProfiles()).findFirst().orElse("default");
    }
}
