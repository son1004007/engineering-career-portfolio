package com.songisuk.portfolio.authbridge.domain;

public enum Role {
    USER,
    ANALYST,
    ADMIN;

    public String authority() {
        return "ROLE_" + name();
    }
}
