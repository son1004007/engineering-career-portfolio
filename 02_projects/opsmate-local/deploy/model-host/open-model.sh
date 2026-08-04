#!/bin/sh

set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
COMPOSE_FILE="$SCRIPT_DIR/compose.yml"
ENV_FILE=${OPSMATE_MODEL_ENV_FILE:-"$SCRIPT_DIR/.env"}

fail() {
    printf '%s\n' "open-model: $1" >&2
    exit 1
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

stop_on_failure() {
    status=$?
    trap - EXIT HUP INT TERM
    if [ "$status" -ne 0 ]; then
        cleanup_ok=1
        if ! compose stop --timeout 20 proxy >/dev/null 2>&1; then
            cleanup_ok=0
        fi
        if ! compose stop --timeout 30 ollama >/dev/null 2>&1; then
            cleanup_ok=0
        fi
        if ! "$SCRIPT_DIR/verify-private.sh" --closed >/dev/null 2>&1; then
            cleanup_ok=0
        fi

        if [ "$cleanup_ok" -eq 1 ]; then
            printf '%s\n' "open-model: failed; proxy and Ollama closure was verified, model volume retained" >&2
        else
            printf '%s\n' "open-model: failed; automatic closure could not be verified, inspect the proxy and GPU before retrying" >&2
        fi
    fi
    exit "$status"
}

validate_private_address() {
    case "$MODEL_BIND_ADDRESS" in
        10.* | 192.168.* | 172.1[6-9].* | 172.2[0-9].* | 172.3[01].* \
        | 100.6[4-9].* | 100.[7-9][0-9].* | 100.1[01][0-9].* | 100.12[0-7].*)
            ;;
        *)
            fail "MODEL_BIND_ADDRESS must be an approved RFC1918 or VPN CGNAT IPv4 address"
            ;;
    esac

    printf '%s' "$MODEL_VPN_INTERFACE" | grep -Eq '^[A-Za-z0-9_.:-]+$' \
        || fail "MODEL_VPN_INTERFACE contains unsupported characters"

    ip -o -4 address show dev "$MODEL_VPN_INTERFACE" 2>/dev/null \
        | awk '{print $4}' \
        | cut -d/ -f1 \
        | grep -Fx "$MODEL_BIND_ADDRESS" >/dev/null \
        || fail "MODEL_BIND_ADDRESS is not assigned to the approved VPN interface"
}

inspect_gpu() {
    printf '%s' "$OPSMATE_GPU_DEVICE_ID" \
        | grep -Eq '^([0-9]+|GPU-[A-Fa-f0-9-]+)$' \
        || fail "OPSMATE_GPU_DEVICE_ID must be one GPU index or full GPU UUID"

    gpu_info=$(nvidia-smi \
        --id="$OPSMATE_GPU_DEVICE_ID" \
        --query-gpu=name,memory.total,driver_version \
        --format=csv,noheader,nounits \
        2>/dev/null) || fail "the selected GPU could not be queried with nvidia-smi"

    [ "$(printf '%s\n' "$gpu_info" | wc -l | tr -d ' ')" -eq 1 ] \
        || fail "the GPU selector must resolve to exactly one device"

    gpu_name=$(printf '%s\n' "$gpu_info" | cut -d, -f1 | sed 's/^ *//;s/ *$//')
    gpu_vram=$(printf '%s\n' "$gpu_info" | cut -d, -f2 | sed 's/^ *//;s/ *$//')
    gpu_driver=$(printf '%s\n' "$gpu_info" | cut -d, -f3 | sed 's/^ *//;s/ *$//')

    [ -n "$gpu_name" ] && [ -n "$gpu_vram" ] && [ -n "$gpu_driver" ] \
        || fail "nvidia-smi returned incomplete GPU inventory"

    printf '%s\n' "open-model: GPU verified (model=$gpu_name, vramMiB=$gpu_vram, driver=$gpu_driver)"
}

