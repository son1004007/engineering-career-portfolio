#!/bin/sh

set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
COMPOSE_FILE="$SCRIPT_DIR/compose.yml"
ENV_FILE=${OPSMATE_ENV_FILE:-"$SCRIPT_DIR/.env"}

fail() {
    printf '%s\n' "verify-closed: $1" >&2
    exit 1
}

[ -f "$ENV_FILE" ] || fail "environment file is missing"
set -a
# shellcheck disable=SC1090
. "$ENV_FILE"
set +a

command -v docker >/dev/null 2>&1 || fail "docker is required"

compose() {
    docker compose --env-file "$ENV_FILE" --file "$COMPOSE_FILE" "$@"
}

running_services=$(compose ps --status running --services 2>/dev/null || true)
for service in caddy app migrate db
do
    if printf '%s\n' "$running_services" | grep -qx "$service"; then
        fail "$service is still running"
    fi
done

volume_name="${COMPOSE_PROJECT_NAME:-opsmate-demo}-postgres-data"
docker volume inspect "$volume_name" >/dev/null 2>&1 \
    || fail "the persistent PostgreSQL volume is missing"

if command -v curl >/dev/null 2>&1 && [ -n "${DEMO_DOMAIN:-}" ]; then
    headers_file=$(mktemp)
    trap 'rm -f "$headers_file"' EXIT HUP INT TERM
    if curl \
        --silent \
        --output /dev/null \
        --dump-header "$headers_file" \
        --head \
        --connect-timeout 5 \
        --max-time 10 \
        "https://${DEMO_DOMAIN}/demo"
    then
        if grep -qi '^X-OpsMate-Demo:[[:space:]]*live' "$headers_file"; then
            fail "the external live edge marker is still reachable"
        fi
    fi
fi

printf '%s\n' "verify-closed: app, database, and live Caddy edge are stopped; database volume retained"
