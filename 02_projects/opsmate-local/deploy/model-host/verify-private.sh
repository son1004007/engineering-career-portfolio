#!/bin/sh

set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
COMPOSE_FILE="$SCRIPT_DIR/compose.yml"
ENV_FILE=${OPSMATE_MODEL_ENV_FILE:-"$SCRIPT_DIR/.env"}
MODE=${1:-open}

fail() {
    printf '%s\n' "verify-private: $1" >&2
    exit 1
}

[ -f "$ENV_FILE" ] || fail "environment file is missing"
set -a
# shellcheck disable=SC1090
. "$ENV_FILE"
set +a

case "$MODE" in
    open | --closed) ;;
    *) fail "supported modes are open and --closed" ;;
esac

for command_name in docker grep curl
do
    command -v "$command_name" >/dev/null 2>&1 || fail "$command_name is required"
done

compose() {
    docker compose --env-file "$ENV_FILE" --file "$COMPOSE_FILE" "$@"
}

volume_name="${COMPOSE_PROJECT_NAME:-opsmate-model-host}-model-data"

if [ "$MODE" = "--closed" ]; then
    running_services=$(compose ps --status running --services 2>/dev/null || true)
    for service in proxy ollama
    do
        if printf '%s\n' "$running_services" | grep -qx "$service"; then
            fail "$service is still running"
        fi
    done

    docker volume inspect "$volume_name" >/dev/null 2>&1 \
        || fail "the persistent model volume is missing"

    if command -v ss >/dev/null 2>&1; then
        if ss -H -ltn 'sport = :11434' 2>/dev/null | grep -q .
        then
            fail "a listener remains on the model proxy port"
        fi
    fi

    closed_status=$(curl \
        --noproxy '*' \
        --silent \
        --output /dev/null \
        --write-out '%{http_code}' \
        --connect-timeout 3 \
        --max-time 5 \
        "http://${MODEL_BIND_ADDRESS}:11434/api/tags" || true)
    [ "$closed_status" = "000" ] \
        || fail "the approved private model endpoint is still reachable"

    printf '%s\n' "verify-private: proxy and Ollama are stopped; model volume retained"
    exit 0
fi

case "${MODEL_BIND_ADDRESS:-}" in
    10.* | 192.168.* | 172.1[6-9].* | 172.2[0-9].* | 172.3[01].* \
    | 100.6[4-9].* | 100.[7-9][0-9].* | 100.1[01][0-9].* | 100.12[0-7].*)
        ;;
    *) fail "the model proxy is not configured for a private VPN IPv4 address" ;;
esac

proxy_id=$(compose ps --quiet proxy)
ollama_id=$(compose ps --quiet ollama)
[ -n "$proxy_id" ] || fail "proxy container is not running"
[ -n "$ollama_id" ] || fail "Ollama container is not running"

proxy_binding=$(docker inspect \
    --format '{{range $binding := index .NetworkSettings.Ports "8080/tcp"}}{{$binding.HostIp}}:{{$binding.HostPort}}{{"\n"}}{{end}}' \
    "$proxy_id")
[ "$proxy_binding" = "${MODEL_BIND_ADDRESS}:11434" ] \
    || fail "the proxy is not bound exclusively to the approved VPN address"

ollama_binding=$(docker inspect \
    --format '{{with index .NetworkSettings.Ports "11434/tcp"}}{{range .}}{{.HostIp}}:{{.HostPort}}{{end}}{{end}}' \
    "$ollama_id")
[ -z "$ollama_binding" ] || fail "Ollama has an unexpected host port binding"

if command -v ss >/dev/null 2>&1; then
    if ss -H -ltn 'sport = :11434' 2>/dev/null \
        | grep -Eq '(^|[[:space:]])(0\.0\.0\.0|\*|\[::\]|::):11434([[:space:]]|$)'
    then
        fail "the model proxy port is listening on a wildcard/public address"
    fi
fi

base_url="http://${MODEL_BIND_ADDRESS}:11434"
unauthorized_status=$(curl \
    --noproxy '*' \
    --silent \
    --output /dev/null \
    --write-out '%{http_code}' \
    --connect-timeout 5 \
    --max-time 10 \
    "${base_url}/api/tags" || true)
[ "$unauthorized_status" = "401" ] \
    || fail "a request without the Bearer token was not rejected with 401"

authorized_status=$(printf 'header = "Authorization: Bearer %s"\n' "$MODEL_PROXY_TOKEN" \
    | curl \
        --config - \
        --noproxy '*' \
        --silent \
        --output /dev/null \
        --write-out '%{http_code}' \
        --connect-timeout 5 \
        --max-time 15 \
        "${base_url}/api/tags" || true)
[ "$authorized_status" = "200" ] \
    || fail "the authenticated private model health request failed"

docker volume inspect "$volume_name" >/dev/null 2>&1 \
    || fail "the persistent model volume is missing"

printf '%s\n' "verify-private: private bind, Bearer enforcement, hidden Ollama port, and model volume passed"
