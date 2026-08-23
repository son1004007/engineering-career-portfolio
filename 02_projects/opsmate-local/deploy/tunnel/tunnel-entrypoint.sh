#!/bin/sh

set -eu

fail() {
    printf '%s\n' "model-tunnel: $1" >&2
    exit 1
}

required() {
    name=$1
    value=$(printenv "$name" 2>/dev/null || true)
    [ -n "$value" ] || fail "required environment variable $name is empty"
}

required OFFICE_SSH_HOST
required OFFICE_SSH_PORT
required OFFICE_SSH_USER

printf '%s' "$OFFICE_SSH_HOST" | grep -Eq '^[A-Za-z0-9][A-Za-z0-9.-]{0,252}$' \
    || fail "OFFICE_SSH_HOST contains unsupported characters"
printf '%s' "$OFFICE_SSH_USER" | grep -Eq '^[A-Za-z0-9_][A-Za-z0-9_.-]{0,63}$' \
    || fail "OFFICE_SSH_USER contains unsupported characters"
printf '%s' "$OFFICE_SSH_PORT" | grep -Eq '^[0-9]{1,5}$' \
    || fail "OFFICE_SSH_PORT must be numeric"
[ "$OFFICE_SSH_PORT" -ge 1 ] && [ "$OFFICE_SSH_PORT" -le 65535 ] \
    || fail "OFFICE_SSH_PORT is outside the valid TCP port range"

KEY_SOURCE=/run/secrets/office_tunnel_key
KNOWN_HOSTS=/run/secrets/office_known_hosts
KEY_FILE=/tmp/office_tunnel_key

[ -s "$KEY_SOURCE" ] || fail "office tunnel private key secret is missing"
[ -s "$KNOWN_HOSTS" ] || fail "office known_hosts secret is missing"

umask 077
cp "$KEY_SOURCE" "$KEY_FILE"
chmod 600 "$KEY_FILE"
ssh-keygen -y -f "$KEY_FILE" >/dev/null 2>&1 \
    || fail "office tunnel private key is invalid"

# The NAS trust store contains the reviewed Office Ed25519 host key only. Pin the
# client algorithm as well so an older SSH client cannot prefer another Office
# host-key type and bypass that exact trust boundary.
#
# The tunnel exposes Ollama only to the Docker-internal model_link network. The
# Office-side target remains loopback-only; no Ollama public listener is created.
printf '%s\n' "model-tunnel: opening restricted SSH forwarding path"
exec ssh \
    -F /dev/null \
    -N \
    -T \
    -i "$KEY_FILE" \
    -p "$OFFICE_SSH_PORT" \
    -o BatchMode=yes \
    -o IdentitiesOnly=yes \
    -o PasswordAuthentication=no \
    -o StrictHostKeyChecking=yes \
    -o HostKeyAlgorithms=ssh-ed25519 \
    -o UserKnownHostsFile="$KNOWN_HOSTS" \
    -o ExitOnForwardFailure=yes \
    -o ServerAliveInterval=30 \
    -o ServerAliveCountMax=3 \
    -L 0.0.0.0:11434:127.0.0.1:11434 \
    -- "$OFFICE_SSH_USER@$OFFICE_SSH_HOST"
