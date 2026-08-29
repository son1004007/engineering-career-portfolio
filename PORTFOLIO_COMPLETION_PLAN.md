# Portfolio Completion Plan

- 기준일: `2026-08-29`
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
- [x] PR regression 성공
- [x] main Pages run `33250726427`의 verify matrix, Jekyll build와 deploy 성공

사용자 작업: 없음.

### P10. repository regression — `verified`

- [x] OpsMate Maven regression
- [x] Spring Security 샘플 regression
- [x] 공개 링크·credential·상태 정합성 검사
- [x] Jekyll build
- [x] shell/Compose/Nginx/container/runbook 검증
- [x] non-root image build 및 migration rehearsal

OpsMate public evidence가 반영된 main commit에서도 Pages run `33250726427` 전체 성공.

사용자 작업: 없음.

### P20. OpsMate NAS 배포 준비 — `verified`

- [x] Synology Docker/x86_64 runtime
- [x] loopback Nginx edge 구조
- [x] destination-restricted SSH key/host-key pin 경계
- [x] immutable linux/amd64 app/tunnel image 발행 및 pull verification
- [x] exact immutable image Synology pull/stage
- [x] NAS-local runtime input permission `600`
- [x] PostgreSQL persistent volume 보존과 DB credential continuity
- [x] 준비 종료 후 running container `0`, final `CLOSED`

검증: runtime preparation run `32849378114`.

### P25. OpsMate 내부 network/security/lifecycle E2E — `verified`

검증: private bounded runtime run `32849533407`.

- [x] stack/host-port/Docker-network/edge security
- [x] app/edge direct egress 차단
- [x] DB/app/model-tunnel host port 미노출
- [x] Secure cookie session과 restricted actual-model path
- [x] persona/durable draft/cross-workspace isolation
- [x] rate limit/credential-log scan
- [x] normal close/same-digest reopen/emergency close/recovery close
- [x] final policy flags `YES_YES`, final `CLOSED`

### P30. OpsMate public HTTPS/network/security E2E — `verified`

검증: private bounded public runtime run `33241004788`.

- [x] 실제 Internet HTTPS origin
- [x] public API/actuator boundary
- [x] 실제 모델 전체 persona workflow
- [x] external two-session isolation
- [x] URL session rewriting 부재
- [x] app direct Internet egress 차단
- [x] DB/model/loopback edge 외부 직접 비노출
- [x] bounded rate burst: allowed `24`, HTTP `429` `36`, transport failure `0`
- [x] credential/private-key/Bearer marker log scan

### P40. OpsMate public close/reopen lifecycle — `verified`

- [x] public OPEN smoke
- [x] normal close + purge + strict CLOSED
- [x] same immutable digest reopen + real-model public smoke
- [x] emergency close + public live marker 부재
- [x] recovery normal close/purge
- [x] running container `0`, PostgreSQL volume 보존
- [x] final `runtime_policy_flags=YES_YES`, final `CLOSED`

이 증거는 bounded E2E이며 24x7 SLA, 장기 부하 또는 production traffic 규모를 의미하지 않는다.

### P50. 두 번째 Java/Spring 사례 `CS-JAVA-02` — `in-progress`

주제: **MyBatis 기간 조회의 정합성과 인덱스 친화 조건을 함께 설계하기**

- [x] 후보 선택
- [x] authorized source에서 본인 귀속 SQL 개선 범위 재확인
- [x] 공개 금지 경계 확정: 회사 SQL/schema/data/내부 식별자 비복사
- [x] 독립 합성 요구사항·테스트 설계
- [x] Java 21 + Spring Boot 3.5.16 + MyBatis + H2 독립 샘플 구현
- [x] same/cross-year, tenant isolation, count/page, deterministic pagination, invalid input 테스트
- [x] synthetic composite index 및 BoundSql SQL-shape 검증
- [x] 12개 테스트 `./mvnw -q clean verify` 성공 — PR run `33251026033`의 MyBatis job
- [x] 사례 게시물·README·ARCHITECTURE·SETUP·VERIFICATION 작성
- [ ] 최신 전체 PR regression 성공
- [ ] main merge
- [ ] GitHub Pages build/deploy 및 공개 case/sample 링크 확인

Oracle optimizer의 index 선택과 운영 성능 수치는 공개 검증하지 않았으므로 주장하지 않는다.

사용자 작업: 없음.

### P55. 다음 Java/Spring 사례 — `pending`

`CS-JAVA-02`가 Pages에 게시된 뒤 다음 `source-reviewed` 후보를 선택한다.

선택 기준:

1. 인증과 SQL 사례와 다른 backend dimension을 추가할 것
2. 본인 귀속과 공개 경계를 재확인할 수 있을 것
3. 회사 코드와 독립된 합성 샘플로 정상·실패·경계 테스트가 가능할 것
4. 확인되지 않은 운영 성과를 필요로 하지 않을 것

### P60. 포트폴리오 유지관리 — `pending`

- [ ] Pages 링크/배포 상태 정기 확인
- [ ] 물리 모바일 최종 UX 검수
- [ ] 회사 GitHub evidence 월말 갱신
- [ ] 완료·미검증 badge와 테스트/증거 동기화

## 현재 사용자에게 필요한 작업

현재 즉시 필요한 사용자 작업은 없다.

OpsMate workload는 `CLOSED` 상태를 유지하고 있으며 현재 active work는 `CS-JAVA-02`의 publication gate다.

## 완료 판정

1. GitHub Pages 포트폴리오: `published`
2. OpsMate code/regression + real-model/internal/public bounded E2E: `verified`
3. OpsMate 24x7 운영/SLA/장기 부하: `not claimed`
4. 첫 Java/Spring 인증 사례: `sample-verified`
5. 두 번째 MyBatis 사례: sample CI `verified`, Pages publication pending
6. 회사 업무 사례: 권한 있는 원본 검토와 독립 공개 샘플 검증 상태를 분리
