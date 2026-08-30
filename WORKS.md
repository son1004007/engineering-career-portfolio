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
| `W00` | GitHub 게시 환경 준비 | 인증된 GitHub CLI, 작업 브랜치 | CLI·계정·remote·branch 확인 | `verified` |
| `W01` | 회사·개인 근거 정합화 | claim, 사례 인덱스, 추가 업무 계정 귀속 | 비식별 claim과 후보 상태 일치 | `verified` |
| `W02` | 검수 체계 고정 | 이 ledger와 공통 체크리스트 | 모든 Work의 증거·공개 gate 정의 | `verified` |
| `W03` | 사용자 로그인·권한 통합 사례 공개 | 인증 통합 게시물, 독립 샘플, 테스트 | 정상·실패·경계 테스트 성공과 공개 검수·Pages deploy | `published` |
| `W04` | 추가 실무 문제 사례 정리 | backend·data·AI·operations 사례 초안 | 각 글의 근거·한계·재현 계획 명시 | `verified` |
| `W05` | Controlled AI Integration 구현 | OpsMate 설계, 수직 기능, 테스트 | RBAC·상태·멱등성·fail-closed 테스트 성공 | `verified` |
| `W06` | GitHub Pages 포트폴리오 구현 | 홈, 탐색, 레이아웃, 배포 workflow | 반응형 규칙·링크·접근성·빌드 검수 | `verified` |
| `W07` | 통합 검수 | 테스트·링크·민감정보·렌더링 결과 | 공통 검수표 필수 항목과 한계 기록 | `verified` |
| `W08` | GitHub 공개 | commit, push, PR, Pages URL | 원격 배포 성공과 공개 URL 응답 확인 | `published` |
| `W09` | OpsMate 공개 데모와 재현 가능한 운영 검증 | immutable release, private DB/model path, public HTTPS E2E, lifecycle evidence | internal + public network/security/lifecycle bounded E2E와 final CLOSED | `verified` |
| `W10` | 복잡한 기간 조회 데이터 정합성 사례 공개 | MyBatis 기간 조회 독립 샘플, 12개 테스트, 사례 게시물 | source review + sample CI + 전체 regression + Pages deploy | `published` |
| `W11` | 환경이 달라도 배포·복구 가능한 구조 사례 공개 | WAR/context-path/profile/health/rollback 독립 샘플, 10개 테스트, 사례 게시물 | source review + sample CI + 전체 regression + Pages deploy | `published` |
| `W12` | 업무 규칙 일관성 사례 공개 | 업무 규칙 정합성 독립 샘플, 11개 테스트, 사례 게시물 | source re-review + 독립 구현 + sample CI + 전체 regression + Pages deploy | `published` |
| `W13` | capability-first 첫 화면 재구성 | README, HOW_I_ENGINEER, profile, strategy, AI context | HR이 문제와 결과를 먼저 이해하고 기술 세부로 내려갈 수 있음 | `verified` |

## 의존성

```text
W00 -> W01 -> W02
             |-> W03 --|
             |-> W04 --|-> W07 -> W08 -> W09 -> W10 -> W11 -> W12 -> W13
             |-> W05 --|
             `-> W06 --'
