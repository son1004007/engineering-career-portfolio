package com.songisuk.portfolio.authbridge.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.StandardCharsets;

@ConfigurationProperties("app.demo")
public class DemoUserProperties {

    private String analystPassword;
    private String adminPassword;
    private String userPassword;
    private String disabledPassword;

    public String analystPassword() {
        return requirePassword(analystPassword, "APP_DEMO_ANALYST_PASSWORD");
    }

    public String adminPassword() {
        return requirePassword(adminPassword, "APP_DEMO_ADMIN_PASSWORD");
    }

    public String disabledPassword() {
        return requirePassword(disabledPassword, "APP_DEMO_DISABLED_PASSWORD");
    }

    public String userPassword() {
        return requirePassword(userPassword, "APP_DEMO_USER_PASSWORD");
    }

    public void setAnalystPassword(String analystPassword) {
        this.analystPassword = analystPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public void setDisabledPassword(String disabledPassword) {
        this.disabledPassword = disabledPassword;
    }

    private static String requirePassword(String value, String environmentVariable) {
        int byteLength = value == null ? 0 : value.getBytes(StandardCharsets.UTF_8).length;
        if (byteLength < 12 || byteLength > 72) {
            throw new IllegalStateException(environmentVariable + " must contain between 12 and 72 UTF-8 bytes");
        }
        return value;
    }
}
