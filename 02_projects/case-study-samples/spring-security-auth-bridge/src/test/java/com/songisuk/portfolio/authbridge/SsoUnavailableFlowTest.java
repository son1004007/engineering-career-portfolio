package com.songisuk.portfolio.authbridge;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.songisuk.portfolio.authbridge.web.SessionCookieCsrfTokenRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.mock.web.MockHttpSession;

import java.time.Instant;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.demo.analyst-password=test-only-analyst-password-01",
        "app.demo.admin-password=test-only-admin-password-01",
        "app.demo.user-password=test-only-user-password-01",
        "app.demo.disabled-password=test-only-disabled-password-01",
        "app.sso.shared-secret="
})
@AutoConfigureMockMvc
class SsoUnavailableFlowTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void unconfiguredSsoAdapterReturns503InsteadOfFallingBack() throws Exception {
        MvcResult csrf = mockMvc.perform(get("/auth/csrf"))
                .andExpect(status().isOk())
                .andReturn();
        var csrfBody = objectMapper.readTree(csrf.getResponse().getContentAsString());
        MockHttpSession session = (MockHttpSession) csrf.getRequest().getSession(false);
        Cookie cookie = csrf.getResponse().getCookie(SessionCookieCsrfTokenRepository.COOKIE_NAME);

        mockMvc.perform(post("/auth/sso")
                        .session(session)
                        .cookie(cookie)
                        .header(csrfBody.get("headerName").asText(), csrfBody.get("token").asText())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "issuer", "portfolio-demo-idp",
                                "audience", "spring-security-auth-bridge",
                                "keyId", "demo-key-v1",
                                "subject", "sso-analyst-001",
                                "issuedAtEpochSeconds", Instant.now().getEpochSecond(),
                                "nonce", "nonce-unavailable-01",
                                "signature", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("SSO_ADAPTER_UNAVAILABLE"));
    }
}
