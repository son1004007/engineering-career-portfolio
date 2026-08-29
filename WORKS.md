# Portfolio Work Ledger

- 기준일: `2026-08-29`
- 목적: 포트폴리오 작성부터 GitHub Pages 공개, OpsMate bounded public deployment 검증, Java/Spring 사례 공개까지 작업·의존성·증거를 추적
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
| `published` | 검증된 산출물이 공개 GitHub Pages에서 확인됨 |

## Work 현황

| Work | 목표 | 핵심 산출물 | 완료 조건 | 현재 상태 |
|---|---|---|---|---|
| `W00` | GitHub 게시 환경 준비 | 인증된 GitHub CLI, 작업 브랜치 | CLI·계정·remote·branch 확인 | `verified` |
| `W01` | 회사·개인 근거 정합화 | claim, 사례 인덱스, 추가 업무 계정 귀속 | 비식별 claim과 후보 상태 일치 | `verified` |
| `W02` | 검수 체계 고정 | 이 ledger와 공통 체크리스트 | 모든 Work의 증거·공개 gate 정의 | `verified` |
| `W03` | 첫 Java/Spring 사례 공개 | 인증 통합 게시물, 독립 샘플, 테스트 | 정상·실패·경계 테스트 성공과 공개 검수 | `verified` |
| `W04` | 추가 업무 사례 정리 | Java/Spring·데이터·AI 사례 초안 | 각 글의 근거·한계·재현 계획 명시 | `verified` |
| `W05` | OpsMate Local 구현 | 설계, 수직 기능, 테스트 | RBAC·상태·멱등성·fail-closed 테스트 성공 | `verified` |
| `W06` | GitHub Pages 블로그 구현 | 홈, 탐색, 레이아웃, 배포 workflow | 반응형 규칙·링크·접근성·빌드 검수 | `verified` |
| `W07` | 통합 검수 | 테스트·링크·민감정보·렌더링 결과 | 공통 검수표 필수 항목과 한계 기록 | `verified` |
| `W08` | GitHub 공개 | commit, push, PR, Pages URL | 원격 배포 성공과 공개 URL 응답 확인 | `published` |
| `W09` | OpsMate 공개 데모와 재현 가능한 운영 검증 | immutable release, private DB/model path, public HTTPS E2E, lifecycle evidence | internal + public network/security/lifecycle bounded E2E와 final CLOSED | `verified` |
| `W10` | 두 번째 Java/Spring 사례 공개 | MyBatis 기간 조회 독립 샘플, 12개 테스트, 사례 게시물 | 본인 귀속/공개 경계 확인 + sample CI + 전체 regression + Pages 게시 | `in-progress` |

## 의존성

```text
W00 -> W01 -> W02
             |-> W03 --|
             |-> W04 --|-> W07 -> W08 -> W09 -> W10
             |-> W05 --|
             `-> W06 --'
