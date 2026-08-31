# TASKS.md

This file defines the current work queue for the portfolio.

The active execution ledger and verification evidence are maintained in [`WORKS.md`](WORKS.md). The ordered completion sequence is maintained in [`PORTFOLIO_COMPLETION_PLAN.md`](PORTFOLIO_COMPLETION_PLAN.md). This file keeps the long-term backlog.

## Rules

- Work from verified evidence, not from marketing claims.
- Complete each task fully before presenting it as finished.
- Each engineering sample must include design, implementation and tests.
- Do not repeat a verification gate already supported by current evidence.
- Choose new public work by capability gap, not by programming language count.
- Public summaries must be understandable to HR first and technically inspectable by engineers second.

---

## Current Tasks

완료된 공개 작업의 명령·결과는 [`WORKS.md`](WORKS.md)에 기록합니다. 현재 장기 작업 순서는 다음과 같습니다.

### 0. Company evidence maintenance

- keep sanitized company evidence synchronized at the defined review interval
- separate implementation, test, integration and operation claims
- keep private repository names, customer identifiers, source code and internal paths out of the public portfolio
- update public claims only after attribution and disclosure review
- before leaving the employer, preserve a final authorized evidence snapshot if still allowed by policy

### 1. OpsMate Local bounded public deployment verification - COMPLETE

- real-model adapter E2E: verified (`2026-08-23`)
- Synology internal deployment/network/security/lifecycle E2E: verified (`2026-08-25`)
- DSM TLS/public Internet application/network/security/lifecycle E2E: verified (`2026-08-29`)
- public two-session workspace isolation, rate limiting, API/actuator boundary, DB/model/loopback non-exposure and credential-log scan: verified
- normal close, same-digest reopen, emergency close and recovery normal close: verified
- final runtime state: `CLOSED`, running workload container `0`, PostgreSQL persistent volume preserved
- evidence: [`02_projects/opsmate-local/docs/PUBLIC_DEPLOYMENT_E2E_EVIDENCE.md`](02_projects/opsmate-local/docs/PUBLIC_DEPLOYMENT_E2E_EVIDENCE.md)
- bounded E2E only; do not claim 24x7 SLA, long-duration load or production traffic scale

### 2. User identity and access integration case - COMPLETE

- traceability ID: `CS-JAVA-01`
- authorized source review and personal contribution boundary: confirmed
- independent Java 21 / Spring Boot 3.5.16 sample: 24 tests PASS
- verified boundaries: DB/SSO authentication convergence, local RBAC, session rotation, CSRF lifecycle, assertion validation, nonce replay and fail-closed configuration
- status: `published`
- actual external IdP, production DB, distributed session, large-scale load and SLA remain unverified

### 3. Complex date-range data correctness case - COMPLETE

- traceability ID: `CS-JAVA-02`
- independent Spring Boot/MyBatis/H2 sample: 12 tests PASS
- same/cross-year correctness, tenant isolation, count/page consistency and deterministic pagination verified
- status: `published`
- actual Oracle execution plan and production performance numbers remain unverified

### 4. Deployment and recovery portability case - COMPLETE

- traceability ID: `CS-JAVA-03`
- independent Java 21 / Spring Boot WAR sample: 10 tests PASS
- external-container bootstrap, non-root context path, deploy-profile fail-closed, backup/replace/health/rollback verified in synthetic environment
- status: `published`
- real external Tomcat rolling deployment, session drain, zero-downtime and production SLA remain unverified

### 5. Business-rule consistency case - COMPLETE

- traceability ID: `CS-JAVA-06`
- independent Java 21 / Spring Boot synthetic sample: 11 MockMvc tests PASS
- verified boundaries: canonical session identity, limited legacy fallback, latest-only policy, explicit-or-latest policy, data-access input ownership, 400/401/404 fail-closed behavior and request identity override prevention
- status: `published`
- actual company SSO/session E2E, production DB results, organization-wide rule ownership and SLA remain unverified

### 6. Capability-first public positioning - COMPLETE

