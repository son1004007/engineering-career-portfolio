# OpsMate Local NAS Internal E2E Evidence

## Scope

This record captures the bounded internal deployment and lifecycle verification completed on `2026-08-25` for the Synology-hosted OpsMate demo path. It intentionally excludes private host addresses, usernames, SSH key material, known_hosts contents, database credentials, and organization approval text.

This evidence verifies the application/runtime boundary **before public DSM/router ingress**. It does not claim that an Internet-facing HTTPS origin has been tested.

## Reviewed release

- source commit: `f99686981da7efb8802635ae2bde5b0f781433ad`
- application image: `ghcr.io/son1004007/opsmate-local@sha256:61e267c05bf0ce0ea932ae62a3989194bd2a0065532d0ee4caee8b37c8f9d40b`
- model-tunnel image: `ghcr.io/son1004007/opsmate-model-tunnel@sha256:7fc133485a8ba60190e55b5eeb2da8b5eb02c1aa7e70e0cc0ce7b723746db1df`
- model: `gemma3:12b`

The source includes COOKIE-only public-demo session tracking, bounded PostgreSQL stop-state convergence, and final removal of a Compose-recreated ephemeral tunnel-secret volume immediately before the strict CLOSED verifier.

## Repository and image gates

Portfolio verification run `32848946968` completed successfully for the reviewed source boundary:

- OpsMate Maven verification: PASS
- Spring Security sample verification: PASS
- public portfolio consistency/public-text checks: PASS
- Jekyll build: PASS
- shell/Compose/Nginx/container/runbook checks: PASS

Immutable image publication run `32848946995`:

- exact source checkout: PASS
- registry authentication: PASS
- application image publish: PASS
- model-tunnel image publish: PASS
- immutable digest pull verification: PASS

## NAS release preparation

Bounded runtime preparation run `32849378114` verified:

- exact reviewed source and both immutable image digests: PASS
- runtime environment permissions: `600`
- retained PostgreSQL volume: present
- database credential continuity carried only from the immediately preceding approved release
- running containers after preparation: `0`
- policy flags before public opening: `NO_NO`
- final state: `CLOSED`
- preparation and DB continuity checks: PASS

## Full internal runtime E2E

Bounded internal verification run `32849533407` completed successfully.

### Startup and network/security gates

- stack start: PASS
- host-port policy: PASS
- Docker network policy: PASS
- Nginx edge security: PASS
- Nginx edge binding: loopback-only `127.0.0.1:18083`
- application host ports: none
- PostgreSQL host ports: none
- model-tunnel host ports: none
- application direct egress: blocked
- edge direct egress: blocked
- `/actuator/**` public edge response: `404`

### Session and business-flow gates

- XSRF cookie present and Secure: PASS
- JSESSIONID cookie/session persistence: PASS
- URL-based session rewriting fallback absent in the reviewed runtime path
- real internal model path through the restricted tunnel: PASS
- requester/persona business flow: PASS
- durable draft creation and redirect flow: PASS
- cross-workspace isolation: PASS
- edge rate limiting: PASS (`429` observed)
- credential/log scan: PASS
- runtime policy evidence persistence: PASS

The model path remained restricted to the tunnel boundary; no paid external API fallback was used.

### Lifecycle gates

The verifier exercised the service lifecycle rather than only checking the first startup.

- normal close with synthetic workspace purge: PASS
- ephemeral tunnel-secret material absent at strict CLOSED verification: PASS
- retained PostgreSQL volume after close: PASS
- same immutable image-digest reopen: PASS
- emergency close rehearsal: PASS
- final normal close: PASS
- final policy flags: `YES_YES`
- final state: `CLOSED`
- full internal verifier: PASS

The shared native model daemon is outside the OpsMate lifecycle and was not stopped; OpsMate controls access to it through the restricted SSH tunnel lifecycle.

## What this evidence supports

The following boundary is now verified for the reviewed release:

```text
Synology container runtime
  -> loopback-only Nginx edge
  -> Spring application
       -> private PostgreSQL
       -> internal model_link
            -> restricted SSH tunnel
                 -> approved native Ollama model
```

It also verifies normal close, emergency close, same-digest reopen, synthetic data purge, and final CLOSED state on the internal deployment path.

## Remaining external gate

The following are **not** established by this internal run and remain separate public-deployment work:

- DSM Reverse Proxy/TLS public source configuration
- router/public ingress configuration
- Internet/LTE/5G HTTPS smoke
- external confirmation that PostgreSQL and the model endpoint are not reachable
- public-origin two-session isolation check
- public-origin rate-limit/security-header verification
- public-origin close/reopen smoke

Until those external checks succeed, describe OpsMate as having a verified internal deployment/lifecycle boundary, not as a fully verified public Internet service.
