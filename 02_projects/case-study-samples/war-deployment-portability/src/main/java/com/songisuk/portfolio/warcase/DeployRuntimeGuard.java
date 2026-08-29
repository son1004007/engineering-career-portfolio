package com.songisuk.portfolio.warcase;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("deploy")
public class DeployRuntimeGuard {

    public DeployRuntimeGuard(@Value("${portfolio.runtime-token:}") String runtimeToken) {
        if (runtimeToken == null || runtimeToken.isBlank()) {
            throw new IllegalStateException("deploy profile requires an external runtime token");
        }
    }
}
