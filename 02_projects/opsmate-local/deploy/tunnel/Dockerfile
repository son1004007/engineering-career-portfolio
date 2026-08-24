FROM alpine:3.21.3@sha256:1c4eef651f65e2f7daee7ee785882ac164b02b78fb74503052a26dc061c90474

ARG TUNNEL_UID=10001
ARG TUNNEL_GID=10001

LABEL org.opencontainers.image.source="https://github.com/son1004007/engineering-career-portfolio"
LABEL org.opencontainers.image.description="Restricted SSH tunnel and loopback-host normalizer for OpsMate Local model traffic"

RUN apk add --no-cache openssh-client curl nginx \
    && addgroup -g "${TUNNEL_GID}" tunnel \
    && adduser -D -u "${TUNNEL_UID}" -G tunnel tunnel

COPY --chown=tunnel:tunnel tunnel-entrypoint.sh /usr/local/bin/tunnel-entrypoint.sh
COPY --chown=tunnel:tunnel tunnel-nginx.conf /etc/nginx/tunnel-nginx.conf
RUN chmod 0555 /usr/local/bin/tunnel-entrypoint.sh \
    && chmod 0444 /etc/nginx/tunnel-nginx.conf

USER tunnel:tunnel

ENTRYPOINT ["/usr/local/bin/tunnel-entrypoint.sh"]