```

## 핵심 검증 기록

### W00-W08

- 첫 공개 포트폴리오, Java/Spring 인증 통합 샘플, Jekyll Pages, 공개 링크/민감정보 검수는 완료 상태를 유지합니다.
- Spring Security 인증 브리지는 회사 코드와 독립된 합성 샘플로 최근 테스트 성공 근거를 유지합니다.
- 회사 업무는 원본 비공개 코드를 복사하지 않고 비식별 claim과 독립 재현 상태를 분리합니다.
- OpsMate public evidence 반영 main Pages run `33250726427`의 verify matrix, Jekyll build와 deploy가 모두 성공 (`2026-08-29`).

### W09 — OpsMate Local

#### 구현/CI

- Spring Boot 기반 구매 초안·제출·승인·반려·발주·감사 수직 기능
- 공개 Thymeleaf/HttpSession UI, CSRF, 서버 생성 persona/workspace
- workspace 격리·TTL, model single-flight/quota/concurrency guard
- PostgreSQL/Flyway admin·migration·runtime role 분리
- private Docker network, non-root/read-only image, loopback Nginx edge
- restricted SSH model tunnel과 fail-closed model boundary
- normal close, emergency close, strict CLOSED verifier, same-digest reopen 자산
- reviewed runtime source: `f99686981da7efb8802635ae2bde5b0f781433ad`
- repository regression run `32848946968`: 성공 (`2026-08-25`)

#### 실제 모델 단독 E2E

- Ollama `0.13.5`, `gemma3:12b`
- 합성 요청 9/9 성공
- 요청·감사 이벤트 각각 9건
- 관측 p95 `21,076ms`, gate `<= 30,000ms`
- 상세: [`02_projects/opsmate-local/docs/REAL_MODEL_E2E_EVIDENCE.md`](02_projects/opsmate-local/docs/REAL_MODEL_E2E_EVIDENCE.md)

#### immutable release

- app: `ghcr.io/son1004007/opsmate-local@sha256:61e267c05bf0ce0ea932ae62a3989194bd2a0065532d0ee4caee8b37c8f9d40b`
- tunnel: `ghcr.io/son1004007/opsmate-model-tunnel@sha256:7fc133485a8ba60190e55b5eeb2da8b5eb02c1aa7e70e0cc0ce7b723746db1df`
- publish/pull verification run `32848946995`: PASS
- Synology runtime preparation run `32849378114`: exact digest pull/stage, input permission `600`, PostgreSQL volume 보존, running container `0`, final `CLOSED`: PASS

#### Synology internal E2E — `verified`

`2026-08-25` bounded runtime run `32849533407`에서 stack/port/network/session/model/rate/log/lifecycle gate와 final `CLOSED`를 통과했습니다.

상세: [`02_projects/opsmate-local/docs/NAS_INTERNAL_E2E_EVIDENCE.md`](02_projects/opsmate-local/docs/NAS_INTERNAL_E2E_EVIDENCE.md)

#### Public Internet deployment/lifecycle E2E — `verified`

`2026-08-29` bounded public runtime run `33241004788`에서 다음을 통과했습니다.

- 실제 Internet HTTPS root/live marker
- public API denial 및 actuator 차단
- 실제 모델 draft -> submit -> approve -> order -> audit -> cleanup
- 외부 두 session cross-workspace isolation
- URL `;jsessionid` rewriting 부재
- app direct Internet egress blocked
- 외부 PostgreSQL/model/loopback edge 직접 TCP 비노출
- bounded burst 60건: allowed `24`, HTTP `429` `36`, transport failure `0`
- credential/private-key/Bearer marker log scan
- normal close, same-digest reopen, emergency close, recovery normal close
- final running container `0`, PostgreSQL volume 보존, `runtime_policy_flags=YES_YES`, `CLOSED`

상세: [`02_projects/opsmate-local/docs/PUBLIC_DEPLOYMENT_E2E_EVIDENCE.md`](02_projects/opsmate-local/docs/PUBLIC_DEPLOYMENT_E2E_EVIDENCE.md)

이 결과는 bounded E2E이며 24x7 SLA, 장기 부하, 대규모 실제 사용자 운영을 뜻하지 않습니다. 검증 종료 후 workload는 의도대로 `CLOSED`입니다.

### W10 — 두 번째 Java/Spring 사례 `CS-JAVA-02`

#### authorized source review

권한 있는 비공개 원본에서 본인 귀속 변경으로 다음 범위를 재확인했습니다.

- 여러 연도 기간 조건의 복합 `OR`을 시작/중간/종료 구간으로 분해
- 서로 겹치지 않는 구간을 `UNION ALL`로 결합
- 검색·정렬 year/month column의 숫자 변환 제거

회사 SQL, schema, 데이터와 내부 식별자는 공개 저장소에 복사하지 않습니다.

#### independent public sample

[`02_projects/case-study-samples/mybatis-query-correctness/`](02_projects/case-study-samples/mybatis-query-correctness/README.md)에 Java 21 + Spring Boot 3.5.16 + MyBatis + H2 합성 샘플을 구현했습니다.

검증 범위:

- same-year / cross-year 경계
- 누락·중복 부재, tenant isolation
- count/page 동일 filter semantics
- deterministic pagination
- invalid input boundary
- 합성 복합 인덱스 존재
- BoundSql 기반 `UNION ALL` branch와 indexed-column conversion/period-OR 부재

자동 테스트 12개가 성공했습니다. PR run `33251026033`의 `MyBatis query correctness` job에서 `./mvnw -q clean verify`가 PASS했습니다.

현재 남은 W10 gate는 **최신 전체 PR regression 통과 → main merge → GitHub Pages 게시/링크 확인**입니다. Oracle optimizer의 index 선택과 운영 성능 수치는 공개 검증하지 않았으므로 주장하지 않습니다.

## 다음 실행 순서

1. W10의 최신 전체 PR regression을 통과시킵니다.
2. PR을 merge하고 Pages build/deploy 및 공개 case/sample 링크를 확인합니다.
3. W10을 `published`로 닫고 다음 `source-reviewed` Java/Spring 후보를 선택합니다.
4. `evidence/company-github/monthly/`를 월말 갱신하고 Pages/링크를 유지관리합니다.
