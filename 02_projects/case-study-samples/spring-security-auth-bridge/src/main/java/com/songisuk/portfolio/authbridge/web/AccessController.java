package com.songisuk.portfolio.authbridge.web;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AccessController {

    @GetMapping("/me")
    AuthenticatedUserResponse me(Authentication authentication) {
        return AuthenticatedUserResponse.from(authentication);
    }

    @GetMapping("/reports/monthly")
    Map<String, Object> monthlyReport() {
        return Map.of("period", "synthetic-2026-08", "recordCount", 3, "status", "READY");
    }

    @PostMapping("/admin/reindex")
    Map<String, String> requestReindex() {
        return Map.of("action", "SYNTHETIC_REINDEX", "status", "ACCEPTED");
    }
}
