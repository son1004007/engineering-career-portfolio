package com.songisuk.portfolio.warcase;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "server.servlet.context-path=/demo")
@ActiveProfiles("local")
class ContextPathIntegrationTest {

    @LocalServerPort
    int port;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Test
    void entryPageUsesTheRuntimeContextPath() throws Exception {
        HttpResponse<String> response = get("/demo/entry");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("href=\"/demo/healthz\"");
    }

    @Test
    void healthEndpointLivesUnderTheConfiguredContextPath() throws Exception {
        HttpResponse<String> response = get("/demo/healthz");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("\"status\":\"UP\"");
        assertThat(response.body()).contains("\"profile\":\"local\"");
    }

    @Test
    void rootHardcodedHealthPathIsNotAccidentallyExposed() throws Exception {
        assertThat(get("/healthz").statusCode()).isEqualTo(404);
    }

    private HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path)).GET().build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
