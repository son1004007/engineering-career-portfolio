package com.songisuk.portfolio.querycase;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.songisuk.portfolio.querycase.repository")
public class MybatisQueryCorrectnessApplication {

    public static void main(String[] args) {
        SpringApplication.run(MybatisQueryCorrectnessApplication.class, args);
    }
}
