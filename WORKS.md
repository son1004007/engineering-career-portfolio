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
| `published` | 검증된 산출물이 공개 GitHub Pages 배포 gate를 통과 |

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
| `W10` | 두 번째 Java/Spring 사례 공개 | MyBatis 기간 조회 독립 샘플, 12개 테스트, 사례 게시물 | source review + sample CI + 전체 regression + Pages deploy | `published` |
| `W11` | 세 번째 Java/Spring 사례 공개 | WAR/context-path/profile/health/rollback 독립 샘플, 10개 테스트, 사례 게시물 | source review + sample CI + 전체 regression + Pages deploy | `published` |
| `W12` | 네 번째 Java/Spring 사례 공개 | `CS-JAVA-06` 업무 규칙 정합성 독립 샘플 | source re-review + 독립 구현 + 정상·실패·경계 테스트 + Pages 게시 | `in-progress` |

## 의존성

```text
W00 -> W01 -> W02
             |-> W03 --|
             |-> W04 --|-> W07 -> W08 -> W09 -> W10 -> W11 -> W12
             |-> W05 --|
             `-> W06 --'
```

## 핵심 검증 기록

### W00-W08

- 첫 공개 포트폴리오, Java/Spring 인증 통합 샘플, Jekyll Pages, 공개 링크/민감정보 검수는 완료 상태를 유지합니다.
- Spring Security 인증 브리지는 회사 코드와 독립된 합성 샘플로 24개 자동 테스트 성공 근거를 유지합니다.
- 회사 업무는 원본 비공개 코드를 복사하지 않고 비식별 claim과 독립 재현 상태를 분리합니다.
- OpsMate public evidence 반영 main Pages run `33250726427`의 verify matrix, Jekyll build와 deploy가 모두 성공 (`2026-08-29`).

### W09 — OpsMate Local

- reviewed runtime source: `f99686981da7efb8802635ae2bde5b0f781433ad`
- app image: `ghcr.io/son1004007/opsmate-local@sha256:61e267c05bf0ce0ea932ae62a3989194bd2a0065532d0ee4caee8b37c8f9d40b`
- tunnel image: `ghcr.io/son1004007/opsmate-model-tunnel@sha256:7fc133485a8ba60190e55b5eeb2da8b5eb02c1aa7e70e0cc0ce7b723746db1df`
- actual-model E2E: Ollama `0.13.5`, `gemma3:12b`, 합성 요청 9/9, 관측 p95 `21,076ms` (`<= 30,000ms`)
- Synology internal bounded E2E run `32849533407`: stack/network/session/model/rate/log/lifecycle + final `CLOSED` PASS
- public Internet bounded E2E run `33241004788`: HTTPS persona flow, two-session isolation, API/actuator boundary, egress/non-exposure, `429`, log scan, normal/reopen/emergency/recovery lifecycle PASS
- final running workload container `0`, PostgreSQL persistent volume preserved, `runtime_policy_flags=YES_YES`, `CLOSED`

상세:

- [`02_projects/opsmate-local/docs/REAL_MODEL_E2E_EVIDENCE.md`](02_projects/opsmate-local/docs/REAL_MODEL_E2E_EVIDENCE.md)
- [`02_projects/opsmate-local/docs/NAS_INTERNAL_E2E_EVIDENCE.md`](02_projects/opsmate-local/docs/NAS_INTERNAL_E2E_EVIDENCE.md)
- [`02_projects/opsmate-local/docs/PUBLIC_DEPLOYMENT_E2E_EVIDENCE.md`](02_projects/opsmate-local/docs/PUBLIC_DEPLOYMENT_E2E_EVIDENCE.md)

이 결과는 bounded E2E이며 24x7 SLA, 장기 부하, 대규모 실제 사용자 운영을 뜻하지 않습니다.

### W10 — `CS-JAVA-02` MyBatis 기간 조회 — `published`

권한 있는 비공개 원본에서 본인 귀속 SQL 개선 범위를 재확인하고 회사 SQL/schema/data/식별자를 복사하지 않은 합성 Spring Boot/MyBatis/H2 샘플을 구현했습니다.

- 기간 복합 `OR`을 상호 배타적 시작/중간/종료 구간으로 분해하고 `UNION ALL`로 결합하는 원칙 재현
- indexed year/month column 숫자 변환 제거 원칙 재현
- same/cross-year 경계, tenant isolation, count/page 공통 filter, deterministic pagination, invalid input, composite index와 BoundSql shape 검증
- 자동 테스트 12개
- PR sample run `33251026033`: PASS
- 최종 PR regression run `33251272174`: 6개 job 전체 PASS
- main commit `a1a58a469056073165b110ab2dc61f83c7d0ad20`
- main Pages run `33251362190`: verify/build/deploy PASS

실제 Oracle optimizer의 index 선택과 운영 성능 수치는 검증하지 않았으므로 주장하지 않습니다.

### W11 — `CS-JAVA-03` WAR 배포 이식성 — `published`

권한 있는 비공개 원본에서 본인 귀속 WAR deploy workflow/runbook, context-path 보정, profile/config 외부화 범위를 재확인했습니다. 회사 WAR/JSP/workflow/호스트/경로/인증정보는 공개 샘플에 복사하지 않았습니다.

독립 Java 21 / Spring Boot 3.5.16 WAR 샘플은 다음을 재현합니다.

- `<packaging>war</packaging>` + provided Tomcat
- 외부 Servlet Container용 `SpringBootServletInitializer`
- non-root `/demo` context path에서 runtime-relative entry/health
- `deploy` profile 필수 외부 값 누락 시 fail-closed
- candidate WAR backup/replace/health gate/rollback
- unsafe application name 거부
- 자동 테스트 10개

검증:

- 최초 sample run `33252018737`: WAR job PASS
- 최종 PR regression run `33252086213`: WAR, MyBatis, Spring Security, OpsMate, Jekyll, public portfolio, container/runbook 7개 job 전체 PASS
- main commit `63abaa49e05a366d6007902edd184a83df6bc7e9`
- main Pages run `33252148733`: 동일 7개 verify job + Pages build + deploy PASS
- context-path CI 관측 환경: Java `21.0.12`, Spring Boot `3.5.16`, embedded Tomcat `10.1.55`

실제 외부 운영 Tomcat rolling deployment, session drain, zero-downtime, 운영 SLA는 검증하지 않았습니다.

### W12 — `CS-JAVA-06` 업무 규칙 정합성 — `in-progress`

현재 active work입니다.

1. 권한 있는 비공개 원본에서 본인 귀속과 실제 결함/수정 범위를 다시 확인합니다.
2. 인증·SQL·배포 사례와 중복되지 않는 Controller-Service-Mapper 규칙 경계를 확정합니다.
3. 회사 코드와 독립된 주문·회원 합성 도메인으로 정상·실패·경계 테스트부터 설계합니다.
4. 독립 Spring 샘플을 구현하고 최근 CI 성공 뒤 사례 글을 공개합니다.

## 다음 실행 순서

1. 이 publication-state 변경의 전체 portfolio regression과 Pages deploy를 통과시킵니다.
2. `CS-JAVA-06` authorized source re-review를 수행합니다.
3. 공개 요구사항/회귀 테스트를 확정한 뒤 독립 Spring 샘플을 구현합니다.
4. `evidence/company-github/monthly/`를 월말 갱신하고 Pages/링크를 유지관리합니다.
