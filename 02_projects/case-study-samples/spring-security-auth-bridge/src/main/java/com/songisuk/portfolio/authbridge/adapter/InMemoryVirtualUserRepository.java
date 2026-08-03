package com.songisuk.portfolio.authbridge.adapter;

import com.songisuk.portfolio.authbridge.domain.VirtualUser;
import com.songisuk.portfolio.authbridge.port.VirtualUserRepository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class InMemoryVirtualUserRepository implements VirtualUserRepository {

    private final Map<String, VirtualUser> byUsername;
    private final Map<String, VirtualUser> bySsoSubject;

    public InMemoryVirtualUserRepository(Collection<VirtualUser> users) {
        this.byUsername = immutableIndex(users, VirtualUser::username, "username");
        this.bySsoSubject = immutableIndex(users, VirtualUser::ssoSubject, "SSO subject");
    }

    @Override
    public Optional<VirtualUser> findByUsername(String username) {
        return Optional.ofNullable(byUsername.get(username));
    }

    @Override
    public Optional<VirtualUser> findBySsoSubject(String ssoSubject) {
        return Optional.ofNullable(bySsoSubject.get(ssoSubject));
    }

    private static Map<String, VirtualUser> immutableIndex(
            Collection<VirtualUser> users,
            Function<VirtualUser, String> keyExtractor,
            String label
    ) {
        try {
            return users.stream().collect(Collectors.toUnmodifiableMap(keyExtractor, Function.identity()));
        } catch (IllegalStateException duplicate) {
            throw new IllegalArgumentException("Duplicate " + label + " in synthetic user data", duplicate);
        }
    }
}
