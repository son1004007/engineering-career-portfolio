#!/bin/sh

set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
ENV_FILE=${OPSMATE_ENV_FILE:-"$SCRIPT_DIR/.env"}

fail() {
    printf '%s\n' "smoke-test: $1" >&2
    exit 1
}

[ -f "$ENV_FILE" ] || fail "environment file is missing"
set -a
# shellcheck disable=SC1090
. "$ENV_FILE"
set +a

command -v curl >/dev/null 2>&1 || fail "curl is required"
command -v grep >/dev/null 2>&1 || fail "grep is required"
command -v sed >/dev/null 2>&1 || fail "sed is required"
[ -n "${DEMO_DOMAIN:-}" ] || fail "DEMO_DOMAIN is required"
case "$DEMO_DOMAIN" in
    *.invalid*) fail "DEMO_DOMAIN still contains a placeholder" ;;
esac

public_port=${DEMO_PUBLIC_PORT:-443}
printf '%s' "$public_port" | grep -Eq '^[0-9]{1,5}$' || fail "DEMO_PUBLIC_PORT must be numeric"
[ "$public_port" -ge 1 ] && [ "$public_port" -le 65535 ] || fail "DEMO_PUBLIC_PORT is outside the valid TCP port range"

if [ "$public_port" = "443" ]; then
    base_url="https://${DEMO_DOMAIN}"
else
    base_url="https://${DEMO_DOMAIN}:${public_port}"
fi

headers_file=$(mktemp)
cookie_file=$(mktemp)
page_file=$(mktemp)
trap 'rm -f "$headers_file" "$cookie_file" "$page_file"' EXIT HUP INT TERM

attempt=1
retries=${SMOKE_RETRIES:-18}
status=000

while [ "$attempt" -le "$retries" ]; do
    : >"$headers_file"
    if status=$(curl \
        --silent \
        --show-error \
        --output /dev/null \
        --dump-header "$headers_file" \
        --write-out '%{http_code}' \
        --connect-timeout 5 \
        --max-time 15 \
        "${base_url}/")
    then
        case "$status" in
            2?? | 3??) break ;;
        esac
    else
        status=000
    fi
    attempt=$((attempt + 1))
    if [ "$attempt" -le "$retries" ]; then
        sleep "${SMOKE_RETRY_SECONDS:-5}"
    fi
done

case "$status" in
    2?? | 3??) ;;
    *) fail "the public HTTPS root did not become ready" ;;
esac

grep -qi '^X-OpsMate-Demo:[[:space:]]*live' "$headers_file" \
    || fail "the live edge marker header is missing"

# Standard HTTPS deployments also prove port-80 redirect behavior. A non-standard
# public HTTPS port does not imply that router port 80 is intentionally exposed.
if [ "$public_port" = "443" ]; then
    http_status=$(curl \
        --silent \
        --output /dev/null \
        --write-out '%{http_code}' \
        --connect-timeout 5 \
        --max-time 10 \
        "http://${DEMO_DOMAIN}/" || true)
    case "$http_status" in
        301 | 302 | 307 | 308) ;;
        *) fail "plain HTTP was not redirected to HTTPS" ;;
    esac
fi

api_status=$(curl \
    --silent \
    --output /dev/null \
    --dump-header "$headers_file" \
    --write-out '%{http_code}' \
    --connect-timeout 5 \
    --max-time 10 \
    "${base_url}/api/audit-events" || true)
[ "$api_status" = "403" ] \
    || fail "the public demo must deny the local Basic API with 403"
if grep -qi '^WWW-Authenticate:[[:space:]]*Basic' "$headers_file"; then
    fail "the public demo exposed a Basic authentication challenge"
fi

extract_csrf() {
    sed -n 's/.*name="_csrf"[^>]*value="\([^"]*\)".*/\1/p' "$1" | sed -n '1p'
}

refresh_dashboard() {
    curl \
        --silent \
        --show-error \
        --fail \
        --cookie "$cookie_file" \
        --cookie-jar "$cookie_file" \
        --output "$page_file" \
        --connect-timeout 5 \
        --max-time 20 \
        "${base_url}/demo"
}

post_status() {
    endpoint=$1
    shift
    curl \
        --silent \
        --show-error \
        --output /dev/null \
        --write-out '%{http_code}' \
        --cookie "$cookie_file" \
        --cookie-jar "$cookie_file" \
        --header "Origin: ${base_url}" \
        --connect-timeout 5 \
        --max-time 90 \
        "$@" \
        "${base_url}${endpoint}"
}

