# Portfolio Work Ledger

- 기준일: `2026-08-30`
- 목적: capability-first 포트폴리오, GitHub Pages 공개, OpsMate bounded public deployment, 실무 문제 독립 재현 사례와 evidence를 추적
- 원칙: 문서·코드·테스트·공개 안전성 검토가 함께 끝나기 전에는 완료로 표시하지 않음
- 공통 검수표: [`03_portfolio/review-checklist.md`](03_portfolio/review-checklist.md)
- 현재 완료 계획: [`PORTFOLIO_COMPLETION_PLAN.md`](PORTFOLIO_COMPLETION_PLAN.md)

## 상태

| 상태 | 의미 |
|---|---|
| `pending` | 선행 작업 전 또는 착수 전 |
| `in-progress` | 현재 작성·구현·검수 중 |
| `blocked` | 외부 입력이나 실행 환경이 없어 다음 단계로 갈 수 없음 |
| `verified` | 산출물과 최근 검증 결과가 존재 |
| `published` | 검증된 산출물이 공개 GitHub Pages 배포 gate를 통과 |

## Work 현황

| Work | 목표 | 핵심 산출물 | 완료 조건 | 현재 상태 |
|---|---|---|---|---|
| `W00` | GitHub 게시 환경 준비 | 인증된 GitHub 작업 경로 | remote·branch·공개 경계 확인 | `verified` |
| `W01` | 회사·개인 근거 정합화 | 비식별 claim과 사례 인덱스 | 근거·귀속·공개 범위 일치 | `verified` |
| `W02` | 검수 체계 고정 | ledger와 공통 체크리스트 | 모든 Work의 검증·공개 gate 정의 | `verified` |
| `W03` | 사용자 로그인·권한 통합 사례 공개 | 인증 통합 독립 샘플 | 정상·실패·경계 테스트와 Pages deploy | `published` |
| `W04` | 추가 실무 문제 사례 정리 | backend·data·AI·operations 사례 | 각 글의 근거·한계·재현 계획 명시 | `verified` |
| `W05` | Controlled AI Integration 구현 | OpsMate 설계·구현·테스트 | 서버 통제·실패 차단 테스트 성공 | `verified` |
| `W06` | GitHub Pages 포트폴리오 구현 | 홈·레이아웃·배포 workflow | 반응형·링크·접근성·빌드 검수 | `verified` |
| `W07` | 통합 검수 | 테스트·링크·민감정보·렌더링 | 공통 검수표와 한계 기록 | `verified` |
| `W08` | GitHub 공개 | PR·Pages URL | 원격 배포와 공개 응답 확인 | `published` |
| `W09` | OpsMate bounded public deployment | 실제 모델·network/security/lifecycle evidence | internal + public E2E와 final CLOSED | `verified` |
| `W10` | 기간 조회 데이터 정합성 사례 공개 | MyBatis 독립 샘플 | source review + regression + Pages | `published` |
| `W11` | 배포·복구 이식성 사례 공개 | WAR/context/profile/rollback 샘플 | regression + Pages | `published` |
| `W12` | 업무 규칙 일관성 사례 공개 | identity/reference-period 독립 샘플 | regression + Pages | `published` |
| `W13` | capability-first 첫 화면 재구성 | README·Pages·strategy·AI context | HR role clarity + evidence hierarchy | `verified` |
| `W14` | Python/FastAPI Data / AI Service Integration 공개 증거 | 독립 Python/FastAPI + synthetic data + tests | failure-boundary tests + regression + Pages | `pending` |

## 핵심 검증 기록

### W03 — 사용자 로그인·권한 통합 — `published`

- 독립 Java 21 / Spring Boot 합성 인증 샘플
- 24개 자동 테스트로 인증 수렴, RBAC, session/CSRF, replay와 fail-closed 경계를 검증
- main Pages publication gate PASS
- 실제 외부 IdP, 운영 DB, 분산 session, 대규모 부하/SLA는 미검증

