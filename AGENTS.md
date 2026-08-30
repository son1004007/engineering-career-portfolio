# AGENTS.md

## Global AI Control

This repository participates in the owner's shared GitHub AI control plane.

When GitHub access is available, before substantive work read `son1004007/ai-agent-workflow-playbook/CONTROL.md`, then return here and follow this repository's local rules and current-state documents. The global control defines cross-repository discovery, Official-Source-First behavior, shared verification rules, and remote-runtime resolution. **This repository remains the source of truth for portfolio code, implementation state, tests, public content and backlog.**

If the private global control is unavailable, continue from this file and repository evidence. Never weaken public-disclosure, security or verification rules because the global control cannot be read.

## Repository Purpose

This repository is a public engineering portfolio.

Its purpose is to show:

1. what kinds of backend problems Son Gisuk can turn into working systems
2. how he connects business rules, data, permissions, failures and operations
3. how he integrates AI capabilities without handing critical decisions to the model
4. how he verifies implementation with tests and runtime evidence
5. which languages, frameworks and tools he used to accomplish those goals

When an AI receives only this repository URL, it must read `README.md`, `HOW_I_ENGINEER.md`, `AI_CONTEXT.md`, `WORKS.md`, `TASKS.md`, `03_portfolio/portfolio-strategy.md`, `03_portfolio/case-study-index.md`, `evidence/company-github/README.md`, and `03_portfolio/evidence-index.md` first.

This repository is a public evidence source, not the authority for private job-choice constraints or offer decisions.

## Reader-First Writing Standard

The primary public audience includes HR, recruiters, hiring managers and engineers.

Write in two layers.

```text
Layer 1: plain-language problem, action and result that a non-engineer can understand
Layer 2: technical terms, implementation detail and evidence for engineering review
```

Do not require readers to understand framework jargon before they can understand the value of the work.

Preferred pattern:

```text
쉽게 설명:
사용자 역할에 따라 할 수 있는 일을 제한하고, 잘못된 요청은 중요한 처리로 이어지지 않게 했습니다.

기술적으로:
RBAC, server-side validation, fail-closed policy를 적용했습니다.
```

For major public documents:

- make the role classification obvious before listing adjacent capabilities
- explain the problem before naming the framework
- explain the result before listing implementation details
- translate specialist terms when they first appear
- keep technical vocabulary where it demonstrates depth
- put technology lists after role, capability and evidence sections
- avoid inflated marketing language and AI buzzword stacking
- preserve exact verification boundaries and limitations
- do not use a raw test count as the primary achievement when the failure condition or runtime boundary can be explained instead

## Public Positioning

Do not define the owner primarily by one programming language or framework, but do make the primary market role immediately clear.

The target public identity is:

> Backend Engineer - AI Integration & Reliable Systems

Korean public summary:

> 업무 요구사항을 데이터, 권한, 처리 상태와 실패 조건이 명확한 백엔드 시스템으로 구현하고, AI 기능을 서버 검증과 실제 실행 증거 안에서 안전하게 연결하는 엔지니어

Positioning hierarchy:

```text
Primary role:
Backend Engineer

Differentiators:
AI Integration
Reliable Systems

Supporting capabilities:
Data
Security
Operations
```

Java/Spring and Python/FastAPI are important implementation tools and verified experience, but they are not the top-level identity.

Do not use `Software / Backend / Platform Engineer` as the first-screen title. `Software Engineer` is too generic to improve classification, and current public evidence is not sufficient to make `Platform Engineer` the top-level role.

Do not position the owner as an ML model researcher. AI capability here means integrating, evaluating and operating LLM capabilities inside backend systems and using AI effectively in the engineering workflow.

## Platform Claim Gate

Do not use `Platform Engineer` as a headline or equivalent top-level role unless public evidence has materially expanded to include several of the following:

- repeatable IaC-managed infrastructure
- Kubernetes or comparable orchestration operations
- internal developer platform or shared deployment/development capabilities
- centralized observability and operations automation
- multi-environment operation, incident handling or capacity evidence

Using Linux, Docker, Nginx, Tomcat, CI/CD and deployment automation is valuable operations evidence, but by itself does not justify the broader platform title.

## Portfolio Composition

Keep the evidence complementary rather than language-centric.

1. `OpsMate Local` demonstrates controlled AI integration with business transactions, security boundaries and operation.
2. Text2SQL/NL2SQL evidence demonstrates Python/API/data/LLM evaluation capability.
3. Engineering case studies demonstrate reusable backend problem-solving depth across identity, data correctness, deployment/recovery and business-rule consistency.
4. `HOW_I_ENGINEER.md` demonstrates the engineering method: problem definition -> plan -> AI-assisted exploration/implementation -> test -> runtime verification -> evidence.

Case studies may use Java/Spring when it is the right reconstruction tool, but titles and summaries should lead with the engineering problem rather than the framework name.

Company work may be inspected only as evidence. Never copy company code into this repository. Public code must be independently reconstructed with synthetic data and generalized domains.

## AI Wording Standard

AI terms must be backed by concrete implementation or verification.

Prefer:

```text
AI가 구매 요청 초안을 만들고,
서버가 권한과 상태를 검증한 뒤,
사람이 승인한 요청만 발주되게 했습니다.
```

before:

```text
Agentic AI Runtime
context engineering
agent workflow
provenance
```

Terms such as `Agent`, `RAG`, `context engineering`, `Agentic AI Runtime` and `provenance` may appear only where the document also states what was implemented, what was verified and what remains unverified.

Do not present routine use of ChatGPT/Codex/Gemini for coding as a differentiating capability by itself. The differentiator must be an engineering outcome, control boundary, verification method or repeatable workflow.

