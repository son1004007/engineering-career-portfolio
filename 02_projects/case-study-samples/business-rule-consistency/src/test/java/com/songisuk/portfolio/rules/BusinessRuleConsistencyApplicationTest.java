package com.songisuk.portfolio.rules;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BusinessRuleConsistencyApplicationTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    void canonicalSessionSubjectWinsOverLegacyLogin() throws Exception {
        MockHttpSession session = session("member-a", "legacy-only");

        mockMvc.perform(get("/portal/current").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subjectId").value("member-a"))
                .andExpect(jsonPath("$.snapshot.year").value(2026))
                .andExpect(jsonPath("$.snapshot.month").value(2))
                .andExpect(jsonPath("$.score").value(88));
    }

    @Test
    void legacyLoginIsUsedOnlyWhenCanonicalSubjectIsAbsent() throws Exception {
        MockHttpSession session = session(null, "legacy-only");

        mockMvc.perform(get("/portal/current").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subjectId").value("legacy-only"))
                .andExpect(jsonPath("$.snapshot.year").value(2026))
                .andExpect(jsonPath("$.snapshot.month").value(1));
    }

    @Test
    void missingSessionIdentityFailsClosed() throws Exception {
        mockMvc.perform(get("/portal/current"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Authenticated subject required"));
    }

    @Test
    void latestOnlyEndpointIgnoresRequestedHistoricalPeriod() throws Exception {
        MockHttpSession session = session("member-a", null);

        mockMvc.perform(get("/portal/current")
                        .session(session)
                        .param("year", "2025")
                        .param("month", "12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.snapshot.year").value(2026))
                .andExpect(jsonPath("$.snapshot.month").value(2));
    }

    @Test
    void explicitSnapshotEndpointUsesRequestedPeriod() throws Exception {
        MockHttpSession session = session("member-a", null);

        mockMvc.perform(get("/portal/snapshot")
                        .session(session)
                        .param("year", "2025")
                        .param("month", "12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.snapshot.year").value(2025))
                .andExpect(jsonPath("$.snapshot.month").value(12))
                .andExpect(jsonPath("$.score").value(71));
    }

    @Test
    void explicitSnapshotEndpointUsesLatestWhenPeriodIsOmitted() throws Exception {
        MockHttpSession session = session("member-a", null);

        mockMvc.perform(get("/portal/snapshot").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.snapshot.year").value(2026))
                .andExpect(jsonPath("$.snapshot.month").value(2));
    }

    @Test
    void incompleteExplicitPeriodIsRejected() throws Exception {
        MockHttpSession session = session("member-a", null);

        mockMvc.perform(get("/portal/snapshot")
                        .session(session)
                        .param("year", "2026"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid snapshot request"));
    }

    @Test
    void invalidMonthIsRejected() throws Exception {
        MockHttpSession session = session("member-a", null);

        mockMvc.perform(get("/portal/snapshot")
                        .session(session)
                        .param("year", "2026")
                        .param("month", "13"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unavailableExplicitSnapshotReturnsNotFound() throws Exception {
        MockHttpSession session = session("member-a", null);

        mockMvc.perform(get("/portal/snapshot")
                        .session(session)
                        .param("year", "2024")
                        .param("month", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Snapshot unavailable"));
    }

    @Test
    void requestParameterCannotOverrideSessionSubject() throws Exception {
        MockHttpSession session = session("member-a", null);

        mockMvc.perform(get("/portal/current")
                        .session(session)
                        .param("subjectId", "legacy-only"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subjectId").value("member-a"))
                .andExpect(jsonPath("$.score").value(88));
    }

    @Test
    void unknownSubjectWithoutLatestSnapshotReturnsNotFound() throws Exception {
        MockHttpSession session = session("unknown-member", null);

        mockMvc.perform(get("/portal/current").session(session))
                .andExpect(status().isNotFound());
    }

    private MockHttpSession session(String subjectId, String legacyLoginId) {
        MockHttpSession session = new MockHttpSession();
        if (subjectId != null) {
            session.setAttribute("subjectId", subjectId);
        }
        if (legacyLoginId != null) {
            session.setAttribute("legacyLoginId", legacyLoginId);
        }
        return session;
    }
}
