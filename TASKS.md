# TASKS.md

This file defines the current work queue for the portfolio.

The active execution ledger, dependencies and verification evidence are maintained in [`WORKS.md`](WORKS.md). The ordered completion sequence is maintained in [`PORTFOLIO_COMPLETION_PLAN.md`](PORTFOLIO_COMPLETION_PLAN.md). This file keeps the long-term backlog.

## Rules
- Always work from top to bottom
- Complete each task fully before moving on
- Each task must include design, implementation, and test
- Do not repeat a verification gate already supported by current evidence; preserve the evidence and continue from the next unverified boundary

---

## Current Tasks

완료된 공개 작업의 명령·결과는 [`WORKS.md`](WORKS.md)에 기록합니다. 현재 장기 작업 순서는 다음과 같습니다.

### 0. company GitHub career evidence maintenance
- at month end, collect authenticated author-attributed commit metadata
- review implementation, test, integration, and operation status separately
- update `evidence/company-github/career-claims.csv` and monthly snapshot
- run the publication scan and evidence tests before push
- before leaving the employer, create a final departure snapshot

### 1. OpsMate Local bounded public deployment verification — COMPLETE
- real-model adapter E2E: verified (`2026-08-23`)
- Synology internal deployment/network/security/lifecycle E2E: verified (`2026-08-25`)
- DSM TLS/public Internet application/network/security/lifecycle E2E: verified (`2026-08-29`)
- public two-session workspace isolation, rate limiting, API/actuator boundary, DB/model/loopback non-exposure and credential-log scan: verified
- normal close, same-digest reopen, emergency close and recovery normal close: verified
- final runtime state: `CLOSED`, running workload container `0`, PostgreSQL persistent volume preserved
- evidence: [`02_projects/opsmate-local/docs/PUBLIC_DEPLOYMENT_E2E_EVIDENCE.md`](02_projects/opsmate-local/docs/PUBLIC_DEPLOYMENT_E2E_EVIDENCE.md)
- bounded E2E only; do not claim 24x7 SLA, long-duration load or production traffic scale

### 2. next Java/Spring case study publication — ACTIVE
- selected candidate: `CS-JAVA-02` — MyBatis/Oracle query correctness and performance-oriented query design
- preserve existing `source-reviewed` evidence; reconfirm attribution/disclosure boundary before making new company-work claims
- implement an independent sample with synthetic schema/data; do not copy company code, SQL, identifiers or data
- focus the public sample on filter/count/page consistency, deterministic ordering, index-friendly range predicates and regression tests
- if Oracle-specific execution-plan evidence cannot be reproduced publicly, label it as an unverified production-specific boundary rather than inventing performance numbers
- add normal, failure and boundary tests and publish only after a recent successful run

### 3. portfolio publication maintenance
- check Pages deployment and public links after material changes
- keep status badges, test counts and limitations synchronized
- perform physical mobile UX verification when practical
- refresh sanitized company GitHub evidence at the defined review interval

### 4. older sample disposition
- keep `ai-rag-api`, `backend-platform-template`, and `security-audit-log` as supporting work only
- repair a sample only when it strengthens the approved two-track strategy
- otherwise mark it partial or archive it without presenting it as a flagship

---

## Definition of Done
A task is complete only when:
- design document exists
- code exists
- tests exist
- tests pass
- documentation matches implementation
- any external/runtime gate claimed as complete has bounded execution evidence
