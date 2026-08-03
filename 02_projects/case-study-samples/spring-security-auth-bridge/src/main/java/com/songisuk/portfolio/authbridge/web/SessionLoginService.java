package com.songisuk.portfolio.authbridge.web;

import com.songisuk.portfolio.authbridge.sso.SsoAuthenticationService;
import com.songisuk.portfolio.authbridge.sso.SsoLoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
public class SessionLoginService {

    private final AuthenticationManager authenticationManager;
    private final SsoAuthenticationService ssoAuthenticationService;
    private final SessionAuthenticationStrategy sessionAuthenticationStrategy;
    private final SecurityContextRepository securityContextRepository;

    public SessionLoginService(
            AuthenticationManager authenticationManager,
            SsoAuthenticationService ssoAuthenticationService,
            SessionAuthenticationStrategy sessionAuthenticationStrategy,
            SecurityContextRepository securityContextRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.ssoAuthenticationService = ssoAuthenticationService;
        this.sessionAuthenticationStrategy = sessionAuthenticationStrategy;
        this.securityContextRepository = securityContextRepository;
    }

    public Authentication loginWithDatabase(
            DbLoginRequest login,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(login.username(), login.password()));
        establishSession(authentication, request, response);
        return authentication;
    }

    public Authentication loginWithSso(
            SsoLoginRequest assertion,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Authentication authentication = ssoAuthenticationService.authenticate(assertion);
        establishSession(authentication, request, response);
        return authentication;
    }

    private void establishSession(
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        sessionAuthenticationStrategy.onAuthentication(authentication, request, response);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);
    }
}
