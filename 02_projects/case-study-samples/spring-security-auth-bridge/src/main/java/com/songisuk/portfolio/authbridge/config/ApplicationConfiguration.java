package com.songisuk.portfolio.authbridge.config;

import com.songisuk.portfolio.authbridge.adapter.InMemoryVirtualUserRepository;
import com.songisuk.portfolio.authbridge.adapter.VirtualUserDetailsService;
import com.songisuk.portfolio.authbridge.domain.Role;
import com.songisuk.portfolio.authbridge.domain.VirtualUser;
import com.songisuk.portfolio.authbridge.port.VirtualUserRepository;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Configuration
@EnableConfigurationProperties({DemoUserProperties.class, SsoProperties.class})
public class ApplicationConfiguration {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    VirtualUserRepository virtualUserRepository(
            DemoUserProperties properties,
            PasswordEncoder passwordEncoder
    ) {
        return new InMemoryVirtualUserRepository(List.of(
                user("00000000-0000-0000-0000-000000000001", "analyst", properties.analystPassword(),
                        "sso-analyst-001", Set.of(Role.USER, Role.ANALYST), true, passwordEncoder),
                user("00000000-0000-0000-0000-000000000002", "admin", properties.adminPassword(),
                        "sso-admin-001", Set.of(Role.USER, Role.ADMIN), true, passwordEncoder),
                user("00000000-0000-0000-0000-000000000003", "disabled", properties.disabledPassword(),
                        "sso-disabled-001", Set.of(Role.USER), false, passwordEncoder),
                user("00000000-0000-0000-0000-000000000004", "user", properties.userPassword(),
                        "sso-user-001", Set.of(Role.USER), true, passwordEncoder)
        ));
    }

    @Bean
    UserDetailsService userDetailsService(VirtualUserRepository users) {
        return new VirtualUserDetailsService(users);
    }

    @Bean
    AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    private static VirtualUser user(
            String id,
            String username,
            String rawPassword,
            String ssoSubject,
            Set<Role> roles,
            boolean enabled,
            PasswordEncoder passwordEncoder
    ) {
        return new VirtualUser(UUID.fromString(id), username, passwordEncoder.encode(rawPassword),
                ssoSubject, roles, enabled);
    }
}