preflight() {
    for command_name in docker curl ip awk cut grep sed tr nvidia-smi
    do
        command -v "$command_name" >/dev/null 2>&1 || fail "$command_name is required"
    done

    docker info >/dev/null 2>&1 || fail "docker daemon is unavailable"
    docker compose version >/dev/null 2>&1 || fail "docker compose v2 is required"
    docker info --format '{{json .Runtimes}}' 2>/dev/null \
        | grep -q '"nvidia"' \
        || fail "NVIDIA Container Toolkit runtime is unavailable"

    for name in \
        COMPOSE_PROJECT_NAME \
        MODEL_BIND_ADDRESS \
        MODEL_VPN_INTERFACE \
        OPSMATE_GPU_DEVICE_ID \
        OLLAMA_IMAGE \
        CADDY_IMAGE \
        OPSMATE_LLM_MODEL \
        OPSMATE_LLM_MODEL_ID \
        MODEL_PROXY_TOKEN
    do
        required "$name"
    done

    printf '%s' "$MODEL_PROXY_TOKEN" | grep -Eq '^[A-Za-z0-9._~-]{32,}$' \
        || fail "MODEL_PROXY_TOKEN must contain at least 32 URL-safe characters"
    printf '%s' "$OLLAMA_IMAGE" | grep -Eq '^[-A-Za-z0-9._/]+@sha256:[A-Fa-f0-9]{64}$' \
        || fail "OLLAMA_IMAGE must be pinned by a full sha256 digest"
    printf '%s' "$CADDY_IMAGE" | grep -Eq '^[-A-Za-z0-9._/:]+@sha256:[A-Fa-f0-9]{64}$' \
        || fail "CADDY_IMAGE must be pinned by a full sha256 digest"
    printf '%s' "$OPSMATE_LLM_MODEL" | grep -Eq '^[A-Za-z0-9][A-Za-z0-9._/-]*:[A-Za-z0-9][A-Za-z0-9._-]*$' \
        || fail "OPSMATE_LLM_MODEL must include an explicit safe tag"
    printf '%s' "$OPSMATE_LLM_MODEL_ID" | grep -Eq '^[A-Fa-f0-9]{12,64}$' \
        || fail "OPSMATE_LLM_MODEL_ID must be the approved model content ID"

    validate_private_address
    inspect_gpu

    compose config --quiet >/dev/null 2>&1 \
        || fail "docker compose configuration is invalid"
}

load_environment

# This approval gate intentionally runs before Docker, GPU, network, or model operations.
[ "${OPSMATE_PRIVATE_GPU_APPROVED:-}" = "YES" ] \
    || fail "explicit private GPU host approval is required"

trap stop_on_failure EXIT HUP INT TERM

preflight

# Never leave a previously running proxy open while the model is being checked or pulled.
compose stop --timeout 20 proxy >/dev/null 2>&1 || fail "existing model proxy could not be stopped"

printf '%s\n' "open-model: starting Ollama on the selected single GPU"
compose up --detach --wait --wait-timeout "${MODEL_WAIT_SECONDS:-180}" ollama

printf '%s\n' "open-model: pulling the approved model"
if ! compose exec --no-TTY ollama ollama pull "$OPSMATE_LLM_MODEL" >/dev/null 2>&1; then
    fail "the approved model could not be pulled"
fi

if ! compose exec --no-TTY ollama ollama show "$OPSMATE_LLM_MODEL" >/dev/null 2>&1; then
    fail "the approved model did not pass the local health check"
fi
actual_model_id=$(compose exec --no-TTY ollama ollama list \
    | awk -v model="$OPSMATE_LLM_MODEL" 'NR > 1 && $1 == model {print $2}' \
    | tr -d '\r' \
    | sed -n '1p')
[ -n "$actual_model_id" ] || fail "the approved model was not found in the local model inventory"
[ "$(printf '%s' "$actual_model_id" | tr '[:upper:]' '[:lower:]')" = \
    "$(printf '%s' "$OPSMATE_LLM_MODEL_ID" | tr '[:upper:]' '[:lower:]')" ] \
    || fail "the pulled model content ID does not match the approved model"

printf '%s\n' "open-model: model health passed; starting the VPN-bound proxy"
compose up --detach --wait --wait-timeout "${PROXY_WAIT_SECONDS:-60}" proxy

"$SCRIPT_DIR/verify-private.sh"

trap - EXIT HUP INT TERM
printf '%s\n' "open-model: private authenticated model proxy is ready"
