package com.songisuk.portfolio.authbridge.web;

import com.songisuk.portfolio.authbridge.sso.SsoLoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final SessionLoginService loginService;
    private final SessionLogoutService logoutService;

    public AuthenticationController(SessionLoginService loginService, SessionLogoutService logoutService) {
        this.loginService = loginService;
        this.logoutService = logoutService;
    }

    @GetMapping("/csrf")
    CsrfTokenResponse csrf(CsrfToken csrfToken) {
        return new CsrfTokenResponse(csrfToken.getHeaderName(), csrfToken.getToken());
    }

    @PostMapping("/db")
    AuthenticatedUserResponse databaseLogin(
            @Valid @RequestBody DbLoginRequest login,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Authentication authentication = loginService.loginWithDatabase(login, request, response);
        return AuthenticatedUserResponse.from(authentication);
    }

    @PostMapping("/sso")
    AuthenticatedUserResponse ssoLogin(
            @Valid @RequestBody SsoLoginRequest assertion,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Authentication authentication = loginService.loginWithSso(assertion, request, response);
        return AuthenticatedUserResponse.from(authentication);
    }

    @PostMapping("/logout")
    LogoutResponse logout(
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return logoutService.logout(request, response, authentication);
    }
}
