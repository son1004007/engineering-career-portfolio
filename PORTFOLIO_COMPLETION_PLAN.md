# Portfolio Completion Plan

- 기준일: `2026-08-25`
- 목적: 공개 포트폴리오와 `OpsMate Local`을 실제 검증 증거에 맞춰 동기화하고 남은 **외부 HTTPS ingress** gate를 완료한다.
- 원칙: 문서상 완료가 아니라 exact source, immutable image digest, CI, bounded runtime E2E를 근거로 상태를 올린다.
- 전역 기준: `son1004007/ai-agent-workflow-playbook/CONTROL.md`
- 실행 ledger: [`WORKS.md`](WORKS.md)
- 장기 backlog: [`TASKS.md`](TASKS.md)
- 최신 내부 배포 증거: [`02_projects/opsmate-local/docs/NAS_INTERNAL_E2E_EVIDENCE.md`](02_projects/opsmate-local/docs/NAS_INTERNAL_E2E_EVIDENCE.md)

## 상태

| 상태 | 의미 |
|---|---|
| `pending` | 아직 시작하지 않음 |
| `in-progress` | 현재 수행 중 |
| `blocked-user` | DSM/router UI처럼 사용자 조작이 필요한 입력 대기 |
| `blocked-external` | 외부 서비스/네트워크/runtime 조건 대기 |
| `verified` | 요구된 검증 증거까지 확보 |

## 현재 검증 release

- portfolio source: `f99686981da7efb8802635ae2bde5b0f781433ad`
- application image: `ghcr.io/son1004007/opsmate-local@sha256:61e267c05bf0ce0ea932ae62a3989194bd2a0065532d0ee4caee8b37c8f9d40b`
- model-tunnel image: `ghcr.io/son1004007/opsmate-model-tunnel@sha256:7fc133485a8ba60190e55b5eeb2da8b5eb02c1aa7e70e0cc0ce7b723746db1df`
- model: `gemma3:12b`
- 기본 runtime 상태: `CLOSED`

## 실행 순서

### P00. 상태 문서 동기화 — `in-progress`

- [x] `2026-08-23` 실제 모델 E2E 기록
- [x] `2026-08-25` Synology internal deployment/lifecycle E2E 증거 확보
- [x] GHCR anonymous-pull blocker가 더 이상 현재 blocker가 아님을 runtime evidence로 확인
- [ ] 최신 evidence/state 문서 변경의 portfolio CI와 Pages build 확인

사용자 작업: 없음.

### P10. 최신 repository regression — `verified`

- [x] OpsMate Maven regression
- [x] Spring Security 샘플 regression
- [x] 공개 링크·credential·상태 정합성 검사
- [x] Jekyll build
- [x] shell/Compose/Nginx/container/runbook 검증
- [x] non-root image build 및 migration rehearsal

검증: source `f99686981da7efb8802635ae2bde5b0f781433ad`에 대해 `Verify Portfolio` run `32848946968`의 모든 job 성공.

사용자 작업: 없음.

### P20. OpsMate NAS 배포 준비 — `verified`

목표 구조:

```text
Internet
  -> DSM Reverse Proxy / TLS
  -> 127.0.0.1:18083 OpsMate Nginx edge
  -> Spring Boot
       -> PostgreSQL internal only
       -> model_link internal only
            -> non-root restricted SSH tunnel
                 -> approved native Ollama
```

검증된 항목:

- [x] Synology Docker/x86_64 runtime 확인
- [x] DSM 80/443 기존 점유를 고려해 loopback Nginx edge 구조 확정
- [x] destination-restricted SSH key/host-key pin 경계 준비
- [x] immutable linux/amd64 app/tunnel image 발행 및 pull verification
- [x] exact immutable image를 Synology에서 실제 pull/stage
- [x] NAS-local runtime input 권한 `600`
- [x] PostgreSQL persistent volume 보존
- [x] 이전 승인 release에서 DB credential continuity 유지
- [x] 준비 완료 후 running container `0`, final `CLOSED`

검증: runtime preparation run `32849378114`, 상세는 NAS internal E2E evidence 문서 참조.

사용자 작업: 없음.

### P25. 내부 network/security/lifecycle E2E — `verified`

`2026-08-25` bounded internal verifier에서 다음을 실제 target에서 확인했다.

