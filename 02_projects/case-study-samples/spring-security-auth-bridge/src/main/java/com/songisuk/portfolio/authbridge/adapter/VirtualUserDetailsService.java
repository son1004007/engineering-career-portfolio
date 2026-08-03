package com.songisuk.portfolio.authbridge.adapter;

import com.songisuk.portfolio.authbridge.domain.VirtualUser;
import com.songisuk.portfolio.authbridge.port.VirtualUserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public final class VirtualUserDetailsService implements UserDetailsService {

    private final VirtualUserRepository users;

    public VirtualUserDetailsService(VirtualUserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        VirtualUser user = users.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));

        return User.withUsername(user.username())
                .password(user.passwordHash())
                .authorities(authorities(user))
                .disabled(!user.enabled())
                .build();
    }

    public static List<GrantedAuthority> authorities(VirtualUser user) {
        return user.roles().stream()
                .map(role -> new SimpleGrantedAuthority(role.authority()))
                .map(GrantedAuthority.class::cast)
                .sorted((left, right) -> left.getAuthority().compareTo(right.getAuthority()))
                .toList();
    }
}
