# Portfolio Completion Plan

- 기준일: `2026-08-30`
- 목적: 공개 포트폴리오의 검증 상태를 실제 실행 증거와 동기화하고 Java/Spring 사례 공개를 순차적으로 완료한다.
- 원칙: 문서상 완료가 아니라 source review, 독립 구현, 최근 CI와 필요한 runtime/publication evidence를 근거로 상태를 올린다.
- 전역 기준: `son1004007/ai-agent-workflow-playbook/CONTROL.md`
- 실행 ledger: [`WORKS.md`](WORKS.md)
- 장기 backlog: [`TASKS.md`](TASKS.md)
- OpsMate internal 증거: [`02_projects/opsmate-local/docs/NAS_INTERNAL_E2E_EVIDENCE.md`](02_projects/opsmate-local/docs/NAS_INTERNAL_E2E_EVIDENCE.md)
- OpsMate public 증거: [`02_projects/opsmate-local/docs/PUBLIC_DEPLOYMENT_E2E_EVIDENCE.md`](02_projects/opsmate-local/docs/PUBLIC_DEPLOYMENT_E2E_EVIDENCE.md)

## 상태

| 상태 | 의미 |
|---|---|
| `pending` | 아직 시작하지 않음 |
| `in-progress` | 현재 수행 중 |
| `blocked-user` | 사용자만 가능한 입력/승인 대기 |
| `blocked-external` | 외부 서비스/네트워크/runtime 조건 대기 |
| `verified` | 요구된 검증 증거까지 확보 |

## 검증된 OpsMate release

- portfolio runtime source: `f99686981da7efb8802635ae2bde5b0f781433ad`
- application image: `ghcr.io/son1004007/opsmate-local@sha256:61e267c05bf0ce0ea932ae62a3989194bd2a0065532d0ee4caee8b37c8f9d40b`
- model-tunnel image: `ghcr.io/son1004007/opsmate-model-tunnel@sha256:7fc133485a8ba60190e55b5eeb2da8b5eb02c1aa7e70e0cc0ce7b723746db1df`
- model: `gemma3:12b`
- 최종 runtime 상태: `CLOSED`

## 실행 순서

### P00. OpsMate 상태 문서 동기화 — `verified`

- [x] `2026-08-23` 실제 모델 E2E 기록
- [x] `2026-08-25` Synology internal deployment/network/security/lifecycle E2E 기록
- [x] `2026-08-29` public Internet deployment/network/lifecycle E2E 기록
- [x] AI context, work/task ledger, evidence index와 public evidence 문서 동기화
- [x] main Pages run `33250726427` verify/build/deploy 성공

사용자 작업: 없음.

### P10. repository regression — `verified`

- [x] OpsMate Maven regression
- [x] Spring Security 샘플 regression
- [x] 공개 링크·credential·상태 정합성 검사
- [x] Jekyll build
- [x] shell/Compose/Nginx/container/runbook 검증
- [x] non-root image build 및 migration rehearsal

사용자 작업: 없음.

### P20-P40. OpsMate deployment/network/security/lifecycle — `verified`

- NAS runtime preparation run `32849378114`: PASS
- internal bounded E2E run `32849533407`: PASS
- public Internet bounded E2E run `33241004788`: PASS
- final `runtime_policy_flags=YES_YES`, running workload container `0`, PostgreSQL volume preserved, final `CLOSED`

이 증거는 bounded E2E이며 24x7 SLA, 장기 부하 또는 production traffic 규모를 의미하지 않는다.

사용자 작업: 없음.

### P50. 두 번째 Java/Spring 사례 `CS-JAVA-02` — `verified`

주제: **MyBatis 기간 조회의 정합성과 인덱스 친화 조건을 함께 설계하기**

- [x] authorized source에서 본인 귀속 SQL 개선 범위 재확인
- [x] 회사 SQL/schema/data/내부 식별자 비복사 경계 확정
- [x] Java 21 + Spring Boot 3.5.16 + MyBatis + H2 독립 샘플 구현
- [x] same/cross-year, tenant isolation, count/page, deterministic pagination, invalid input 테스트
- [x] composite index 및 BoundSql SQL-shape 검증
- [x] 12개 자동 테스트
- [x] final PR regression run `33251272174` 성공
- [x] main commit `a1a58a469056073165b110ab2dc61f83c7d0ad20`
- [x] main Pages run `33251362190` verify/build/deploy 성공
- [x] 상태: `published`

Oracle optimizer index 선택과 운영 성능 수치는 공개 검증하지 않았으므로 주장하지 않는다.

사용자 작업: 없음.

### P55. 세 번째 Java/Spring 사례 `CS-JAVA-03` — `verified`

