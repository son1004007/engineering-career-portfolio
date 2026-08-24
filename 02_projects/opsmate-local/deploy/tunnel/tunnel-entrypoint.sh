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
NGINX_CONF=/etc/nginx/tunnel-nginx.conf

[ -s "$KEY_SOURCE" ] || fail "office tunnel private key secret is missing"
[ -s "$KNOWN_HOSTS" ] || fail "office known_hosts secret is missing"
[ -r "$NGINX_CONF" ] || fail "tunnel nginx configuration is missing"

umask 077
cp "$KEY_SOURCE" "$KEY_FILE"
chmod 600 "$KEY_FILE"
ssh-keygen -y -f "$KEY_FILE" >/dev/null 2>&1 \
    || fail "office tunnel private key is invalid"
nginx -t -c "$NGINX_CONF" >/dev/null 2>&1 \
    || fail "tunnel nginx configuration is invalid"

# Ollama validates browser/host origins and accepts loopback hosts by default.
# Docker clients address this service as model-tunnel, so a raw TCP forward would
# preserve Host: model-tunnel:11434 and can be rejected with HTTP 403. Keep the
# Office daemon unchanged: Nginx accepts only the Docker-internal model_link
# traffic on 11434, rewrites Host to the Office loopback authority, and proxies
# to the SSH forward bound only on this container's loopback port 11435.
printf '%s\n' "model-tunnel: opening restricted SSH forwarding path"
ssh \
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
    -L 127.0.0.1:11435:127.0.0.1:11434 \
    -- "$OFFICE_SSH_USER@$OFFICE_SSH_HOST" &
ssh_pid=$!

nginx -c "$NGINX_CONF" -g 'daemon off;' &
nginx_pid=$!

shutdown() {
    trap - HUP INT TERM
    kill "$nginx_pid" "$ssh_pid" >/dev/null 2>&1 || true
    wait "$nginx_pid" >/dev/null 2>&1 || true
    wait "$ssh_pid" >/dev/null 2>&1 || true
    exit 0
}
trap shutdown HUP INT TERM

# No full supervisor is needed for this two-process container. Poll both bounded
# local processes; if either the SSH transport or Host-normalizing proxy exits,
# terminate the peer and fail the container so Compose health/dependency gates
# cannot treat a half-open model path as healthy.
while kill -0 "$ssh_pid" >/dev/null 2>&1 && kill -0 "$nginx_pid" >/dev/null 2>&1; do
    sleep 1
done

trap - HUP INT TERM
kill "$nginx_pid" "$ssh_pid" >/dev/null 2>&1 || true
wait "$nginx_pid" >/dev/null 2>&1 || true
wait "$ssh_pid" >/dev/null 2>&1 || true
fail "SSH transport or Host-normalizing proxy exited"