require_cookie_attribute() {
    cookie_name=$1
    attribute=$2
    cookie_header=$(grep -i "^Set-Cookie:[[:space:]]*${cookie_name}=" "$headers_file" | sed -n '1p')
    [ -n "$cookie_header" ] || fail "the ${cookie_name} cookie is missing"
    printf '%s' "$cookie_header" | grep -qi "${attribute}" \
        || fail "the ${cookie_name} cookie is missing ${attribute}"
}

# Execute one complete synthetic persona flow so a wrong model name or broken
# /api/chat cannot be reported as a successful public open.
curl \
    --silent \
    --show-error \
    --fail \
    --cookie-jar "$cookie_file" \
    --dump-header "$headers_file" \
    --output "$page_file" \
    --connect-timeout 5 \
    --max-time 20 \
    "${base_url}/"
require_cookie_attribute "XSRF-TOKEN" "Secure"
require_cookie_attribute "XSRF-TOKEN" "HttpOnly"
require_cookie_attribute "XSRF-TOKEN" "SameSite=Lax"
csrf=$(extract_csrf "$page_file")
[ -n "$csrf" ] || fail "the landing CSRF token is missing"

: >"$headers_file"
session_status=$(post_status "/demo/sessions" \
    --dump-header "$headers_file" \
    --data-urlencode "_csrf=${csrf}")
[ "$session_status" = "302" ] || fail "a synthetic demo session could not be started"
require_cookie_attribute "JSESSIONID" "Secure"
require_cookie_attribute "JSESSIONID" "HttpOnly"
require_cookie_attribute "JSESSIONID" "SameSite=Lax"
refresh_dashboard
csrf=$(extract_csrf "$page_file")
[ -n "$csrf" ] || fail "the dashboard CSRF token is missing"

draft_status=$(post_status "/demo/drafts" \
    --data-urlencode "_csrf=${csrf}" \
    --data-urlencode "requestText=개발용 노트북 1대를 2500000원에 구매하고 싶습니다.")
[ "$draft_status" = "302" ] || fail "the real-model draft request did not redirect"
refresh_dashboard
grep -Fq '정책 근거가 연결된 구매 초안을 생성했습니다.' "$page_file" \
    || fail "the real model did not produce a server-validated draft"
request_id=$(grep -oE '/demo/requests/[0-9a-f-]{36}/submit' "$page_file" \
    | sed -n '1{s#/demo/requests/##;s#/submit##;p;}')
[ -n "$request_id" ] || fail "the synthetic purchase request ID is missing"
csrf=$(extract_csrf "$page_file")

submit_status=$(post_status "/demo/requests/${request_id}/submit" --data-urlencode "_csrf=${csrf}")
[ "$submit_status" = "302" ] || fail "the synthetic request could not be submitted"
refresh_dashboard
csrf=$(extract_csrf "$page_file")
persona_status=$(post_status "/demo/personas" \
    --data-urlencode "_csrf=${csrf}" \
    --data-urlencode "persona=APPROVER")
[ "$persona_status" = "302" ] || fail "the approver persona could not be selected"
refresh_dashboard
csrf=$(extract_csrf "$page_file")

decision_status=$(post_status "/demo/requests/${request_id}/decisions" \
    --data-urlencode "_csrf=${csrf}" \
    --data-urlencode "decision=APPROVE")
[ "$decision_status" = "302" ] || fail "the synthetic request could not be approved"
refresh_dashboard
csrf=$(extract_csrf "$page_file")
persona_status=$(post_status "/demo/personas" \
    --data-urlencode "_csrf=${csrf}" \
    --data-urlencode "persona=BUYER")
[ "$persona_status" = "302" ] || fail "the buyer persona could not be selected"
refresh_dashboard
csrf=$(extract_csrf "$page_file")

order_status=$(post_status "/demo/orders" \
    --data-urlencode "_csrf=${csrf}" \
    --data-urlencode "purchaseRequestId=${request_id}")
[ "$order_status" = "302" ] || fail "the synthetic purchase order could not be created"
refresh_dashboard
csrf=$(extract_csrf "$page_file")
persona_status=$(post_status "/demo/personas" \
    --data-urlencode "_csrf=${csrf}" \
    --data-urlencode "persona=AUDITOR")
[ "$persona_status" = "302" ] || fail "the auditor persona could not be selected"
refresh_dashboard
grep -Fq 'ORDER_CREATED' "$page_file" \
    || fail "the completed synthetic flow is missing its audit evidence"
csrf=$(extract_csrf "$page_file")

end_status=$(post_status "/demo/end" --data-urlencode "_csrf=${csrf}")
[ "$end_status" = "302" ] || fail "the synthetic smoke workspace could not be deleted"

printf '%s\n' "smoke-test: HTTPS, API denial, real-model draft, persona workflow, audit, and cleanup passed"
