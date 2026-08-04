-- Flyway history is migration metadata; the runtime role must not change or delete it.
REVOKE ALL PRIVILEGES ON TABLE flyway_schema_history FROM ${appRole};
