package io.github.son1004007.opsmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
public class OpsMateApplication {

    public static void main(String[] args) {
        if (MigrationCommand.requested(args)) {
            // migration credential은 one-shot process에서만 사용하고 장시간 실행 app에는 전달하지 않는다.
            MigrationCommand.run(System.getenv());
            return;
        }
        SpringApplication.run(OpsMateApplication.class, args);
    }
}
