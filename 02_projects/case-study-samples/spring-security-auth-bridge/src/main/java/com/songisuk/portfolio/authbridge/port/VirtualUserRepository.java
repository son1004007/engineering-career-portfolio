package com.songisuk.portfolio.authbridge.port;

import com.songisuk.portfolio.authbridge.domain.VirtualUser;

import java.util.Optional;

public interface VirtualUserRepository {

    Optional<VirtualUser> findByUsername(String username);

    Optional<VirtualUser> findBySsoSubject(String ssoSubject);
}