### W09 — OpsMate Local — `verified`

- 실제 `gemma3:12b` 모델로 9개 핵심 합성 업무 시나리오 E2E 전건 성공
- Synology internal bounded E2E PASS
- public Internet HTTPS에서 사용자 작업 분리, rate limit, DB/model 비노출, lifecycle PASS
- final runtime `CLOSED`, workload container 0, persistent DB volume preserved
- 세부 응답시간과 project gate는 상세 evidence 문서에 유지

이 결과는 bounded E2E이며 24x7 SLA, 장기 부하, 대규모 실제 사용자 운영을 뜻하지 않습니다.

### W10 — 복잡한 기간 조회 데이터 정합성 — `published`

- 독립 Spring Boot/MyBatis/H2 합성 샘플
- 자동 테스트 12개
- 기간 경계, tenant isolation, count/page consistency, deterministic pagination 검증
- actual Oracle optimizer/performance는 미검증

### W11 — 환경이 달라도 배포·복구 가능한 구조 — `published`

- 독립 Spring Boot WAR 샘플
- 자동 테스트 10개
- external configuration, context path, health failure, rollback 검증
- 실제 운영 Tomcat zero-downtime/session drain/SLA는 미검증

### W12 — 업무 규칙 일관성 — `published`

- 독립 Java/Spring 합성 샘플
- 자동 테스트 11개
- canonical session identity, limited fallback, latest/explicit policy, service/data-access boundary와 400/401/404 fail-closed 검증
- 회사 코드·SQL·schema·내부 endpoint를 공개하지 않음

### W13 — capability-first 포트폴리오 — `verified`

공개 첫 화면과 AI 해석 규칙은 다음 순서를 사용합니다.

```text
문제와 결과
-> engineering capability
-> verified evidence
-> technical detail
-> technology
```

2026-08-30 독립 Codex/Gemini read-only 검토에서는 현재 `Backend Engineer` 방향을 유지하되 다음 P0 정렬이 필요하다고 확인했습니다.

- 공개 재현 evidence가 Java/Spring 쪽에 더 강하다는 사실을 숨기지 않음
- Python/FastAPI 실무 경험은 유지하되 독립 공개 evidence gap을 명시
- 공개 검증보다 앞선 `RAG/Agent patterns`, `observability` 같은 상위 용어를 첫 화면에서 내림
- p95/테스트 개수보다 실제 시나리오와 실패 차단 경계를 먼저 제시

현재 branch에서 위 문구 정렬을 publication 재검증 중입니다.

### W14 — Python/FastAPI Data / AI Service Integration — `pending`

목적은 Python 프로젝트 수를 늘리는 것이 아니라 **현재 공개 evidence의 실제 공백을 하나의 강한 사례로 채우는 것**입니다.

선정 범위:

```text
FastAPI API
-> natural-language question
-> SQL candidate
-> read-only validation/policy
-> bounded execution
-> result/evaluation status
```

핵심 검증:

- generation success / policy success / execution success / answer correctness 분리
- unsafe/write SQL 차단
- row/timeout limit
- invalid input와 unsupported query fail-closed
- deterministic fixture/provider 기반 core regression
- synthetic/public data only

외부 paid model, generic CRUD, generic RAG chatbot은 필수 범위가 아닙니다.

## 다음 실행 순서

1. W13 P0 문구 정렬 PR regression과 Pages publication을 마감합니다.
2. 변경된 공개 main을 Synology self-hosted 경로의 Codex/Gemini로 다시 독립 검토합니다.
3. 새 P0 blocker가 없으면 W14 Python/FastAPI Data / AI Service Integration 한 건을 시작합니다.
4. 기존 Java/Spring 사례는 추가 개수 확대보다 유지관리합니다.
5. Pages/링크/모바일 baseline과 회사 evidence 월말 갱신을 반복 유지합니다.
