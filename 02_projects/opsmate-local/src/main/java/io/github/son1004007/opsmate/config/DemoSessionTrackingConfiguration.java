package io.github.son1004007.opsmate.config;

import java.util.Set;

import jakarta.servlet.SessionTrackingMode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 공개 데모의 세션 식별자를 쿠키로만 전달하도록 Servlet 경계를 고정한다.
 *
 * <p>설정 파일의 {@code server.servlet.session.tracking-modes=cookie} 선언과 별개로
 * 실제 ServletContext에도 COOKIE 단일 모드를 적용해 URL rewriting에 의한
 * {@code ;jsessionid=} 노출을 허용하지 않는다.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "opsmate.demo.enabled", havingValue = "true")
public class DemoSessionTrackingConfiguration {

    @Bean
    ServletContextInitializer cookieOnlySessionTrackingInitializer() {
        return servletContext ->
                servletContext.setSessionTrackingModes(Set.of(SessionTrackingMode.COOKIE));
    }
}
