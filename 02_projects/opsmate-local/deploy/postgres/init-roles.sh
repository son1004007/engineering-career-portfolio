#!/bin/sh

set -eu

fail() {
    printf '%s\n' "init-roles: $1" >&2
    exit 1
}

for name in \
    POSTGRES_DB \
    POSTGRES_USER \
    OPSMATE_DB_MIGRATION_USER \
    OPSMATE_DB_MIGRATION_PASSWORD \
    OPSMATE_DB_APP_USER \
    OPSMATE_DB_APP_PASSWORD
do
    value=$(printenv "$name" 2>/dev/null || true)
    [ -n "$value" ] || fail "$name is required"
done

for role in "$POSTGRES_USER" "$OPSMATE_DB_MIGRATION_USER" "$OPSMATE_DB_APP_USER"
do
    printf '%s' "$role" | grep -Eq '^[a-z_][a-z0-9_]{0,62}$' \
        || fail "database role names must be lowercase safe PostgreSQL identifiers"
done

[ "$POSTGRES_USER" != "$OPSMATE_DB_MIGRATION_USER" ] \
    && [ "$POSTGRES_USER" != "$OPSMATE_DB_APP_USER" ] \
    && [ "$OPSMATE_DB_MIGRATION_USER" != "$OPSMATE_DB_APP_USER" ] \
    || fail "admin, migration and runtime roles must be different"

# psql variables quote role identifiers and password literals without constructing SQL in the shell.
psql \
    --username "$POSTGRES_USER" \
    --dbname "$POSTGRES_DB" \
    --set ON_ERROR_STOP=1 \
    --set database_name="$POSTGRES_DB" \
    --set migration_user="$OPSMATE_DB_MIGRATION_USER" \
    --set migration_password="$OPSMATE_DB_MIGRATION_PASSWORD" \
    --set app_user="$OPSMATE_DB_APP_USER" \
    --set app_password="$OPSMATE_DB_APP_PASSWORD" <<'EOSQL'
REVOKE CONNECT, TEMPORARY ON DATABASE :"database_name" FROM PUBLIC;
REVOKE CREATE ON SCHEMA public FROM PUBLIC;

CREATE ROLE :"migration_user"
    LOGIN PASSWORD :'migration_password'
    NOSUPERUSER NOCREATEDB NOCREATEROLE NOINHERIT;
CREATE ROLE :"app_user"
    LOGIN PASSWORD :'app_password'
    NOSUPERUSER NOCREATEDB NOCREATEROLE NOINHERIT;

GRANT CONNECT ON DATABASE :"database_name" TO :"migration_user", :"app_user";
GRANT USAGE, CREATE ON SCHEMA public TO :"migration_user";
GRANT USAGE ON SCHEMA public TO :"app_user";

ALTER DEFAULT PRIVILEGES FOR ROLE :"migration_user" IN SCHEMA public
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO :"app_user";
ALTER DEFAULT PRIVILEGES FOR ROLE :"migration_user" IN SCHEMA public
    GRANT USAGE, SELECT ON SEQUENCES TO :"app_user";
EOSQL
