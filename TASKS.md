# TASKS.md

This file defines the current work queue for the portfolio.

The active execution ledger, dependencies and verification evidence are maintained in [`WORKS.md`](WORKS.md). This file keeps the long-term backlog.

## Rules
- Always work from top to bottom
- Complete each task fully before moving on
- Each task must include design, implementation, and test

---

## Current Tasks

완료된 공개 작업의 명령·결과는 [`WORKS.md`](WORKS.md)에 기록합니다. 현재 장기 작업 순서는 다음과 같습니다.

### 0. company GitHub career evidence maintenance
- at month end, collect authenticated author-attributed commit metadata
- review implementation, test, integration, and operation status separately
- update `evidence/company-github/career-claims.csv` and monthly snapshot
- run the publication scan and evidence tests before push
- before leaving the employer, create a final departure snapshot

### 1. OpsMate Local final verification and controlled deployment
- finish the latest `clean verify`, container/config checks and documentation consistency review
- use only an authorized private open-weight model endpoint; no paid API fallback
- verify real structured-output success, p95, timeout, malformed output and model-unavailable behavior
- verify the implemented single-flight, workspace/global quota, queue/follower and concurrency limits against the real model boundary
- apply and retain evidence for app-host egress allowlisting and public edge/WAF rate limiting
- verify the public URL, external smoke and DB/model non-exposure
- rehearse app/model normal close, environment-independent emergency close and same-image-digest reopen
- leave both hosts closed after validation and record only generalized results without access details

### 2. next Java/Spring case study publication
- select the next `source-reviewed` case from `03_portfolio/case-study-index.md`
- reconfirm personal attribution and disclosure boundary in the authorized source
- implement an independent sample with synthetic data
- add normal, failure and boundary tests and publish only after a recent successful run

### 3. portfolio publication maintenance
- check Pages deployment and public links after material changes
- keep status badges, test counts and limitations synchronized
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
