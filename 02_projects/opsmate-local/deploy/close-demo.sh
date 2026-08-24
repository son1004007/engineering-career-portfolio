#!/bin/sh

set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
COMPOSE_FILE="$SCRIPT_DIR/compose.yml"
ENV_FILE=${OPSMATE_ENV_FILE:-"$SCRIPT_DIR/.env"}

fail() {
    printf '%s\n' "close-demo: $1" >&2
    exit 1
}

load_environment() {
    [ -f "$ENV_FILE" ] || fail "environment file is missing"
    set -a
    # shellcheck disable=SC1090
    . "$ENV_FILE"
    set +a
}

compose() {
    docker compose --env-file "$ENV_FILE" --file "$COMPOSE_FILE" "$@"
}

remove_tunnel_secret_material() {
    compose rm --force --stop model-tunnel tunnel-secret-init >/dev/null 2>&1 \
        || fail "tunnel containers could not be removed"
    secret_volume="${COMPOSE_PROJECT_NAME:-opsmate-demo}-tunnel-secrets"
    if docker volume inspect "$secret_volume" >/dev/null 2>&1; then
        docker volume rm "$secret_volume" >/dev/null \
            || fail "ephemeral tunnel secret volume could not be removed"
    fi
}

load_environment
command -v docker >/dev/null 2>&1 || fail "docker is required"
command -v tr >/dev/null 2>&1 || fail "tr is required"
docker info >/dev/null 2>&1 || fail "docker daemon is unavailable"
compose config --quiet >/dev/null 2>&1 || fail "docker compose configuration is invalid"

printf '%s\n' "close-demo: stopping the loopback Nginx edge"
compose stop --timeout 30 edge >/dev/null 2>&1 || fail "loopback edge could not be stopped"

printf '%s\n' "close-demo: draining and stopping the application before data deletion"
compose stop --timeout 45 app >/dev/null 2>&1 || fail "application service could not be stopped"

printf '%s\n' "close-demo: stopping the Office model tunnel"
compose stop --timeout 15 model-tunnel >/dev/null 2>&1 || fail "model tunnel could not be stopped"
remove_tunnel_secret_material

# The edge, write-capable app and model transport must all be closed before
# synthetic data is purged. This avoids late writes and ensures the model path
# is not left open after the demo closes.
still_running=$(compose ps --status running --services edge app model-tunnel tunnel-secret-init 2>/dev/null || fail "stopped-service state could not be verified")
[ -z "$still_running" ] || fail "edge, application or model tunnel is still running; synthetic data was not deleted"

purge_failed=0
if ! compose up --detach --wait --wait-timeout "${DB_WAIT_SECONDS:-90}" db >/dev/null; then
    purge_failed=1
else
    printf '%s\n' "close-demo: deleting synthetic demo workspaces"
    if ! compose exec --no-TTY db \
        psql \
        --username "$POSTGRES_ADMIN_USER" \
        --dbname "$POSTGRES_DB" \
        --set ON_ERROR_STOP=1 \
        --command "TRUNCATE TABLE public.demo_workspaces CASCADE;" \
        >/dev/null
    then
        purge_failed=1
    else
        remaining=$(compose exec --no-TTY db \
            psql \
            --username "$POSTGRES_ADMIN_USER" \
            --dbname "$POSTGRES_DB" \
            --tuples-only \
            --no-align \
            --command "SELECT COUNT(*) FROM public.demo_workspaces;" \
            2>/dev/null | tr -d '[:space:]')
        [ "$remaining" = "0" ] || purge_failed=1
    fi
fi

compose stop --timeout 10 migrate >/dev/null 2>&1 || fail "migration service could not be stopped"

printf '%s\n' "close-demo: stopping PostgreSQL without deleting its volume"
compose stop --timeout 30 db >/dev/null 2>&1 || fail "PostgreSQL service could not be stopped"

"$SCRIPT_DIR/verify-closed.sh"

if [ "$purge_failed" -ne 0 ]; then
    fail "services are stopped, but synthetic data deletion could not be verified"
fi

printf '%s\n' "close-demo: edge, app, model tunnel and database are closed; tunnel secrets removed, synthetic rows deleted and PostgreSQL volume retained"
