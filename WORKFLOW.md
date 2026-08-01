# DEVELOPMENT WORKFLOW

## Purpose
Define a consistent workflow from design to push.

## Standard Flow
1. Design
   - write `ARCHITECTURE.md`

2. Documentation
   - write `README.md`
   - define API, usage, and execution method

3. Implementation
   - implement minimal working code
   - keep structure layered

4. Testing
   - write tests under `tests/`
   - validate the main logic

5. Verification
   - run tests
   - confirm main scenarios work as expected

6. Documentation Sync
   - update README and ARCHITECTURE to match the code

7. Commit
   - use proper commit type such as `feat`, `docs`, `test`, `fix`, `refactor`, `chore`

8. Push
   - push changes after test confirmation

## Key Principle
- Code without tests is incomplete.
- Documentation without implementation is unreliable.
- Implementation without design is hard to maintain.

All three must exist:
- design
- implementation
- validation

## Company GitHub Evidence Flow

1. Capture
   - use authenticated company GitHub or a current local clone
   - load repository paths and author identities from the ignored local config

2. Attribution
   - match Git author identity; review committer metadata separately when it matters
   - do not call commit metadata a push audit event

3. Classification
   - group commits by sanitized project ID
   - separate designed, implemented, tested, integrated, operated, and planned

4. Verification
   - check default-branch reachability, related PR, tests, execution result and work evidence when available
   - use commit count only as an inventory value

5. Redaction
   - remove repository names, URLs, customers, commit messages, file paths, emails, source code and internal infrastructure data

6. Sync
   - update `career-claims.csv`, `projects.md`, the monthly snapshot, `AI_CONTEXT.md`, and `evidence-index.md` together

7. Publish
   - run `python -B -m unittest discover -s tests -p "test_*.py" -v`
   - scan the complete staged diff before commit and push
