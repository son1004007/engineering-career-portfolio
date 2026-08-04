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

load_environment
command -v docker >/dev/null 2>&1 || fail "docker is required"
command -v tr >/dev/null 2>&1 || fail "tr is required"
docker info >/dev/null 2>&1 || fail "docker daemon is unavailable"
compose config --quiet >/dev/null 2>&1 || fail "docker compose configuration is invalid"

printf '%s\n' "close-demo: stopping the public Caddy service"
compose stop --timeout 30 caddy >/dev/null 2>&1 || fail "public Caddy service could not be stopped"

printf '%s\n' "close-demo: draining and stopping the application before data deletion"
compose stop --timeout 45 app >/dev/null 2>&1 || fail "application service could not be stopped"

# 삭제 전에 외부 진입점과 쓰기 주체가 모두 실제로 중단됐음을 확인한다.
# 이 경계를 생략하면 stop 오류 뒤에도 진행되어, 새 트랜잭션과 합성 데이터 삭제가 경합할 수 있다.
still_running=$(compose ps --status running --services caddy app 2>/dev/null || fail "stopped-service state could not be verified")
[ -z "$still_running" ] || fail "Caddy or application is still running; synthetic data was not deleted"

# No application transaction can race with the purge after the graceful stop completes.
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

printf '%s\n' "close-demo: public app closed and synthetic rows deleted; PostgreSQL volume retained"
printf '%s\n' "close-demo: close and verify the separate model host to release its GPU"
