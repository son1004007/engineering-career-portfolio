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