The repository first screen and public site must lead with capability rather than Java/Spring branding.

Completed:

- `README.md` capability-first structure
- `index.md` HR-readable landing page
- `HOW_I_ENGINEER.md` engineering-method explanation
- profile documents rewritten around problem solving, AI use, verification, security and operations
- `AGENTS.md` reader-first writing rule
- `AI_CONTEXT.md` language-independent interpretation rule
- `portfolio-strategy.md` capability map and evidence tracks
- `_config.yml` and `llms.txt` title/context update

Required interpretation order:

```text
problem and result
-> engineering capability
-> evidence
-> technical detail
-> technology
```

### 7. Python/FastAPI Data / AI Service Integration evidence - SAMPLE VERIFIED, PUBLICATION PENDING

The capability gap identified by the independent Codex/Gemini review is now covered by one independently runnable public sample rather than another Java/Spring case.

Implemented public project:

> `Text2SQL Workspace` — Python/FastAPI multi-user Text2SQL backend with deterministic evaluation and PostgreSQL read-only Docker runtime verification

Verified scope:

- synthetic/public data only
- FastAPI authentication/workspace/query API boundary
- natural-language question -> SQL candidate -> SQLGlot validation -> read-only execution -> result flow
- generation / validation / execution / answer-correctness outcomes separated
- single-statement, SELECT-only and table-allowlist application policy
- unsafe/write SQL rejected before executor invocation
- deterministic fixture model so core verification does not require an external paid model
- result-based evaluation instead of SQL-string equality
- PostgreSQL 17 Docker runtime with dedicated analytics reader
- database reader can `SELECT` but cannot `INSERT`
- bounded rows, statement timeout and explicit read-only transaction
- application metadata authority separated from analytics query authority
- FastAPI host exposure bounded to loopback; PostgreSQL has no host-published port
- public disclosure review completed
- public project main CI PASS for both Python tests and Docker/PostgreSQL E2E (`2026-08-31`)

Still not claimed:

- production authentication or external IdP integration
- external/real LLM E2E or statistically meaningful model-quality metrics
- arbitrary production database connectors
- production concurrency, load, SLA or large-user operation

Remaining publication gate:

```text
portfolio evidence/status synchronization
-> portfolio regression PASS
-> merge
-> main Pages build/deploy PASS
-> status promoted from sample-verified to published
-> final status-sync Pages build/deploy PASS
```

Until the Pages gate completes, `CS-AI-01` remains `sample-verified`, not `published`.

`CS-JAVA-11` statistical analysis UI remains an optional source-reviewed candidate only if a future target role specifically needs stronger analysis-UI evidence. Its legacy ID does not make Java the selection criterion.

### 8. HR-readable case-study refinement

For each published case, keep technical depth but review the first screen in this order:

```text
Problem
Why it mattered
What changed
Verified result
Technical implementation
Limitations
```

Specialist terminology should remain available, but the reader should understand the problem without knowing the terminology first.

### 9. Portfolio publication maintenance

- keep Pages deployment and public links healthy after material changes
- keep status badges, test counts and limitations synchronized
- maintain viewport, responsive breakpoint and horizontal-overflow regression tests
- check the capability-first landing page after the current Pages build/deploy completes
- physical handset visual spot-check is optional maintenance
- refresh sanitized company evidence at the defined review interval

### 10. Older sample disposition

- keep `ai-rag-api`, `backend-platform-template`, and `security-audit-log` as supporting work only while they remain incomplete or weakly verified
- repair a sample only when it fills a capability evidence gap
- do not repair a sample just to add another technology keyword
- otherwise mark it partial or archive it without presenting it as a flagship

---

## Definition of Done

A task is complete only when:

- the problem and constraints are documented
- design or engineering decision is documented
- code exists when implementation is part of the claim
- tests exist and pass
- documentation matches implementation
- any runtime or external boundary claimed as complete has bounded execution evidence
- any publication claim has current Pages build/deploy evidence
- a non-engineer can understand the problem and result before the specialist detail
- the public claim does not exceed the verified boundary
