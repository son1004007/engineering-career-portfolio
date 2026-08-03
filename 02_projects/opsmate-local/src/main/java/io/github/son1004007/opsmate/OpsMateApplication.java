package io.github.son1004007.opsmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class OpsMateApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpsMateApplication.class, args);
    }
}
