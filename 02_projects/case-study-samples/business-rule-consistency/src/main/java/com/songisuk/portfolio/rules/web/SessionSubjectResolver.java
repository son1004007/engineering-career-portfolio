package com.songisuk.portfolio.rules.web;

import com.songisuk.portfolio.rules.error.MissingIdentityException;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class SessionSubjectResolver {
    static final String CANONICAL_SUBJECT_ATTRIBUTE = "subjectId";
    static final String LEGACY_LOGIN_ATTRIBUTE = "legacyLoginId";

    public String resolve(HttpSession session) {
        String subjectId = asNonBlankString(session.getAttribute(CANONICAL_SUBJECT_ATTRIBUTE));
        if (subjectId != null) {
            return subjectId;
        }

        String legacyLoginId = asNonBlankString(session.getAttribute(LEGACY_LOGIN_ATTRIBUTE));
        if (legacyLoginId != null) {
            return legacyLoginId;
        }

        throw new MissingIdentityException();
    }

    private String asNonBlankString(Object value) {
        if (!(value instanceof String text) || text.isBlank()) {
            return null;
        }
        return text;
    }
}
