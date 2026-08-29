# Verification

- 최근 검증일: `2026-08-29`
- GitHub Actions run: `33252018737`
- job: `WAR deployment portability`
- command: `./mvnw -q clean verify`
- result: `PASS`
- automated tests: `10`

## Gate V1 — WAR/container structure — PASS

- Maven packaging is `war`.
- Tomcat dependency is `provided`.
- `ServletInitializer` extends `SpringBootServletInitializer`.
- Maven `verify` reached the package lifecycle successfully.

## Gate V2 — context-path portability — PASS

With `server.servlet.context-path=/demo`:

- `/demo/entry` returns 200.
- the generated health link is `/demo/healthz`.
- `/demo/healthz` returns `UP`.
- root `/healthz` is not accidentally exposed.

Observed CI runtime for this embedded-container gate:

- Java `21.0.12`
- Spring Boot `3.5.16`
- embedded Tomcat `10.1.55`

## Gate V3 — deploy configuration boundary — PASS

- `deploy` profile without an external runtime token fails application-context creation.
- the same profile starts when a synthetic external value is supplied.
- runtime-token contents are not returned by application endpoints.

The intentional missing-value test emits a Spring startup failure in the test log; the test itself passes only when that fail-closed behavior occurs.

## Gate V4 — release/rollback rehearsal — PASS

Using temporary synthetic files and a fake health client:

- successful health keeps the candidate WAR and retains the previous WAR as rollback material.
- failed health restores the previous WAR.
- unsafe application names are rejected before artifact replacement.

## Evidence boundary

This is sufficient for `sample-verified`: the independent public sample executed its defined gates in CI. `published` still requires the merged main commit to complete GitHub Pages build/deploy.

This evidence does **not** prove a real external Tomcat rolling deployment, production SLA, session drain, cluster failover, or zero-downtime behavior.