- [x] stack start
- [x] host-port policy
- [x] Docker network policy
- [x] loopback Nginx edge/security headers 및 `/actuator/**` 차단
- [x] app/edge direct egress 차단
- [x] PostgreSQL/app/model-tunnel host port 미노출
- [x] Secure XSRF/JSESSIONID session 흐름
- [x] 실제 restricted tunnel -> `gemma3:12b` model path
- [x] persona flow와 durable draft 생성
- [x] cross-workspace isolation
- [x] Nginx rate limit `429`
- [x] credential/log scan
- [x] normal close + synthetic workspace purge
- [x] strict CLOSED verifier
- [x] same-digest reopen
- [x] emergency close rehearsal
- [x] final normal close
- [x] final `runtime_policy_flags=YES_YES`
- [x] final `CLOSED`

검증: device-control bounded runtime run `32849533407`; 공개 가능한 요약은 [`NAS_INTERNAL_E2E_EVIDENCE.md`](02_projects/opsmate-local/docs/NAS_INTERNAL_E2E_EVIDENCE.md)에 기록.

사용자 작업: 없음.

### P30. DSM public HTTPS ingress + 외부 gate — `blocked-user`

목표: 공개 app만 인터넷에서 접근 가능하게 하고 DB/model/admin 경계가 외부에 노출되지 않음을 증명한다.

현재 남은 설정:

- [ ] DSM Reverse Proxy/TLS source 생성
- [ ] router에서 선택한 public HTTPS port를 NAS로 forwarding
- [ ] 공개 origin 확정

현재 설계 후보:

```text
public HTTPS: <Synology DDNS hostname>:58889
DSM reverse proxy destination: http://127.0.0.1:18083
```

`58889`는 설정 전까지 검증된 공개 포트가 아니다. DSM source의 인증서는 실제 DDNS hostname과 일치해야 한다.

사용자 설정 후 자동으로 수행할 검증:

- [ ] exact reviewed release OPEN
- [ ] Internet/LTE 외부 HTTPS smoke
- [ ] root/live marker와 security headers
- [ ] 실제 모델 기반 전체 persona flow
- [ ] 외부 두 세션 cross-workspace 격리
- [ ] PostgreSQL 외부 비노출
- [ ] model/Ollama 외부 비노출
- [ ] public `/api/**`/`/actuator/**` 경계
- [ ] public rate limit `429`
- [ ] 공개 로그 credential/민감정보 scan

완료 조건: 외부에서 app만 정상 사용 가능하고 내부 의존성은 노출되지 않으며 rate/session/egress 경계가 검증된다.

### P40. public close/reopen lifecycle — `pending`

내부 lifecycle은 P25에서 이미 `verified`다. 여기서는 public origin까지 포함한 마지막 확인만 수행한다.

- [ ] public OPEN smoke
- [ ] normal close 후 public live marker 부재
- [ ] same digest reopen 후 public smoke
- [ ] emergency close 후 public live marker 부재
- [ ] 마지막 normal close/purge
- [ ] 최종 `CLOSED`

사용자 작업: 없음. 단, DSM/router가 별도 사람 승인을 강제하는 경우 그 승인만 요청한다.

### P50. 두 번째 Java/Spring 사례 공개 — `pending`

- [ ] `03_portfolio/case-study-index.md`에서 우선 후보 선택
- [ ] authorized evidence에서 본인 기여·공개 경계 재확인
- [ ] 합성 도메인으로 독립 구현
- [ ] 정상·실패·경계 테스트
- [ ] 공개 문서·코드 검수 및 Pages 게시

사용자 작업: 기존 authorized evidence로 확정할 수 없는 내부 사실이 반드시 필요한 경우에만 요청한다.

### P60. 포트폴리오 유지관리 — `pending`

- [ ] Pages 링크/배포 상태 정기 확인
- [ ] 물리 모바일 최종 UX 검수
- [ ] 회사 GitHub evidence 월말 갱신
- [ ] 완료·미검증 badge와 테스트/증거 동기화

## 현재 사용자에게 필요한 작업

내부 배포와 lifecycle gate는 모두 통과했다. **현재 사용자 작업은 DSM Reverse Proxy/TLS와 router public ingress 설정뿐이다.**

설정이 완료되면 ChatGPT/device-control이 exact reviewed release를 열고 외부 smoke, DB/model 비노출, rate/session 경계, public close/reopen을 이어서 검증한다.

## 완료 판정

1. GitHub Pages 포트폴리오: `published`
2. OpsMate 코드/CI: `implemented` + latest regression `verified`
3. 실제 모델 adapter E2E: `verified`
4. NAS internal deployment/network/security/lifecycle boundary: `verified`
5. public Internet application: P30/P40 완료 전까지 별도 `pending`
6. 회사 업무 사례: 원본 검토와 독립 공개 샘플 상태를 분리
