package com.songisuk.portfolio.warcase;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ReleaseWarScriptTest {

    @TempDir
    Path tempDir;

    @Test
    void healthyReleaseKeepsNewArtifactAndBacksUpPreviousWar() throws Exception {
        assumePosixShell();
        Path deployDir = Files.createDirectory(tempDir.resolve("deploy"));
        Path active = write(deployDir.resolve("demo.war"), "old");
        Path candidate = write(tempDir.resolve("candidate.war"), "new");
        Path health = executable(tempDir.resolve("health-ok.sh"), "#!/bin/sh\nexit 0\n");

        ProcessResult result = runRelease(candidate, deployDir, "demo", health, Map.of());

        assertThat(result.exitCode()).isZero();
        assertThat(Files.readString(active)).isEqualTo("new");
        assertThat(Files.readString(deployDir.resolve(".rollback/demo.war.previous"))).isEqualTo("old");
    }

    @Test
    void failedHealthCheckRestoresPreviousWar() throws Exception {
        assumePosixShell();
        Path deployDir = Files.createDirectory(tempDir.resolve("deploy"));
        Path active = write(deployDir.resolve("demo.war"), "old");
        Path candidate = write(tempDir.resolve("candidate.war"), "new");
        Path health = executable(tempDir.resolve("health-fail.sh"), "#!/bin/sh\nexit 1\n");

        ProcessResult result = runRelease(candidate, deployDir, "demo", health, Map.of());

        assertThat(result.exitCode()).isEqualTo(1);
        assertThat(Files.readString(active)).isEqualTo("old");
        assertThat(result.output()).contains("restoring previous artifact");
    }

    @Test
    void unsafeApplicationNameIsRejectedBeforeReplacement() throws Exception {
        assumePosixShell();
        Path deployDir = Files.createDirectory(tempDir.resolve("deploy"));
        Path candidate = write(tempDir.resolve("candidate.war"), "new");
        Path health = executable(tempDir.resolve("health-ok.sh"), "#!/bin/sh\nexit 0\n");

        ProcessResult result = runRelease(candidate, deployDir, "../escape", health, Map.of());

        assertThat(result.exitCode()).isEqualTo(65);
        assertThat(result.output()).contains("unsafe app name");
        assertThat(Files.exists(tempDir.resolve("escape.war"))).isFalse();
    }

    private ProcessResult runRelease(Path candidate, Path deployDir, String appName, Path health, Map<String, String> extraEnv)
            throws Exception {
        Path script = Path.of("deploy/release-war.sh").toAbsolutePath();
        ProcessBuilder builder = new ProcessBuilder(
                "sh", script.toString(), candidate.toString(), deployDir.toString(), appName, "http://127.0.0.1/healthz");
        builder.redirectErrorStream(true);
        builder.environment().put("CURL_BIN", health.toString());
        builder.environment().put("HEALTH_ATTEMPTS", "1");
        builder.environment().put("HEALTH_DELAY_SECONDS", "0");
        builder.environment().putAll(extraEnv);
        Process process = builder.start();
        String output = new String(process.getInputStream().readAllBytes());
        return new ProcessResult(process.waitFor(), output);
    }

    private Path write(Path path, String content) throws Exception {
        Files.writeString(path, content);
        return path;
    }

    private Path executable(Path path, String content) throws Exception {
        Files.writeString(path, content);
        path.toFile().setExecutable(true);
        return path;
    }

    private void assumePosixShell() {
        Assumptions.assumeFalse(System.getProperty("os.name", "").toLowerCase().contains("win"));
    }

    private record ProcessResult(int exitCode, String output) {
    }
}
