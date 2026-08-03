package com.songisuk.portfolio.authbridge.sso;

import com.songisuk.portfolio.authbridge.adapter.VirtualUserDetailsService;
import com.songisuk.portfolio.authbridge.domain.VirtualUser;
import com.songisuk.portfolio.authbridge.port.VirtualUserRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SsoAuthenticationService {

    private final SsoAssertionVerifier verifier;
    private final VirtualUserRepository users;

    public SsoAuthenticationService(SsoAssertionVerifier verifier, VirtualUserRepository users) {
        this.verifier = verifier;
        this.users = users;
    }

    public Authentication authenticate(SsoLoginRequest assertion) {
        String subject = verifier.verify(assertion);
        VirtualUser user = users.findBySsoSubject(subject)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid SSO assertion or account"));
        if (!user.enabled()) {
            throw new DisabledException("Invalid SSO assertion or account");
        }

        return UsernamePasswordAuthenticationToken.authenticated(
                user.username(), null, VirtualUserDetailsService.authorities(user));
    }
}
