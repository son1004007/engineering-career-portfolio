package com.songisuk.portfolio.authbridge.web;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;

import java.security.SecureRandom;
import java.util.Base64;

public final class SessionCookieCsrfTokenRepository implements CsrfTokenRepository {

    public static final String COOKIE_NAME = "XSRF-TOKEN";
    public static final String HEADER_NAME = "X-XSRF-TOKEN";
    private static final String PARAMETER_NAME = "_csrf";
    private static final String SESSION_ATTRIBUTE =
            SessionCookieCsrfTokenRepository.class.getName() + ".TOKEN";

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        byte[] value = new byte[32];
        secureRandom.nextBytes(value);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(value);
        return new DefaultCsrfToken(HEADER_NAME, PARAMETER_NAME, token);
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        if (token == null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.removeAttribute(SESSION_ATTRIBUTE);
            }
            response.addCookie(cookie("", 0, request.isSecure()));
            return;
        }

        request.getSession(true).setAttribute(SESSION_ATTRIBUTE, token.getToken());
        response.addCookie(cookie(token.getToken(), -1, request.isSecure()));
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute(SESSION_ATTRIBUTE);
        if (!(value instanceof String token) || token.isBlank()) {
            return null;
        }
        return new DefaultCsrfToken(HEADER_NAME, PARAMETER_NAME, token);
    }

    private static Cookie cookie(String value, int maxAge, boolean secure) {
        Cookie cookie = new Cookie(COOKIE_NAME, value);
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        cookie.setSecure(secure);
        cookie.setMaxAge(maxAge);
        cookie.setAttribute("SameSite", "Strict");
        return cookie;
    }
}
