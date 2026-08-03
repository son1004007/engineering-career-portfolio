package com.songisuk.portfolio.authbridge.web;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Service;

@Service
public class SessionLogoutService {

    private final CsrfTokenRepository csrfTokens;
    private final SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

    public SessionLogoutService(CsrfTokenRepository csrfTokens) {
        this.csrfTokens = csrfTokens;
    }

    public LogoutResponse logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        csrfTokens.saveToken(null, request, response);
        logoutHandler.logout(request, response, authentication);

        Cookie sessionCookie = new Cookie("JSESSIONID", "");
        sessionCookie.setPath("/");
        sessionCookie.setHttpOnly(true);
        sessionCookie.setSecure(request.isSecure());
        sessionCookie.setMaxAge(0);
        sessionCookie.setAttribute("SameSite", "Strict");
        response.addCookie(sessionCookie);

        return new LogoutResponse("LOGGED_OUT", "The authenticated session was closed");
    }
}
