package com.songisuk.portfolio.authbridge.web;

import org.springframework.security.core.Authentication;

import java.util.List;

public record AuthenticatedUserResponse(String username, List<String> roles) {

    public static AuthenticatedUserResponse from(Authentication authentication) {
        List<String> roles = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .sorted()
                .toList();
        return new AuthenticatedUserResponse(authentication.getName(), roles);
    }
}