```

## 핵심 검증 기록

### W00-W08

- 첫 공개 포트폴리오, 사용자 로그인·권한 통합 독립 샘플, Jekyll Pages, 공개 링크/민감정보 검수는 완료 상태를 유지합니다.
- 인증 통합 샘플은 회사 코드와 독립된 합성 샘플로 24개 자동 테스트 성공 근거를 유지합니다.
- main commit `c74655a2e7aacfa0d05f41bc594598a0c0f73296`의 Pages run `33276912458`에서 인증 regression, public/Jekyll 검사, Pages build/deploy가 모두 성공해 publication gate를 충족했습니다.
- 회사 업무는 원본 비공개 코드를 복사하지 않고 비식별 claim과 독립 재현 상태를 분리합니다.
- OpsMate public evidence 반영 main Pages run `33250726427`의 verify matrix, Jekyll build와 deploy가 모두 성공 (`2026-08-29`).
- case-study publication-state 동기화 main Pages run `33252607907`도 verify/build/deploy가 성공했습니다.

### W03 — 사용자 로그인·권한 통합 — `published`

- 독립 Java 21 / Spring Boot 3.5.16 합성 인증 샘플
- 24개 자동 테스트: DB/SSO 로그인, RBAC, session rotation, CSRF lifecycle, issuer/audience/keyId/signature, nonce replay, 시간 경계와 fail-closed 설정
- main publication evidence commit `c74655a2e7aacfa0d05f41bc594598a0c0f73296`
- main Pages run `33276912458`: authentication regression + public portfolio + Jekyll + Pages build/deploy PASS
- 실제 외부 IdP, 운영 DB, 분산 session, Redis replay store, 대규모 부하/SLA는 미검증

### W09 — OpsMate Local

- reviewed runtime source: `f99686981da7efb8802635ae2bde5b0f781433ad`
- app image: `ghcr.io/son1004007/opsmate-local@sha256:61e267c05bf0ce0ea932ae62a3989194bd2a0065532d0ee4caee8b37c8f9d40b`
- tunnel image: `ghcr.io/son1004007/opsmate-model-tunnel@sha256:7fc133485a8ba60190e55b5eeb2da8b5eb02c1aa7e70e0cc0ce7b723746db1df`
- actual-model E2E: Ollama `0.13.5`, `gemma3:12b`, 합성 요청 9/9, 관측 p95 `21,076ms` (`<= 30,000ms`)
- Synology internal bounded E2E run `32849533407`: stack/network/session/model/rate/log/lifecycle + final `CLOSED` PASS
- public Internet bounded E2E run `33241004788`: HTTPS persona flow, two-session isolation, API/actuator boundary, egress/non-exposure, `429`, log scan, normal/reopen/emergency/recovery lifecycle PASS
- final running workload container `0`, PostgreSQL persistent volume preserved, `runtime_policy_flags=YES_YES`, `CLOSED`

이 결과는 bounded E2E이며 24x7 SLA, 장기 부하, 대규모 실제 사용자 운영을 뜻하지 않습니다.

### W10 — 복잡한 기간 조회 데이터 정합성 — `published`

- 독립 Spring Boot/MyBatis/H2 합성 샘플, 12개 자동 테스트
- final PR regression run `33251272174`: PASS
- main Pages run `33251362190`: verify/build/deploy PASS
- Oracle optimizer index 선택과 운영 성능 수치는 미검증

### W11 — 환경이 달라도 배포·복구 가능한 구조 — `published`

- 독립 Java 21 / Spring Boot 3.5.16 WAR 샘플, 10개 자동 테스트
- final PR regression run `33252086213`: 전체 PASS
- main commit `63abaa49e05a366d6007902edd184a83df6bc7e9`
- main Pages run `33252148733`: verify/build/deploy PASS
- 실제 외부 운영 Tomcat rolling deployment, session drain, zero-downtime, SLA는 미검증

### W12 — 업무 규칙 일관성 — `published`

권한 있는 비공개 원본에서 본인 author/committer 변경을 다시 확인하고, 회사 코드와 독립된 Java 21 / Spring Boot 3.5.16 합성 `member snapshot` 샘플로 재현했습니다.

검증된 경계:

- canonical session identity 우선, canonical 부재 시에만 legacy fallback
- identity 부재 시 `401` fail-closed
- `LATEST_ONLY`와 `EXPLICIT_OR_LATEST` snapshot policy
- Service가 확정한 `subjectId + SnapshotKey`만 Mapper에 전달
- 잘못된/불완전한 기간 `400`, snapshot 부재 `404`
- 요청 parameter가 session identity를 덮어쓸 수 없는 경계

증거:

- 자동 회귀 테스트 11개
- 최초 PR run `33275860098`: 신규 sample job + 전체 8개 job PASS
- 상태 동기화 후 final PR run `33276143715`: 전체 8개 job PASS
- main merge commit `733db7c614af5613216773b3b1fc6b3567e0b84c`
- main Pages run `33276278894`: 8개 verify job + Pages build + deploy PASS
- 공개 문서에는 회사 클래스명, endpoint, field, SQL, schema, 테스트 계정, 실제 데이터와 내부 식별자를 복사하지 않음

실제 회사 시스템 전체 SSO/session, Mapper SQL/운영 DB, 운영 데이터 전체 정합성, 조직 전체 업무 규칙 설계 책임, 운영 성능/SLA는 검증하거나 주장하지 않습니다.

### W13 — capability-first 포트폴리오 — `verified`

공개 첫 화면과 AI 해석 규칙을 다음 방향으로 변경했습니다.

```text
문제와 결과
-> engineering capability
-> verified evidence
-> technical detail
-> technology
```

추가 또는 갱신 문서:

- `README.md`
- `HOW_I_ENGINEER.md`
- `AGENTS.md`
- `AI_CONTEXT.md`
- `01_profile/career-summary.md`
- `01_profile/core-strengths.md`
- `01_profile/career-direction.md`
- `03_portfolio/portfolio-strategy.md`
- `03_portfolio/portfolio-overview.md`

목표는 Java/Spring을 숨기는 것이 아니라, 언어보다 problem framing, backend/data integration, AI use, verification, security와 operations를 먼저 보여주는 것입니다.

## 다음 실행 순서

1. 기존 공개 사례 4건은 추가 개발보다 문제 중심 제목과 HR-readable 도입부를 유지합니다.
2. 신규 사례는 특정 언어 수를 늘리는 방식이 아니라 capability map의 증거 공백을 기준으로 선택합니다.
3. data/AI/platform/security/operations 중 공개 evidence가 상대적으로 약한 영역을 다음 독립 사례 후보로 우선합니다.
4. Pages/링크/모바일 baseline 회귀와 `evidence/company-github/monthly/` 월말 갱신을 유지관리합니다.
