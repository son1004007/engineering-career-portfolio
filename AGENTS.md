# AGENTS.md

## Repository Purpose
This repository is a public engineering portfolio.
Its purpose is to show:
1. who Son Gisuk is as an engineer
2. what kinds of systems he designs and builds
3. how he documents, implements, and validates work

When an AI receives only this repository URL, it must read `AI_CONTEXT.md`, `WORKS.md`, `03_portfolio/portfolio-strategy.md`, `03_portfolio/case-study-index.md`, `evidence/company-github/README.md`, and `03_portfolio/evidence-index.md` first. This repository is a public evidence source, not the authority for private job-choice constraints or offer decisions.

## Public Repository Rule
Do not add private job-search notes, company comparisons, support decisions, or internal-only memos.
Only add recruiter-facing portfolio content.

Do not copy salary, family, health, current-employer problems, private activity-log details, credentials, customer data, or internal URLs from linked private repositories.

For company GitHub evidence, publish only sanitized metadata and reviewed career claims. Do not publish repository names, customer identifiers, commit messages, file paths, diffs, internal emails, or source code. A Git commit identifies author/committer metadata; it does not by itself prove who performed the push event.

## Portfolio Composition

Keep the two portfolio tracks separate and complementary:

1. `OpsMate Local` is the implemented flagship candidate that demonstrates safe AI Agent integration with enterprise transactions. Keep its `tested-component` boundary explicit until a real local-model E2E run succeeds.
2. Case-study posts demonstrate existing Java/Spring, SQL, authentication, deployment, and operations depth.

Do not position the owner as an ML model researcher. The target identity is a Java/Spring enterprise backend and platform engineer who can safely integrate open-weight LLM and Agent capabilities.

Company work may be inspected only as evidence. Never copy company code into this repository. Public code must be independently reconstructed with synthetic data and generalized domains.

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

## Work Sequence
Follow this order for each project:
1. write design document
2. write project README
3. implement code
4. add tests
5. run tests and confirm success
6. update docs if needed
7. commit changes
8. push changes

A task is not complete if tests do not exist or if tests fail.

## Required Case Study Deliverables

Each published case study should include:

- anonymized problem and constraints
- the owner's verified responsibility, separated from team outcomes
- alternatives and the engineering decision
- independently reconstructed sample code when code is needed
- synthetic or public data
- tests for normal, failure, and boundary scenarios
- a recent verification result
- disclosure review and explicit limitations

`source-reviewed` means private source and attribution were checked. It does not mean a public sample is implemented or verified.

## Commit Message Rules
- `docs:` documentation changes
- `feat:` new functionality
- `fix:` bug fixes
- `test:` test changes
- `refactor:` structural improvement
- `chore:` maintenance work

## Current Project Priority
1. keep the GitHub Pages site and published evidence links healthy
2. connect `02_projects/opsmate-local` to an authorized open-weight model server and record an E2E result without adding a paid API fallback
3. independently reconstruct and verify the next Java/Spring case from `03_portfolio/case-study-index.md`
4. repair or archive the older partial samples only when they strengthen the two primary tracks
