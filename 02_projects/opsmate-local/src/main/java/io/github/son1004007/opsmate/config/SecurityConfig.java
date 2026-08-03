package io.github.son1004007.opsmate.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.StringUtils;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/purchase-requests/drafts").hasRole("REQUESTER")
                        .requestMatchers(HttpMethod.POST, "/api/purchase-requests/*/submit").hasRole("REQUESTER")
                        .requestMatchers(HttpMethod.POST, "/api/purchase-requests/*/decisions").hasRole("APPROVER")
                        .requestMatchers(HttpMethod.POST, "/api/purchase-orders").hasRole("BUYER")
                        .requestMatchers(HttpMethod.GET, "/api/audit-events").hasRole("AUDITOR")
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(SecurityProperties properties, PasswordEncoder encoder) {
        Map<String, String> credentials = new LinkedHashMap<>();
        credentials.put("requester", properties.getRequesterPassword());
        credentials.put("approver", properties.getApproverPassword());
        credentials.put("buyer", properties.getBuyerPassword());
        credentials.put("auditor", properties.getAuditorPassword());

        credentials.forEach((username, password) -> {
            if (!StringUtils.hasText(password)) {
                throw new IllegalStateException("Missing required password for demo user: " + username);
            }
        });

        UserDetails requester = user("requester", credentials.get("requester"), "REQUESTER", encoder);
        UserDetails approver = user("approver", credentials.get("approver"), "APPROVER", encoder);
        UserDetails buyer = user("buyer", credentials.get("buyer"), "BUYER", encoder);
        UserDetails auditor = user("auditor", credentials.get("auditor"), "AUDITOR", encoder);
        return new InMemoryUserDetailsManager(requester, approver, buyer, auditor);
    }

    private UserDetails user(String username, String password, String role, PasswordEncoder encoder) {
        return User.withUsername(username)
                .password(encoder.encode(password))
                .roles(role)
                .build();
    }
}
