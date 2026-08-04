# Project Roadmap

## Stage 1. Evidence and case-study selection
- status: `verified` (`2026-08-03`)
- verify the top Java/Spring cases in authorized source repositories
- define redaction and independent reconstruction boundaries
- select one first article and sample

## Stage 2. First Java/Spring case study
- status: `verified` — Spring Security auth bridge, 24 tests
- implement a synthetic, independent reproduction
- add tests and verification evidence
- publish the recruiter-facing article

## Stage 3. OpsMate Local design
- status: `verified`
- design the purchase approval and ordering vertical slice
- define a server-controlled policy query port, pass only retrieved evidence to the model, and keep RBAC, audit and fail-closed behavior outside the model

## Stage 4. OpsMate Local implementation
- status: `tested-component` — historical 2026-08-03 baseline 19 tests; 2026-08-04 latest clean verify 54/54 passed
- implement and test the smallest end-to-end workflow
- verify local-model failure, authorization, idempotency and rollback

## Stage 5. Portfolio expansion
- status: `in-progress`
- publish additional verified Java/Spring and AI application cases
- repair or archive older partial samples based on hiring value

## Stage 6. OpsMate Local model integration
- status: `in-progress`
- connect an authorized open-weight model endpoint without a paid API fallback
- implemented the bounded Ollama gateway, single-flight, workspace/global quota, queue/follower and concurrency controls
- verify real structured output, p95, failure behavior and GPU load on the approved private model host

## Stage 7. OpsMate Local public demo operations
- status: `implemented`, external rehearsal `pending`
- implemented Thymeleaf session UI, workspace isolation/TTL, PostgreSQL one-shot migration and runtime-role separation
- implemented digest-pinned Docker/Caddy deployment plus app/model normal close, emergency close and reopen assets
- verify public URL, host egress allowlist, edge/WAF rate limit, external smoke and both-host close/reopen rehearsal
