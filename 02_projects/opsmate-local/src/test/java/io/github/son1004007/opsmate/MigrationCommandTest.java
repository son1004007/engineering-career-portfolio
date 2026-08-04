package io.github.son1004007.opsmate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;

import org.junit.jupiter.api.Test;

class MigrationCommandTest {

    @Test
    void explicitFlagSelectsTheOneShotMigrationPath() {
        assertThat(MigrationCommand.requested(new String[] {"--opsmate-migrate-only"})).isTrue();
        assertThat(MigrationCommand.requested(new String[] {"--server.port=0"})).isFalse();
    }

    @Test
    void unsafeRuntimeRoleIsRejectedBeforeDatabaseAccess() {
        Map<String, String> environment = Map.of(
                "OPSMATE_DB_URL", "jdbc:postgresql://127.0.0.1:5432/opsmate",
                "OPSMATE_FLYWAY_USERNAME", "migration",
                "OPSMATE_FLYWAY_PASSWORD", "synthetic-password",
                "OPSMATE_FLYWAY_APP_ROLE", "opsmate_app; DROP TABLE audit_events");

        assertThatThrownBy(() -> MigrationCommand.run(environment))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("safe PostgreSQL identifier");
    }
}