주제: **WAR 기반 Spring 서비스의 배포 이식성**

- [x] authorized source에서 WAR deploy workflow/runbook, context-path, profile/config 본인 귀속 범위 재확인
- [x] 회사 WAR/JSP/workflow/서버 식별자/배포 경로/인증정보 비복사 경계 확정
- [x] Java 21 + Spring Boot 3.5.16 독립 WAR 샘플 구현
- [x] external-container initializer / provided Tomcat 구조
- [x] non-root context path entry/health
- [x] deploy profile 외부 값 누락 fail-closed
- [x] backup -> replace -> health -> rollback rehearsal
- [x] unsafe application name 거부
- [x] 10개 자동 테스트
- [x] final PR regression run `33252086213`: 7개 job 전체 성공
- [x] main commit `63abaa49e05a366d6007902edd184a83df6bc7e9`
- [x] main Pages run `33252148733`: 7개 verify job + build + deploy 성공
- [x] 상태: `published`

CI context-path 관측은 Java `21.0.12`, Spring Boot `3.5.16`, embedded Tomcat `10.1.55`에서 수행됐다. 실제 외부 운영 Tomcat rolling deployment, session drain, zero-downtime와 SLA는 검증하지 않았다.

사용자 작업: 없음.

### P60. 네 번째 Java/Spring 사례 `CS-JAVA-06` — `verified`

주제: **여러 화면에 흩어진 기준값과 사용자 식별 규칙을 한 흐름으로 정합화한 과정**

- [x] authorized source에서 본인 author/committer와 실제 결함/수정 경계 재확인
- [x] 공개 금지 경계 확정: 회사 클래스명, endpoint, field, SQL, schema, 테스트 계정, 실제 데이터와 내부 식별자 비복사
- [x] canonical session identity / legacy fallback / latest-only / explicit-or-latest / Mapper 입력 소유권 요구사항 정의
- [x] 정상·실패·경계 회귀 테스트 설계
- [x] Java 21 + Spring Boot 3.5.16 독립 `member snapshot` 샘플 구현
- [x] 11개 MockMvc 회귀 테스트 성공
- [x] 최초 PR run `33275860098`: 신규 sample job 및 전체 8개 job 성공
- [x] 상태 동기화 후 final PR run `33276143715`: 전체 8개 job 성공
- [x] main merge commit `733db7c614af5613216773b3b1fc6b3567e0b84c`
- [x] main Pages run `33276278894`: 8개 verify job + build + deploy 성공
- [x] 상태: `published`

실제 회사 SSO/session E2E, 회사 Mapper SQL/운영 DB 결과, 운영 데이터 전체 정합성, 조직 전체 업무 규칙 설계 책임, 운영 성능/SLA는 검증하지 않는다.

사용자 작업: 없음.

### P70. 다음 Java/Spring 사례 선택 — `pending`

선택 기준:

1. 인증·SQL·WAR 배포·업무 규칙과 다른 backend dimension을 추가할 것
2. 본인 귀속과 공개 경계를 재확인할 수 있을 것
3. 회사 코드와 독립된 합성 샘플로 정상·실패·경계 테스트가 가능할 것
4. 확인되지 않은 운영 성과를 필요로 하지 않을 것

`CS-JAVA-11` 통계 분석 UI는 데이터 처리/시각화 관점을 추가할 수 있는 `source-reviewed` 후보입니다. 다음 사례로 채택하기 전 직접 기여 경계와 통계 검증 범위를 다시 확인합니다.

### P80. 포트폴리오 유지관리 — `pending`

- [ ] Pages 링크/배포 상태 정기 확인
- [ ] 물리 모바일 최종 UX 검수
- [ ] 회사 GitHub evidence 월말 갱신
- [ ] 완료·미검증 badge와 테스트/증거 동기화

## 현재 사용자에게 필요한 작업

현재 즉시 필요한 사용자 작업은 없다.

OpsMate workload는 `CLOSED` 상태를 유지한다. `CS-JAVA-02`, `CS-JAVA-03`, `CS-JAVA-06` publication gate는 완료되었으며 다음 단계는 신규 사례 선택과 유지관리다.

## 완료 판정

1. GitHub Pages 포트폴리오: `published`
2. OpsMate code/regression + real-model/internal/public bounded E2E: `verified`
3. OpsMate 24x7 운영/SLA/장기 부하: `not claimed`
4. 첫 Java/Spring 인증 사례: `sample-verified`
5. 두 번째 MyBatis 사례: `published`
6. 세 번째 WAR deployment 사례: `published`
7. 네 번째 업무 규칙 정합성 사례: `published`
8. 회사 업무 사례: 권한 있는 원본 검토와 독립 공개 샘플 검증 상태를 분리
