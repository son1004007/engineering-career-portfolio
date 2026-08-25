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

### 1. OpsMate Local public ingress and external verification
- preserve the verified `2026-08-23` real-model evidence and `2026-08-25` NAS internal deployment/lifecycle E2E evidence
- use only the reviewed source and immutable app/tunnel image digests recorded in [`02_projects/opsmate-local/docs/NAS_INTERNAL_E2E_EVIDENCE.md`](02_projects/opsmate-local/docs/NAS_INTERNAL_E2E_EVIDENCE.md)
- do not repeat internal stack/network/session/model/lifecycle gates unless the release changes
- configure DSM Reverse Proxy/TLS and router public ingress
- open the exact reviewed release only after public ingress is ready
- verify Internet/LTE public application smoke, two-session workspace isolation, public rate limiting and security boundaries
- verify PostgreSQL and model endpoint remain externally unreachable
- perform public-origin normal close, same-digest reopen, emergency close, final normal close and leave runtime `CLOSED`
- no paid API fallback

### 2. next Java/Spring case study publication
- select the next `source-reviewed` case from `03_portfolio/case-study-index.md`
- reconfirm personal attribution and disclosure boundary in the authorized source
- implement an independent sample with synthetic data
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
