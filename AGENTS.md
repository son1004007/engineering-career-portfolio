# AGENTS.md

## Repository Purpose
This repository is a public engineering portfolio.
Its purpose is to show:
1. who Son Gisuk is as an engineer
2. what kinds of systems he designs and builds
3. how he documents, implements, and validates work

When an AI receives only this repository URL, it must read `AI_CONTEXT.md` and `03_portfolio/evidence-index.md` first. This repository is a public evidence source, not the authority for private job-choice constraints or offer decisions.

## Public Repository Rule
Do not add private job-search notes, company comparisons, support decisions, or internal-only memos.
Only add recruiter-facing portfolio content.

Do not copy salary, family, health, current-employer problems, private work-log details, credentials, customer data, or internal URLs from linked private repositories.

## Evidence Labels
Keep profile claims and project status explicit:
- `verified`: a recent run or test success is recorded
- `implemented`: core code exists
- `tested-file-present`: tests exist but recent success is not recorded
- `partial`: required components are missing
- `planned`: documentation or task-list item only
- `self-described`: profile or career claim needing separate evidence

Never present a planned or partial item as completed. Update `03_portfolio/evidence-index.md` whenever implementation or verification status changes.

## Folder Roles
- `01_profile/`: profile, strengths, direction
- `02_projects/`: project artifacts
- `03_portfolio/`: positioning and portfolio summaries

## Required Project Deliverables
Each project should include:
- `README.md`
- `ARCHITECTURE.md`
- `SETUP.md`
- source code directory such as `app/` or `src/`
- `tests/`

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

## Commit Message Rules
- `docs:` documentation changes
- `feat:` new functionality
- `fix:` bug fixes
- `test:` test changes
- `refactor:` structural improvement
- `chore:` maintenance work

## Current Project Priority
1. `02_projects/ai-rag-api`
2. `02_projects/backend-platform-template`
3. `02_projects/security-audit-log`
