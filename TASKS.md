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
- status/evidence Pages publication run `33250726427`: success
- bounded E2E only; do not claim 24x7 SLA, long-duration load or production traffic scale

### 2. CS-JAVA-01 Spring Security auth bridge publication — COMPLETE
- authorized source review and personal contribution boundary: confirmed
- independent Java 21 / Spring Boot 3.5.16 sample: 24 tests PASS
- verified boundaries: DB/SSO authentication convergence, local RBAC, session rotation, CSRF lifecycle, issuer/audience/keyId/signature, nonce replay and fail-closed configuration
- publication evidence main commit `c74655a2e7aacfa0d05f41bc594598a0c0f73296`
- main Pages run `33276912458`: Spring Security regression + public/Jekyll + build + deploy PASS
- status: `published`
- actual external IdP, production DB, distributed session, large-scale load and SLA remain unverified and must not be claimed

### 3. CS-JAVA-02 MyBatis query correctness publication — COMPLETE
- authorized source re-review: complete
- independent Spring Boot/MyBatis/H2 sample: 12 tests PASS
- final PR regression run `33251272174`: PASS
- main Pages run `33251362190`: verify/build/deploy PASS
- status: `published`
- actual Oracle execution plan and production performance numbers remain unverified and must not be claimed

### 4. CS-JAVA-03 WAR deployment portability publication — COMPLETE
- authorized source re-review: WAR deploy workflow/runbook, context-path, profile/config contribution boundary reconfirmed
- independent Java 21 / Spring Boot 3.5.16 WAR sample: 10 tests PASS
- sample boundaries: external-container initializer, non-root context path, deploy-profile fail-closed, backup/replace/health/rollback
- final PR regression run `33252086213`: 7 jobs PASS
- main Pages run `33252148733`: 7 verify jobs + build + deploy PASS
- status: `published`
- real external Tomcat rolling deployment, session drain, zero-downtime and production SLA remain unverified and must not be claimed

### 5. CS-JAVA-06 business-rule consistency publication — COMPLETE
- authorized private source attribution and exact defect/fix boundary: reconfirmed
- public disclosure boundary: company class names, endpoints, fields, SQL, schema, test accounts, real data and internal identifiers excluded
- independent Java 21 / Spring Boot 3.5.16 synthetic `member snapshot` sample: implemented
- 11 MockMvc regression tests: PASS
- verified boundaries: canonical session identity, limited legacy fallback, latest-only policy, explicit-or-latest policy, Mapper input ownership, 400/401/404 fail-closed behavior, request identity override prevention
- initial PR run `33275860098`: `Business-rule consistency` job and all 8 portfolio jobs PASS
- final PR run `33276143715`: all 8 portfolio jobs PASS after evidence/state synchronization
- main merge commit: `733db7c614af5613216773b3b1fc6b3567e0b84c`
- main Pages run `33276278894`: 8 verify jobs + build + deploy PASS
- status: `published`
- actual company SSO/session E2E, Mapper SQL/production DB results, organization-wide rule ownership and production SLA remain unverified and must not be claimed

### 6. optional next Java/Spring case selection
- no additional Java/Spring case is required for the current core portfolio completion gate
- select another case only when a target role reveals a clear evidence gap
- prefer a backend dimension distinct from authentication, SQL correctness, WAR deployment and business-rule consistency
- `CS-JAVA-11` statistical analysis UI remains a source-reviewed option if data-processing/visualization depth becomes useful
- do not promote a candidate until independent code, normal/failure/boundary tests and current CI evidence exist

### 7. portfolio publication maintenance
- keep Pages deployment and public links healthy after material changes
- mobile baseline is guarded by viewport + responsive breakpoint + horizontal overflow regression tests
- keep status badges, test counts and limitations synchronized
- physical handset visual spot-check is optional maintenance, not a release blocker
- refresh sanitized company GitHub evidence at the defined review interval

### 8. older sample disposition
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
- any publication claim has current Pages build/deploy evidence
