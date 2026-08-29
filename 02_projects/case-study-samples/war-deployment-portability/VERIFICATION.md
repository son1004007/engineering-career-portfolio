# Verification

## Gate V1 — WAR/container structure

- Maven packaging is `war`.
- Tomcat dependency is `provided`.
- `ServletInitializer` extends `SpringBootServletInitializer`.
- `mvn verify` reaches the package lifecycle and produces the WAR artifact.

## Gate V2 — context-path portability

With `server.servlet.context-path=/demo`:

- `/demo/entry` returns 200.
- the generated health link is `/demo/healthz`.
- `/demo/healthz` returns `UP`.
- root `/healthz` is not accidentally exposed.

## Gate V3 — deploy configuration boundary

- `deploy` profile without an external runtime token fails application-context creation.
- the same profile starts when a synthetic external value is supplied.
- runtime-token contents are not returned by application endpoints.

## Gate V4 — release/rollback rehearsal

Using temporary synthetic files and a fake health client:

- successful health keeps the candidate WAR and retains the previous WAR as rollback material.
- failed health restores the previous WAR.
- unsafe application names are rejected before artifact replacement.

## Command

```bash
chmod +x mvnw
./mvnw clean verify
```

## Evidence rule

`sample-verified` is assigned only after the GitHub Actions Java matrix executes this command successfully. `published` is assigned only after the merged main commit completes GitHub Pages build/deploy. Neither state proves a real external Tomcat rolling deployment, production SLA, or zero-downtime behavior.
