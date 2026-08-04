package io.github.son1004007.opsmate;

import java.util.Arrays;
import java.util.Map;

import org.flywaydb.core.Flyway;

/**
 * Spring 웹 context를 만들지 않고 Flyway만 실행한 뒤 종료하는 배포 전용 command.
 *
 * <p>migration credential이 runtime app 환경에 남지 않도록 같은 immutable jar를 별도
 * one-shot container로 실행한다. app role은 안전한 식별자로 제한한 뒤 callback
 * placeholder에만 전달한다.
 */
final class MigrationCommand {

    private static final String FLAG = "--opsmate-migrate-only";

    private MigrationCommand() {
    }

    static boolean requested(String[] args) {
        return Arrays.asList(args).contains(FLAG);
    }

    static void run(Map<String, String> environment) {
        String url = required(environment, "OPSMATE_DB_URL");
        String user = required(environment, "OPSMATE_FLYWAY_USERNAME");
        String password = required(environment, "OPSMATE_FLYWAY_PASSWORD");
        String appRole = required(environment, "OPSMATE_FLYWAY_APP_ROLE");
        if (!url.startsWith("jdbc:postgresql://")) {
            throw new IllegalStateException("Migration URL must use PostgreSQL JDBC");
        }
        if (!appRole.matches("[a-z_][a-z0-9_]{0,62}")) {
            throw new IllegalStateException("Runtime database role is not a safe PostgreSQL identifier");
        }

        Flyway.configure()
                .dataSource(url, user, password)
                .locations("classpath:db/migration")
                .placeholders(Map.of("appRole", appRole))
                .load()
                .migrate();
    }

    private static String required(Map<String, String> environment, String name) {
        String value = environment.get(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(name + " is required for migration");
        }
        return value;
    }
}
