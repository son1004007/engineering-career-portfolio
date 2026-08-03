package com.songisuk.portfolio.authbridge.web;

import java.time.Instant;

public record ApiError(String code, String message, Instant timestamp) {
}
