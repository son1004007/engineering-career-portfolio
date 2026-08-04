package io.github.son1004007.opsmate.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.util.StringUtils;

/**
 * 로컬 Basic API와 공개 session demo를 서로 다른 filter chain으로 분리한다.
 *
 * <p>demo profile은 API를 전부 닫고 브라우저 쓰기에 CSRF를 유지한다. 공유 비밀번호가
 * 공개 경로에 남지 않게 환경별 chain을 일부 허용하는 방식 대신 명시적으로 deny 한다.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * 로컬 자동화와 API 회귀 검증에만 사용하는 stateless 보안 체인.
     *
     * <p>공개 demo 프로필에서는 Basic 인증을 부분적으로 열어 두지 않고 API 전체를
     * 거부한다. 브라우저 데모는 별도의 session/CSRF 체인을 거쳐 같은 application
     * service를 호출하므로 공유 비밀번호나 API 우회 경로가 공개되지 않는다.
     */
    @Bean
    @Order(1)
    SecurityFilterChain apiSecurityFilterChain(HttpSecurity http, SecurityProperties properties) throws Exception {
        http
                .securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        if (properties.isBasicEnabled()) {
            http.authorizeHttpRequests(auth -> auth
                            .requestMatchers(HttpMethod.POST, "/api/purchase-requests/drafts").hasRole("REQUESTER")
                            .requestMatchers(HttpMethod.POST, "/api/purchase-requests/*/submit").hasRole("REQUESTER")
                            .requestMatchers(HttpMethod.POST, "/api/purchase-requests/*/decisions").hasRole("APPROVER")
                            .requestMatchers(HttpMethod.POST, "/api/purchase-orders").hasRole("BUYER")
                            .requestMatchers(HttpMethod.GET, "/api/audit-events").hasRole("AUDITOR")
                            .anyRequest().authenticated())
                    .httpBasic(Customizer.withDefaults());
        } else {
            http.authorizeHttpRequests(auth -> auth.anyRequest().denyAll());
        }
        return http.build();
    }

    /**
     * 공개 브라우저 데모용 stateful 보안 체인.
     *
     * <p>HttpSession은 동일 출처 form에서만 사용하고 모든 쓰기에 CSRF를 적용한다.
     * 세션에 저장된 DemoPrincipal의 workspace와 persona는 서버가 생성하며, 브라우저가
     * actor나 workspace를 요청 값으로 주입할 수 없다.
     */
    @Bean
    @Order(2)
    SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        CookieCsrfTokenRepository csrfRepository = new CookieCsrfTokenRepository();
        csrfRepository.setCookiePath("/");
        csrfRepository.setCookieCustomizer(cookie -> cookie.sameSite("Lax"));
        return http
                // landing GET은 서버 session 대신 CSRF cookie만 발급해 익명 session heap 증가를 피한다.
                .csrf(csrf -> csrf.csrfTokenRepository(csrfRepository))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/", "/assets/**", "/error", "/actuator/health/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, "/demo/sessions").permitAll()
                        .requestMatchers("/demo/**").authenticated()
                        .anyRequest().denyAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                // 공개 데모는 로그인 후 saved request 복원이 필요 없다. 익명 보호 경로 조회가
                // HttpSessionRequestCache를 통해 무제한 JSESSIONID를 만들지 못하게 한다.
                .requestCache(cache -> cache.requestCache(new NullRequestCache()))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/")))
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "default-src 'self'; style-src 'self'; script-src 'self'; "
                                        + "img-src 'self' data:; object-src 'none'; frame-ancestors 'none'; "
                                        + "base-uri 'self'; form-action 'self'"))
                        .frameOptions(frame -> frame.deny()))
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(SecurityProperties properties, PasswordEncoder encoder) {
        if (!properties.isBasicEnabled()) {
            return new InMemoryUserDetailsManager();
        }

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