## Evidence Presentation

Public evidence should lead with what was proven, not how many assertions exist.

Preferred order:

```text
runtime behavior or user flow
-> failure/security boundary
-> execution environment
-> supporting test count or metric when useful
```

Strong examples:

- unauthorized approval is blocked by the server
- two user sessions do not share workspaces
- the database and model are not directly exposed on the public path
- the service can be closed and reopened with the same verified release
- actual-model E2E completed 9/9 requests
- p95 met a predeclared project gate

Weak first-screen example:

- 24 + 12 + 10 + 11 tests

Test counts remain valid supporting evidence in detailed case-study pages.

## Public Repository Rule

Do not add private job-search notes, company comparisons, support decisions, or internal-only memos.
Only add recruiter-facing portfolio content.

Do not copy salary, family, health, current-employer problems, private activity-log details, credentials, customer data, or internal URLs from linked private repositories.

For company GitHub evidence, publish only sanitized metadata and reviewed career claims. Do not publish repository names, customer identifiers, commit messages, file paths, diffs, internal emails, or source code. A Git commit identifies author/committer metadata; it does not by itself prove who performed the push event.

## Evidence Labels

Keep profile claims and project status explicit:

- `verified`: a recent run or test success is recorded
- `implemented`: core code exists
- `tested-file-present`: tests exist but recent success is not recorded
- `tested-component`: test artifacts exist for the named component; this does not verify the complete system
- `partial`: required components are missing
- `planned`: documentation or task-list item only
- `self-described`: profile or career claim needing separate evidence
- `private-work-code-verified`: company-owned private code and user attribution were checked in an authorized environment; only a sanitized claim is public

Never present a planned or partial item as completed. Update `03_portfolio/evidence-index.md` whenever implementation or verification status changes.

## Company Evidence Maintenance

- At month end, refresh `evidence/company-github/monthly/` from authenticated sources.
- Update `career-claims.csv` only after human review of attribution, implementation status, and disclosure limits.
- Keep authored, merged, tested, integrated, operated, and planned states separate.
- Do not use commit count as a productivity or impact score.
- Keep optional verification digests and source fingerprints private. Never commit a generated public snapshot with `verification_digests_included: true`.
- If private access expires, preserve the last verification date and mark access as expired. Do not silently invent or delete the prior verified state.
- Before leaving the employer, create a final `departure-snapshot-YYYY-MM-DD.md` while authorized access still exists.

## Folder Roles

- `01_profile/`: profile, strengths, direction
- `02_projects/`: project artifacts
- `03_portfolio/`: positioning and portfolio summaries
- `03_portfolio/case-studies/`: recruiter-facing, sanitized engineering case studies
- `evidence/company-github/`: sanitized company contribution evidence and monthly snapshots
- `tools/`: local evidence collection utilities; local configuration is never committed
- `HOW_I_ENGINEER.md`: public explanation of backend problem solving, AI integration, verification and operation method

## Required Project Deliverables

Each project should include:

- `README.md`
- `ARCHITECTURE.md`
- `SETUP.md`
- source code directory such as `app/` or `src/`
- `tests/`

## Code Explanation Standard

For Java/Spring code, follow [`03_portfolio/code-explanation-standard.md`](03_portfolio/code-explanation-standard.md).

- Explain business rules, trust boundaries, failure behavior, side effects, concurrency assumptions, and change impact in Korean Javadoc or comments.
- Prefer type-level and method-level Javadoc for durable contracts. Use inline comments only when the reason or constraint is not evident from the code.
- Do not narrate obvious statements, comment every line, or add Javadoc to simple getters and generated accessors.
- Update or remove an explanation whenever the related behavior changes, and keep it consistent with tests and public documentation.
- Never include customer identifiers, private repository paths, internal URLs, credentials, or copied company code in explanations or examples.

The same principle applies to Python and other languages: explain durable engineering decisions, not obvious syntax.

## Work Sequence

Follow this order for each project:

1. define the problem, constraints and completion criteria
2. write or update design documents
3. implement code
4. add tests
5. run tests and confirm success
6. verify relevant runtime boundaries when practical
7. update evidence and public docs
8. commit and push changes

A task is not complete if required tests fail or if the public claim exceeds the verified boundary.

## Required Case Study Deliverables

Each published case study should include:

- plain-language title and one-paragraph summary for non-engineers
- anonymized problem and constraints
- the owner's verified responsibility, separated from team outcomes
- alternatives and the engineering decision
- independently reconstructed sample code when code is needed
- synthetic or public data
- tests for normal, failure, and boundary scenarios
- a recent verification result
- technical detail section for engineering readers
- disclosure review and explicit limitations

Preferred case-study order:

```text
Problem
-> Why it mattered
-> Constraints
-> Decision
-> Implementation
-> Verification
-> Result
-> Limits
-> Technologies used
```

`source-reviewed` means private source and attribution were checked. It does not mean a public sample is implemented or verified.

## Commit Message Rules

- `docs:` documentation changes
- `feat:` new functionality
- `fix:` bug fixes
- `test:` test changes
- `refactor:` structural improvement
- `chore:` maintenance work

## Current Project Priority

1. keep GitHub profile, README, Pages, profile and strategy documents synchronized with `Backend Engineer - AI Integration & Reliable Systems`
2. make the first-screen role understandable to HR before technical details
3. emphasize verified failure boundaries and runtime behavior over raw test counts
4. strengthen reproducible Python/FastAPI and AI/data backend evidence without inventing experience
5. keep the verified `2026-08-29` OpsMate public Internet E2E boundary accurate
6. keep the GitHub Pages site, evidence links and mobile rendering healthy
7. repair or archive older partial samples when they weaken public engineering credibility
