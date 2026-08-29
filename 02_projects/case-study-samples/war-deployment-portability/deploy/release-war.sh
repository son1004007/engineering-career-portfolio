#!/bin/sh
set -eu

usage() {
  echo "usage: $0 <candidate-war> <deploy-dir> <app-name> <health-url>" >&2
  exit 64
}

[ "$#" -eq 4 ] || usage

CANDIDATE_WAR=$1
DEPLOY_DIR=$2
APP_NAME=$3
HEALTH_URL=$4
CURL_BIN=${CURL_BIN:-curl}
HEALTH_ATTEMPTS=${HEALTH_ATTEMPTS:-5}
HEALTH_DELAY_SECONDS=${HEALTH_DELAY_SECONDS:-1}

case "$APP_NAME" in
  ''|*[!A-Za-z0-9._-]*)
    echo "unsafe app name" >&2
    exit 65
    ;;
esac

[ -f "$CANDIDATE_WAR" ] || { echo "candidate WAR does not exist" >&2; exit 66; }
[ -d "$DEPLOY_DIR" ] || { echo "deploy directory does not exist" >&2; exit 66; }

ACTIVE_WAR="$DEPLOY_DIR/$APP_NAME.war"
STAGED_WAR="$DEPLOY_DIR/.$APP_NAME.war.next"
ROLLBACK_DIR="$DEPLOY_DIR/.rollback"
BACKUP_WAR="$ROLLBACK_DIR/$APP_NAME.war.previous"
HAD_ACTIVE=0

cleanup() {
  rm -f "$STAGED_WAR"
}
trap cleanup EXIT HUP INT TERM

mkdir -p "$ROLLBACK_DIR"
if [ -f "$ACTIVE_WAR" ]; then
  cp "$ACTIVE_WAR" "$BACKUP_WAR"
  HAD_ACTIVE=1
else
  rm -f "$BACKUP_WAR"
fi

cp "$CANDIDATE_WAR" "$STAGED_WAR"
chmod 0644 "$STAGED_WAR"
mv "$STAGED_WAR" "$ACTIVE_WAR"

i=1
while [ "$i" -le "$HEALTH_ATTEMPTS" ]; do
  if "$CURL_BIN" -fsS "$HEALTH_URL" >/dev/null 2>&1; then
    echo "deployment health check passed"
    exit 0
  fi
  if [ "$i" -lt "$HEALTH_ATTEMPTS" ]; then
    sleep "$HEALTH_DELAY_SECONDS"
  fi
  i=$((i + 1))
done

echo "deployment health check failed; restoring previous artifact" >&2
if [ "$HAD_ACTIVE" -eq 1 ]; then
  cp "$BACKUP_WAR" "$STAGED_WAR"
  chmod 0644 "$STAGED_WAR"
  mv "$STAGED_WAR" "$ACTIVE_WAR"
else
  rm -f "$ACTIVE_WAR"
fi
exit 1
