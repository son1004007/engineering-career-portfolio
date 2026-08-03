package com.songisuk.portfolio.authbridge.sso;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SsoLoginRequest(
        @NotBlank @Size(max = 256) String issuer,
        @NotBlank @Size(max = 256) String audience,
        @NotBlank @Size(max = 64) String keyId,
        @NotBlank @Size(max = 128) String subject,
        long issuedAtEpochSeconds,
        @NotBlank @Size(min = 16, max = 128) String nonce,
        @NotBlank @Size(max = 128) String signature
) {
}
