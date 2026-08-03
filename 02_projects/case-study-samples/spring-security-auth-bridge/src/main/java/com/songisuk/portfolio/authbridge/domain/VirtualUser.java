package com.songisuk.portfolio.authbridge.domain;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public record VirtualUser(
        UUID id,
        String username,
        String passwordHash,
        String ssoSubject,
        Set<Role> roles,
        boolean enabled
) {
    public VirtualUser {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(username, "username");
        Objects.requireNonNull(passwordHash, "passwordHash");
        Objects.requireNonNull(ssoSubject, "ssoSubject");
        roles = Set.copyOf(Objects.requireNonNull(roles, "roles"));
    }
}
