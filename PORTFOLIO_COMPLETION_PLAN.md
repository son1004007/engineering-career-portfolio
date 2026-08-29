# Portfolio Completion Plan

- 기준일: `2026-08-29`
- 목적: 공개 포트폴리오와 `OpsMate Local`의 검증 상태를 실제 실행 증거와 동기화하고 다음 Java/Spring 사례 공개를 진행한다.
- 원칙: 문서상 완료가 아니라 exact source, immutable image digest, CI, bounded runtime E2E를 근거로 상태를 올린다.
- 전역 기준: `son1004007/ai-agent-workflow-playbook/CONTROL.md`
- 실행 ledger: [`WORKS.md`](WORKS.md)
- 장기 backlog: [`TASKS.md`](TASKS.md)
- 내부 배포 증거: [`02_projects/opsmate-local/docs/NAS_INTERNAL_E2E_EVIDENCE.md`](02_projects/opsmate-local/docs/NAS_INTERNAL_E2E_EVIDENCE.md)
- public 배포 증거: [`02_projects/opsmate-local/docs/PUBLIC_DEPLOYMENT_E2E_EVIDENCE.md`](02_projects/opsmate-local/docs/PUBLIC_DEPLOYMENT_E2E_EVIDENCE.md)

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

### P00. 상태 문서 동기화 — `in-progress`

- [x] `2026-08-23` 실제 모델 E2E 기록
- [x] `2026-08-25` Synology internal deployment/network/security/lifecycle E2E 기록
- [x] `2026-08-29` public Internet deployment/network/lifecycle E2E 증거 확보
- [x] `AI_CONTEXT.md`, `WORKS.md`, `TASKS.md`, `evidence-index.md`, case-study queue를 public E2E 상태와 동기화
- [ ] 최신 public evidence/state 문서의 portfolio CI와 Pages deploy 성공 확인

사용자 작업: 없음.

### P10. repository regression — `verified`

- [x] OpsMate Maven regression
- [x] Spring Security 샘플 regression
- [x] 공개 링크·credential·상태 정합성 검사
- [x] Jekyll build
- [x] shell/Compose/Nginx/container/runbook 검증
- [x] non-root image build 및 migration rehearsal

검증 기준 release의 `Verify Portfolio` run `32848946968` 전체 성공. 이후 내부 E2E 상태 문서 반영 main Pages run `32851086949`도 성공했다.

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

사용자 작업: 없음.

### P25. 내부 network/security/lifecycle E2E — `verified`

`2026-08-25` bounded internal verifier에서 다음을 실제 target에서 확인했다.

- [x] stack/host-port/Docker-network/edge-security gate
- [x] app/edge direct egress 차단
- [x] PostgreSQL/app/model-tunnel host port 미노출
- [x] Secure XSRF/JSESSIONID 및 COOKIE-only session tracking
- [x] restricted tunnel -> 실제 `gemma3:12b` model path
- [x] persona flow, durable draft, cross-workspace isolation
- [x] Nginx rate limit `429`, credential/log scan
- [x] normal close + synthetic workspace purge + strict CLOSED
- [x] same-digest reopen
- [x] emergency close + recovery normal close
- [x] final policy flags `YES_YES`, final `CLOSED`

검증: private `device-control` bounded runtime run `32849533407`.

사용자 작업: 없음.

### P30. public HTTPS/network/security E2E — `verified`

`2026-08-29` 실제 Internet 경로에서 다음을 확인했다.

- [x] DSM Reverse Proxy/TLS + router ingress를 통한 HTTPS origin
- [x] root/live marker 및 public `/api/**` denial, `/actuator/**` 차단
- [x] 실제 모델 기반 draft -> submit -> approve -> order -> audit -> cleanup
- [x] 외부 두 session cross-workspace isolation
- [x] URL `;jsessionid` rewriting 부재
- [x] app direct Internet egress 차단
- [x] PostgreSQL/model/loopback edge 직접 외부 TCP 비노출
- [x] public Nginx bounded burst: allowed `24`, HTTP `429` `36`, transport failure `0`
- [x] container credential/private-key/Bearer marker log scan

검증: private `device-control` final public runtime run `33241004788`.

사용자 작업: 없음.

### P40. public close/reopen lifecycle — `verified`

동일 public runtime run에서 다음 lifecycle을 검증했다.

- [x] public OPEN smoke
- [x] normal close + synthetic workspace purge
- [x] public live marker 부재 + strict `CLOSED`
- [x] same app/tunnel digest reopen
- [x] reopen 후 public HTTPS + 실제 모델 persona smoke 재통과
- [x] emergency close
- [x] public live marker 부재 + strict `CLOSED`
- [x] recovery normal close/purge
- [x] running container `0`, PostgreSQL volume 보존
- [x] final `runtime_policy_flags=YES_YES`
- [x] final `CLOSED`

이 검증은 bounded deployment/lifecycle 증거이며 24x7 SLA, 장기 부하 또는 production traffic 규모를 의미하지 않는다.

사용자 작업: 없음.

### P50. 두 번째 Java/Spring 사례 공개 — `in-progress`

OpsMate 목표 gate가 완료됐으므로 현재 작업 우선순위는 여기다.

- [x] `CS-JAVA-02` — MyBatis·Oracle 업무 조회 정합성/성능 지향 사례 선택
- [ ] authorized evidence에서 본인 기여·공개 경계 재확인
- [ ] 회사 원본과 독립된 합성 요구사항·테스트 설계
- [ ] 합성 도메인으로 독립 구현
- [ ] 정상·실패·경계 테스트
- [ ] 공개 문서·코드 검수
- [ ] Pages 게시 및 링크 확인

공개 샘플에서는 `filter/count/page` 정합성, deterministic ordering, index-friendly range predicate를 중심으로 재현한다. Oracle 고유 실행계획이나 운영 성능 수치는 공개 재현 또는 권한 있는 재확인 전까지 주장하지 않는다.

사용자 작업: 기존 authorized evidence로 확정할 수 없는 내부 사실이 반드시 필요한 경우에만 요청한다.

### P60. 포트폴리오 유지관리 — `pending`

- [ ] Pages 링크/배포 상태 정기 확인
- [ ] 물리 모바일 최종 UX 검수
- [ ] 회사 GitHub evidence 월말 갱신
- [ ] 완료·미검증 badge와 테스트/증거 동기화

## 현재 사용자에게 필요한 작업

현재 즉시 필요한 사용자 작업은 없다.

OpsMate 검증 종료 후 application workload는 의도대로 `CLOSED`이며 PostgreSQL persistent volume은 보존돼 있다. 현재 active work는 `CS-JAVA-02`의 독립 공개 재구현이다.

## 완료 판정

1. GitHub Pages 포트폴리오: `published`
2. OpsMate 코드/CI: `implemented` + regression `verified`
3. 실제 모델 adapter E2E: `verified`
4. NAS internal deployment/network/security/lifecycle boundary: `verified`
5. public Internet deployment/network/security/lifecycle boundary: `verified` (bounded E2E)
6. 24x7 운영/SLA/장기 부하: `not claimed`
7. 회사 업무 사례: 원본 검토와 독립 공개 샘플 검증 상태를 분리
