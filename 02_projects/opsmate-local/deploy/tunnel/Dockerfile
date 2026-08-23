FROM alpine:3.21.3@sha256:1c4eef651f65e2f7daee7ee785882ac164b02b78fb74503052a26dc061c90474

ARG TUNNEL_UID=10001
ARG TUNNEL_GID=10001

RUN apk add --no-cache openssh-client curl \
    && addgroup -g "${TUNNEL_GID}" tunnel \
    && adduser -D -u "${TUNNEL_UID}" -G tunnel tunnel

COPY --chown=tunnel:tunnel tunnel-entrypoint.sh /usr/local/bin/tunnel-entrypoint.sh
RUN chmod 0555 /usr/local/bin/tunnel-entrypoint.sh

USER tunnel:tunnel

ENTRYPOINT ["/usr/local/bin/tunnel-entrypoint.sh"]
