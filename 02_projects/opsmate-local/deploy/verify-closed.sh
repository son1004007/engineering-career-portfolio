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
for service in edge app model-tunnel migrate db
do
    if printf '%s\n' "$running_services" | grep -qx "$service"; then
        fail "$service is still running"
    fi
done

volume_name="${COMPOSE_PROJECT_NAME:-opsmate-demo}-postgres-data"
docker volume inspect "$volume_name" >/dev/null 2>&1 \
    || fail "the persistent PostgreSQL volume is missing"

# The Compose edge must be stopped and its loopback live marker must disappear.
if command -v curl >/dev/null 2>&1 && [ -n "${OPSMATE_EDGE_HOST_PORT:-}" ]; then
    local_headers=$(mktemp)
    trap 'rm -f "$local_headers"' EXIT HUP INT TERM
    if curl \
        --silent \
        --output /dev/null \
        --dump-header "$local_headers" \
        --head \
        --connect-timeout 2 \
        --max-time 5 \
        "http://127.0.0.1:${OPSMATE_EDGE_HOST_PORT}/demo"
    then
        if grep -qi '^X-OpsMate-Demo:[[:space:]]*live' "$local_headers"; then
            fail "the NAS loopback live edge marker is still reachable"
        fi
    fi
fi

# When public ingress exists, closing the stack must also remove the live marker
# from the public origin. An unavailable origin is an acceptable closed result.
if command -v curl >/dev/null 2>&1 && [ -n "${DEMO_DOMAIN:-}" ]; then
    public_headers=$(mktemp)
    trap 'rm -f "${local_headers:-}" "$public_headers"' EXIT HUP INT TERM
    public_port=${DEMO_PUBLIC_PORT:-443}
    if [ "$public_port" = "443" ]; then
        public_url="https://${DEMO_DOMAIN}/demo"
    else
        public_url="https://${DEMO_DOMAIN}:${public_port}/demo"
    fi
    if curl \
        --silent \
        --output /dev/null \
        --dump-header "$public_headers" \
        --head \
        --connect-timeout 5 \
        --max-time 10 \
        "$public_url"
    then
        if grep -qi '^X-OpsMate-Demo:[[:space:]]*live' "$public_headers"; then
            fail "the external live edge marker is still reachable"
        fi
    fi
fi

printf '%s\n' "verify-closed: edge, app, model tunnel and database are stopped; database volume retained"
