#!/bin/sh

set -eu

PROJECT_NAME=${1:-${OPSMATE_COMPOSE_PROJECT_NAME:-opsmate-demo}}

fail() {
    printf '%s\n' "emergency-close: $1" >&2
    exit 1
}

printf '%s' "$PROJECT_NAME" | grep -Eq '^[A-Za-z0-9][A-Za-z0-9_.-]{0,62}$' \
    || fail "compose project name is invalid"
command -v docker >/dev/null 2>&1 || fail "docker is required"
docker info >/dev/null 2>&1 || fail "docker daemon is unavailable"

running_ids() {
    docker ps --quiet \
        --filter "label=com.docker.compose.project=$PROJECT_NAME" \
        --filter "label=com.docker.compose.service=$1"
}

stop_service() {
    service=$1
    timeout=$2
    ids=$(running_ids "$service")
    [ -n "$ids" ] || return 0

    for id in $ids
    do
        actual_project=$(docker inspect --format '{{ index .Config.Labels "com.docker.compose.project" }}' "$id")
        actual_service=$(docker inspect --format '{{ index .Config.Labels "com.docker.compose.service" }}' "$id")
        [ "$actual_project" = "$PROJECT_NAME" ] && [ "$actual_service" = "$service" ] \
            || fail "container label verification failed for $id"
        name=$(docker inspect --format '{{.Name}}' "$id" | sed 's#^/##')
        printf '%s\n' "emergency-close: stopping $name"
        docker stop --time "$timeout" "$id" >/dev/null \
            || fail "$service container $id could not be stopped"
    done
}

# External entry, write-capable application and model transport are closed first.
# Credential-independent emergency close intentionally does not purge data.
stop_service edge 30
stop_service app 45
stop_service model-tunnel 15
stop_service migrate 10
stop_service db 30

for service in edge app model-tunnel migrate db
do
    [ -z "$(running_ids "$service")" ] || fail "$service is still running"
done

printf '%s\n' "emergency-close: edge, app and model-tunnel closure verified for project $PROJECT_NAME"
printf '%s\n' "emergency-close: synthetic rows were not purged; recover the env file and run normal close before reopen"
