package com.songisuk.portfolio.authbridge.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DbLoginRequest(
        @NotBlank @Size(max = 64) String username,
        @NotBlank @Size(max = 256) String password
) {
}
