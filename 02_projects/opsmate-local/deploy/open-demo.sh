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

compose() {
    docker compose --env-file "$ENV_FILE" --file "$COMPOSE_FILE" "$@"
}

stop_stack_on_failure() {
    status=$?
    trap - EXIT HUP INT TERM
    if [ "$status" -ne 0 ]; then
        cleanup_ok=1
        if ! compose stop --timeout 20 caddy >/dev/null 2>&1; then
            cleanup_ok=0
        fi
        if ! compose stop --timeout 45 app >/dev/null 2>&1; then
            cleanup_ok=0
        fi

        # 실패 복구에서도 정상 종료와 같은 순서를 지킨다. 쓰기 주체가 남아 있으면
        # 합성 데이터 삭제를 시도하지 않아 진행 중 트랜잭션과의 경합을 피한다.
        if running=$(compose ps --status running --services caddy app 2>/dev/null); then
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

        if ! compose stop --timeout 20 migrate db >/dev/null 2>&1; then
            cleanup_ok=0
        fi

        if [ "$cleanup_ok" -eq 1 ]; then
            printf '%s\n' "open-demo: failed; public edge, app and database were stopped and synthetic rows were deleted" >&2
        else
            printf '%s\n' "open-demo: failed; automatic cleanup was incomplete, so verify the stack is closed before retrying" >&2
        fi
    fi
    exit "$status"
}

preflight() {
    command_exists docker || fail "docker is required"
    command_exists curl || fail "curl is required for private-model and public smoke checks"
    command_exists grep || fail "grep is required for configuration validation"
    docker info >/dev/null 2>&1 || fail "docker daemon is unavailable"
    docker compose version >/dev/null 2>&1 || fail "docker compose v2 is required"

    for name in \
        COMPOSE_PROJECT_NAME \
        OPSMATE_APP_IMAGE \
        DEMO_DOMAIN \
        ACME_EMAIL \
        POSTGRES_DB \
        POSTGRES_ADMIN_USER \
        POSTGRES_ADMIN_PASSWORD \
        OPSMATE_DB_MIGRATION_USER \
        OPSMATE_DB_MIGRATION_PASSWORD \
        OPSMATE_DB_APP_USER \
        OPSMATE_DB_APP_PASSWORD \
        OPSMATE_LLM_BASE_URL \
        OPSMATE_LLM_ALLOWED_HOSTS \
        OPSMATE_LLM_MODEL \
        OPSMATE_LLM_AUTH_TOKEN \
        OPSMATE_HOST_EGRESS_POLICY_VERIFIED \
        OPSMATE_EDGE_RATE_LIMIT_VERIFIED
    do
        required "$name"
    done

    [ "${OPSMATE_DEMO_START_ENABLED:-false}" = "true" ] \
        || fail "OPSMATE_DEMO_START_ENABLED must be true before opening"
    [ "$OPSMATE_HOST_EGRESS_POLICY_VERIFIED" = "YES" ] \
        || fail "verified host egress allowlisting is required before opening"
    [ "$OPSMATE_EDGE_RATE_LIMIT_VERIFIED" = "YES" ] \
        || fail "verified public edge rate limiting is required before opening"
    printf '%s' "$OPSMATE_APP_IMAGE" | grep -Eq '^[-A-Za-z0-9._/:]+@sha256:[A-Fa-f0-9]{64}$' \
        || fail "OPSMATE_APP_IMAGE must be a verified registry image pinned by full sha256 digest"

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

    printf '%s' "$OPSMATE_LLM_AUTH_TOKEN" | grep -Eq '^[A-Za-z0-9._~-]{32,}$' \
        || fail "OPSMATE_LLM_AUTH_TOKEN must contain at least 32 URL-safe characters"

    case "$OPSMATE_LLM_BASE_URL" in
        http://*) ;;
        *) fail "OPSMATE_LLM_BASE_URL must use the approved HTTP endpoint inside the encrypted VPN" ;;
    esac
    model_authority=${OPSMATE_LLM_BASE_URL#http://}
    case "$model_authority" in
        */*) fail "OPSMATE_LLM_BASE_URL must not contain a path" ;;
    esac
    printf '%s' "$model_authority" | grep -Eq '^([0-9]{1,3}\.){3}[0-9]{1,3}:[0-9]{1,5}$' \
        || fail "OPSMATE_LLM_BASE_URL must contain one private IPv4 address and port"
    model_host=${model_authority%%:*}
    case "$model_host" in
        10.* | 192.168.* | 172.1[6-9].* | 172.2[0-9].* | 172.3[01].* \
        | 100.6[4-9].* | 100.[7-9][0-9].* | 100.1[01][0-9].* | 100.12[0-7].*) ;;
        *) fail "the model endpoint must use an approved private VPN IPv4 address" ;;
    esac
    [ "$OPSMATE_LLM_ALLOWED_HOSTS" = "$model_host" ] \
        || fail "OPSMATE_LLM_ALLOWED_HOSTS must contain only the approved model IPv4 address"

    compose config --quiet >/dev/null 2>&1 \
        || fail "docker compose configuration is invalid"

    health_path=${OPSMATE_LLM_HEALTH_PATH:-/api/tags}
    case "$health_path" in
        /*) ;;
        *) fail "OPSMATE_LLM_HEALTH_PATH must start with /" ;;
    esac

    model_url=${OPSMATE_LLM_BASE_URL%/}${health_path}
    if ! printf 'header = "Authorization: Bearer %s"\n' "$OPSMATE_LLM_AUTH_TOKEN" \
        | curl \
            --config - \
            --noproxy '*' \
            --silent \
            --output /dev/null \
            --fail \
            --connect-timeout 5 \
            --max-time 15 \
            "$model_url"
    then
        fail "the approved private model endpoint did not pass preflight"
    fi
}

load_environment
trap stop_stack_on_failure EXIT HUP INT TERM

preflight

# A failed or interrupted open must never leave the public edge running.
compose stop --timeout 20 caddy >/dev/null 2>&1 || fail "existing public Caddy service could not be stopped"

printf '%s\n' "open-demo: starting PostgreSQL"
compose up --detach --wait --wait-timeout "${DB_WAIT_SECONDS:-90}" db

printf '%s\n' "open-demo: pulling the verified immutable application image"
compose pull app migrate
docker image inspect "$OPSMATE_APP_IMAGE" >/dev/null 2>&1 \
    || fail "the verified application image digest is unavailable"

printf '%s\n' "open-demo: running one-shot migration and starting the verified application"
compose up --detach --no-build --wait --wait-timeout "${APP_WAIT_SECONDS:-180}" app

printf '%s\n' "open-demo: application readiness passed; starting Caddy"
compose up --detach --wait --wait-timeout "${CADDY_WAIT_SECONDS:-90}" caddy

"$SCRIPT_DIR/smoke-test.sh"

trap - EXIT HUP INT TERM
printf '%s\n' "open-demo: public HTTPS smoke checks passed"
