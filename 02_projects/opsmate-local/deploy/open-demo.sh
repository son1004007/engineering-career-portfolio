#!/bin/sh

set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
COMPOSE_FILE="$SCRIPT_DIR/compose.yml"
ENV_FILE=${OPSMATE_ENV_FILE:-"$SCRIPT_DIR/.env"}

fail() {
    printf '%s\n' "open-demo: $1" >&2
    exit 1
}

command_exists() {
    command -v "$1" >/dev/null 2>&1
}

load_environment() {
    [ -f "$ENV_FILE" ] || fail "environment file is missing; copy .env.example to .env first"
    set -a
    # shellcheck disable=SC1090
    . "$ENV_FILE"
    set +a
}

required() {
    name=$1
    value=$(printenv "$name" 2>/dev/null || true)
    [ -n "$value" ] || fail "required environment variable $name is empty"
    case "$value" in
        *replace-with-* | *.invalid*)
            fail "required environment variable $name still contains a placeholder"
            ;;
    esac
}

resolve_deploy_file() {
    case "$1" in
        /*) printf '%s\n' "$1" ;;
        ./*) printf '%s/%s\n' "$SCRIPT_DIR" "${1#./}" ;;
        *) printf '%s/%s\n' "$SCRIPT_DIR" "$1" ;;
    esac
}

compose() {
    docker compose --env-file "$ENV_FILE" --file "$COMPOSE_FILE" "$@"
}

stop_stack_on_failure() {
    status=$?
    trap - EXIT HUP INT TERM
    if [ "$status" -ne 0 ]; then
        cleanup_ok=1
        compose stop --timeout 20 caddy >/dev/null 2>&1 || cleanup_ok=0
        compose stop --timeout 45 app >/dev/null 2>&1 || cleanup_ok=0
        compose stop --timeout 15 model-tunnel >/dev/null 2>&1 || cleanup_ok=0

        if running=$(compose ps --status running --services caddy app model-tunnel 2>/dev/null); then
            [ -z "$running" ] || cleanup_ok=0
        else
            cleanup_ok=0
        fi

        if [ "$cleanup_ok" -eq 1 ]; then
            if ! compose exec --no-TTY db \
                psql \
                --username "${POSTGRES_ADMIN_USER:-}" \
                --dbname "${POSTGRES_DB:-}" \
                --set ON_ERROR_STOP=1 \
                --command "TRUNCATE TABLE public.demo_workspaces CASCADE;" \
                >/dev/null 2>&1
            then
                cleanup_ok=0
            fi
        fi

        compose stop --timeout 20 migrate db >/dev/null 2>&1 || cleanup_ok=0

        if [ "$cleanup_ok" -eq 1 ]; then
            printf '%s\n' "open-demo: failed; edge, app, model tunnel and database were stopped and synthetic rows were deleted" >&2
        else
            printf '%s\n' "open-demo: failed; automatic cleanup was incomplete, so verify the stack is closed before retrying" >&2
        fi
    fi
    exit "$status"
}

validate_image_digest() {
    value=$1
    label=$2
    printf '%s' "$value" | grep -Eq '^[-A-Za-z0-9._/:]+@sha256:[A-Fa-f0-9]{64}$' \
        || fail "$label must be pinned by a full sha256 digest"
}

preflight() {
    command_exists docker || fail "docker is required"
    command_exists grep || fail "grep is required for configuration validation"
    docker info >/dev/null 2>&1 || fail "docker daemon is unavailable"
    docker compose version >/dev/null 2>&1 || fail "docker compose v2 is required"

    for name in \
        COMPOSE_PROJECT_NAME \
        OPSMATE_APP_IMAGE \
        OPSMATE_TUNNEL_IMAGE \
        DEMO_DOMAIN \
        ACME_EMAIL \
        POSTGRES_DB \
        POSTGRES_ADMIN_USER \
        POSTGRES_ADMIN_PASSWORD \
        OPSMATE_DB_MIGRATION_USER \
        OPSMATE_DB_MIGRATION_PASSWORD \
        OPSMATE_DB_APP_USER \
        OPSMATE_DB_APP_PASSWORD \
        OFFICE_SSH_HOST \
        OFFICE_SSH_PORT \
        OFFICE_SSH_USER \
        OPSMATE_OFFICE_SSH_KEY_FILE \
        OPSMATE_OFFICE_KNOWN_HOSTS_FILE \
        OPSMATE_LLM_BASE_URL \
        OPSMATE_LLM_ALLOWED_HOSTS \
        OPSMATE_LLM_MODEL \
        OPSMATE_HOST_EGRESS_POLICY_VERIFIED \
        OPSMATE_EDGE_RATE_LIMIT_VERIFIED
    do
        required "$name"
    done

    [ "${OPSMATE_DEMO_START_ENABLED:-false}" = "true" ] \
        || fail "OPSMATE_DEMO_START_ENABLED must be true before opening"
    [ "$OPSMATE_HOST_EGRESS_POLICY_VERIFIED" = "YES" ] \
        || fail "verified tunnel-only model egress policy is required before opening"
    [ "$OPSMATE_EDGE_RATE_LIMIT_VERIFIED" = "YES" ] \
        || fail "verified public edge rate limiting is required before opening"

    validate_image_digest "$OPSMATE_APP_IMAGE" "OPSMATE_APP_IMAGE"
    validate_image_digest "$OPSMATE_TUNNEL_IMAGE" "OPSMATE_TUNNEL_IMAGE"

    for role in "$POSTGRES_ADMIN_USER" "$OPSMATE_DB_MIGRATION_USER" "$OPSMATE_DB_APP_USER"
    do
        printf '%s' "$role" | grep -Eq '^[a-z_][a-z0-9_]{0,62}$' \
            || fail "database role names must be lowercase safe PostgreSQL identifiers"
    done
    [ "$POSTGRES_ADMIN_USER" != "$OPSMATE_DB_MIGRATION_USER" ] \
        && [ "$POSTGRES_ADMIN_USER" != "$OPSMATE_DB_APP_USER" ] \
        && [ "$OPSMATE_DB_MIGRATION_USER" != "$OPSMATE_DB_APP_USER" ] \
        || fail "admin, migration and runtime database roles must be different"

    for password in \
        "$POSTGRES_ADMIN_PASSWORD" \
        "$OPSMATE_DB_MIGRATION_PASSWORD" \
        "$OPSMATE_DB_APP_PASSWORD"
    do
        [ "${#password}" -ge 24 ] || fail "database passwords must contain at least 24 characters"
    done

    printf '%s' "$OFFICE_SSH_HOST" | grep -Eq '^[A-Za-z0-9][A-Za-z0-9.-]{0,252}$' \
        || fail "OFFICE_SSH_HOST contains unsupported characters"
    printf '%s' "$OFFICE_SSH_USER" | grep -Eq '^[A-Za-z0-9_][A-Za-z0-9_.-]{0,63}$' \
        || fail "OFFICE_SSH_USER contains unsupported characters"
    printf '%s' "$OFFICE_SSH_PORT" | grep -Eq '^[0-9]{1,5}$' \
        || fail "OFFICE_SSH_PORT must be numeric"
    [ "$OFFICE_SSH_PORT" -ge 1 ] && [ "$OFFICE_SSH_PORT" -le 65535 ] \
        || fail "OFFICE_SSH_PORT is outside the valid TCP port range"

    key_file=$(resolve_deploy_file "$OPSMATE_OFFICE_SSH_KEY_FILE")
    known_hosts_file=$(resolve_deploy_file "$OPSMATE_OFFICE_KNOWN_HOSTS_FILE")
    [ -s "$key_file" ] || fail "Office tunnel private key file is missing"
    [ -s "$known_hosts_file" ] || fail "Office known_hosts file is missing"
    case "$key_file $known_hosts_file" in
        *'/tunnel/fixtures/'*) fail "CI-only SSH fixture files cannot be used for a real deployment" ;;
    esac

    [ "$OPSMATE_LLM_BASE_URL" = "http://model-tunnel:11434" ] \
        || fail "OPSMATE_LLM_BASE_URL must point only to the internal model-tunnel service"
    [ "$OPSMATE_LLM_ALLOWED_HOSTS" = "model-tunnel" ] \
        || fail "OPSMATE_LLM_ALLOWED_HOSTS must contain only model-tunnel"
    [ -z "${OPSMATE_LLM_AUTH_TOKEN:-}" ] \
        || fail "OPSMATE_LLM_AUTH_TOKEN must be empty for the SSH-forwarded loopback model path"

    compose config --quiet >/dev/null 2>&1 \
        || fail "docker compose configuration is invalid"
}

load_environment
trap stop_stack_on_failure EXIT HUP INT TERM

preflight

# Pull immutable images before starting any service. No public listener is created here.
printf '%s\n' "open-demo: pulling verified immutable application and tunnel images"
compose pull app migrate model-tunnel
docker image inspect "$OPSMATE_APP_IMAGE" >/dev/null 2>&1 \
    || fail "the verified application image digest is unavailable"
docker image inspect "$OPSMATE_TUNNEL_IMAGE" >/dev/null 2>&1 \
    || fail "the verified tunnel image digest is unavailable"

# A failed or interrupted open must never leave a prior public edge running.
compose stop --timeout 20 caddy >/dev/null 2>&1 || fail "existing public Caddy service could not be stopped"

printf '%s\n' "open-demo: starting the restricted Office model tunnel"
compose up --detach --no-build --wait --wait-timeout "${TUNNEL_WAIT_SECONDS:-60}" model-tunnel

printf '%s\n' "open-demo: starting PostgreSQL"
compose up --detach --wait --wait-timeout "${DB_WAIT_SECONDS:-90}" db

printf '%s\n' "open-demo: running one-shot migration and starting the verified application"
compose up --detach --no-build --wait --wait-timeout "${APP_WAIT_SECONDS:-180}" app

printf '%s\n' "open-demo: application readiness passed; starting Caddy"
compose up --detach --wait --wait-timeout "${CADDY_WAIT_SECONDS:-90}" caddy

"$SCRIPT_DIR/smoke-test.sh"

trap - EXIT HUP INT TERM
printf '%s\n' "open-demo: public HTTPS smoke checks passed"
