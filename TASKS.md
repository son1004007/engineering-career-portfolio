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

### 7. Python/FastAPI Data / AI Service Integration evidence - COMPLETE

The capability gap identified by the independent Codex/Gemini review is now covered by one independently runnable public sample rather than another Java/Spring case.

Published public project evidence:

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
- portfolio PR regression: all 8 jobs PASS
- portfolio main Pages: verify 8 jobs + build + deploy PASS (`2026-08-31`)
- status: `published`

Still not claimed:

- production authentication or external IdP integration
- external/real LLM E2E or statistically meaningful model-quality metrics
- arbitrary production database connectors
- production concurrency, load, SLA or large-user operation

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
- physical handset visual spot-check is optional maintenance
- refresh sanitized company evidence at the defined review interval

### 10. Older sample disposition

- keep `ai-rag-api`, `backend-platform-template`, and `security-audit-log` as supporting work only while they remain incomplete or weakly verified
- repair a sample only when it fills a capability evidence gap
- do not repair a sample just to add another technology keyword
- otherwise mark it partial or archive it without presenting it as a flagship

### 11. Planned future evidence: reliable backend performance and AI service verification — NOT STARTED

This is a future public engineering task, not a completed or currently verified implementation. The private career learning plan schedules the work **after the existing PMP goal**; do not consume the current job-application/PMP preparation window with a large new build. The canonical personal sequencing is maintained in the private `career-playbook/CURRENT_PLAN.md` and its 2026-10-21 learning roadmap; do not copy private career information here.

Select one independently reproducible **synthetic backend scenario**; prefer extending a fitting existing public project rather than creating multiple disconnected technology demos. Only promote artifacts when implementation and runtime evidence exist.

- [ ] **Redis**: cache-aside, TTL/invalidation, update consistency, cache-miss concurrency and Redis outage/fallback tests, baseline vs cache-enabled measurement.
- [ ] **Kafka**: event producer/consumer, retry/dead-letter handling, duplicate delivery/idempotency, consumer restart and broker outage/recovery tests; document ordering and guarantees accurately.
- [ ] **PostgreSQL performance**: deterministic synthetic dataset, EXPLAIN (ANALYZE, BUFFERS), index/query changes, transactions/locks, reproducible before/after p50/p95/throughput measurements at equal load.
- [ ] **AI-service evaluation**: output schema and permission guardrails, failure/timeouts/fallback, fixed evaluation set for a selected Text2SQL/RAG/Agent flow, answer correctness and retrieval metrics where applicable, observed latency/cost if measured.
- [ ] **Evidence gate**: design/ADR and tradeoffs, Docker-based reproducible runtime, unit/integration/fault tests, CI, benchmark scripts and measured results, independent source/privacy/license review, explicit limitations, public publication verification.
- [ ] Update `WORKS.md`, `03_portfolio/evidence-index.md`, case-study and Pages only **after** tests/runtime checks; don't claim production scale, AWS commercial experience, or test outcomes before verification.

The associated AWS SAA and ISMS-P/technical professional certification roadmap is **private study planning**, not a skill/evidence claim for this public repository.


### 12. Planned: Enterprise AI Backend Lab - RAG, Agent and evaluation portfolio (2026-11-16)

- Status: **PLANNED / NOT STARTED**. This is a future learning/portfolio task, not implemented/verified work.
- Timing: **after the existing PMP priority**; do not automatically start a new large project or interfere with existing study commitments. Reassess scope when the work is explicitly resumed.
- Goal: independently implement, compare and explain enterprise AI retrieval/agent backend patterns, emphasizing API/DB integration, security boundaries, measured quality and reproducible evidence, **not model training or a modeler career claim**.
- Reuse the verified `Text2SQL Workspace` as a reference SQL baseline if it fits, and prefer an extension or one coherent lab over four disconnected demos. Use only public/synthetic data; do not copy employer/customer source, schemas, records, logs or documents.

Learning and implementation backlog (sequence is suggested; each item remains unstarted):

- [ ] **Concept notes:** LLM + Retrieval-Augmented Generation (RAG); SQL-backed retrieval ("SQL RAG" here is a project shorthand, not a formal standardized product); embedding / Vector RAG; Knowledge Graph, Ontology and their difference; GraphRAG; Hybrid RAG; tradeoffs and decision criteria.
- [ ] **Common reproducible dataset and test cases:** choose one generic business/technology document domain, fixed source snapshots, expected answers and evidence; design about 30-50 labeled questions including direct lookup, semantic matching, multi-hop relations, contradictory/missing facts and authorization boundaries. Record dataset provenance and avoid fabricating expected outputs.
- [ ] **SQL retrieval baseline:** controlled text-to-SQL / structured query, AST validation, allow-listed read-only PostgreSQL access, row/time bounds, per-user permissions and result-based correctness; reuse the existing sample where supported.
- [ ] **Vector RAG:** chunking, embedding, pgvector, semantic retrieval, source citation and metadata/ACL filtering; compare keyword/BM25 search and re-ranking as separate improvements.
- [ ] **GraphRAG:** entity/relation extraction with verification, graph traversal and multi-hop queries, provenance back to source text; start with PostgreSQL relation tables and adopt Neo4j/Cypher only when the measured use case benefits. Explore RDF/OWL/SPARQL/ontology modeling as **optional advanced** work; GraphRAG does not require a formal ontology.
- [ ] **Hybrid retrieval:** combine SQL, keyword, vector and graph retrieval through explicit routing/fusion/re-ranking; compare identical questions and evaluation conditions against each individual baseline.
- [ ] **Agent and tool execution:** LLM API/structured outputs; function/tool calling, MCP server/client, LangGraph stateful orchestration; demonstrate bounded retries, timeouts, idempotency, tool authorization and human approval for sensitive operations. Study context/prompt management; multi-agent/A2A is optional, not an initial acceptance gate.
- [ ] **Evaluation:** separate retrieval Recall@K from answer correctness, citation-grounding, permission violations and refusal quality; measure actual observed p50/p95 latency and token/cost under comparable settings; retain unsuccessful examples and limitations. Inspect RAGAS / LLM-as-a-Judge as helpers, not substitutes for deterministic golden cases.
- [ ] **Observability, operations and security:** prompt injection and source poisoning tests, server-side allowlists/RBAC and secret isolation; structured logs, tracing and error handling (e.g. OpenTelemetry/Langfuse), bounded rate/cost budgets and model failure fallback. LiteLLM/gateway and dedicated model-serving/fine-tuning are optional later extensions.
- [ ] **Documentation and publication:** keep architecture/ADR, reproducible setup, tests, evaluation scripts, original measurements and honest limitations in the project; publish an editorial blog series in existing GitHub Pages style (problem -> implementation -> comparison -> validation) with meaningful figures instead of formulaic tables; link a case study from this portfolio only after verifiable CI/runtime and public-data review.

Acceptance: one reproducible demo, four retrieval strategies compared on the same labeled question set, at least one bounded tool/agent workflow, failed/unsafe cases tested, measured quality/cost/latency and source citations, explicit NOT RUN/unknowns, and verified public publication. Do not promote a planned experiment to production experience or claimed skill before the evidence gate.


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
