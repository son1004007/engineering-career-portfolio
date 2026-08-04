#!/bin/sh

set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
COMPOSE_FILE="$SCRIPT_DIR/compose.yml"
ENV_FILE=${OPSMATE_MODEL_ENV_FILE:-"$SCRIPT_DIR/.env"}

fail() {
    printf '%s\n' "close-model: $1" >&2
    exit 1
}

[ -f "$ENV_FILE" ] || fail "environment file is missing"
set -a
# shellcheck disable=SC1090
. "$ENV_FILE"
set +a

command -v docker >/dev/null 2>&1 || fail "docker is required"
docker info >/dev/null 2>&1 || fail "docker daemon is unavailable"

compose() {
    docker compose --env-file "$ENV_FILE" --file "$COMPOSE_FILE" "$@"
}

compose config --quiet >/dev/null 2>&1 || fail "docker compose configuration is invalid"

printf '%s\n' "close-model: stopping the VPN-bound proxy"
compose stop --timeout 30 proxy >/dev/null 2>&1 || true

printf '%s\n' "close-model: stopping Ollama"
compose stop --timeout 60 ollama >/dev/null 2>&1 || true

"$SCRIPT_DIR/verify-private.sh" --closed

printf '%s\n' "close-model: closed; downloaded model volume retained"
