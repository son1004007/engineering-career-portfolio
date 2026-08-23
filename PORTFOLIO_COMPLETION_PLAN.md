# Portfolio Completion Plan

- 기준일: `2026-08-24`
- 목적: 현재 공개 포트폴리오와 `OpsMate Local`을 실제 검증 증거에 맞춰 동기화하고 남은 외부 배포·운영 gate를 순서대로 완료한다.
- 원칙: 문서상 완료가 아니라 최근 commit, test, workflow, bounded runtime E2E를 근거로 상태를 올린다.
- 전역 기준: `son1004007/ai-agent-workflow-playbook/CONTROL.md`
- 실행 ledger: [`WORKS.md`](WORKS.md)
- 장기 backlog: [`TASKS.md`](TASKS.md)

## 상태

| 상태 | 의미 |
|---|---|
| `pending` | 아직 시작하지 않음 |
| `in-progress` | 현재 수행 중 |
| `blocked-user` | 계정 승인·물리 기기 확인 등 사용자만 가능한 입력 대기 |
| `blocked-external` | 외부 서비스/네트워크/runtime 조건 대기 |
| `verified` | 요구된 검증 증거까지 확보 |

## 실행 순서

### P00. 상태 문서 동기화 — `verified`

- [x] `2026-08-23` 실제 모델 E2E를 공개 상태 문서에 동기화
- [x] README/AGENTS/evidence/strategy/task 문서 정합성 확인

검증: PR `#10`의 `Verify Portfolio` run `32638023909`에서 public portfolio 정합성 검사와 Jekyll build가 성공했다.

사용자 작업: 없음.

### P10. 최신 baseline regression — `verified`

- [x] OpsMate `clean verify`
- [x] Spring Security 샘플 `clean verify`
- [x] 저장소 공개 링크·credential·상태 정합성 검사
- [x] Jekyll/Pages build 검증
- [x] container/config 정적 검수
- [x] Docker non-root image build와 one-shot migration rehearsal

검증: PR `#10`, `Verify Portfolio` run `32638023909`의 모든 job 성공. P20 tunnel host-key pin 변경은 PR `#12`, run `32659673514`에서 전체 portfolio regression을 다시 통과했다.

사용자 작업: 없음.

### P20. OpsMate public application 배포 준비 — `blocked-user`

목표: 실제 인터넷에서 접근 가능한 애플리케이션 URL을 만들되 DB와 모델 endpoint는 공개하지 않는다.

#### 확정한 runtime/architecture

- [x] app host: 개인 Synology NAS
- [x] model runtime: 승인된 Office GPU 서버의 native Ollama
- [x] Office 공개 포트폴리오 추론 사용 승인 확인
- [x] Office Ollama `0.13.5`, `gemma3:12b`, API reachability 확인
- [x] Office Docker 미설치 확인; Docker 설치를 전제조건에서 제거
- [x] Synology Docker `24.0.2`, `x86_64` 확인
- [x] Synology 80/443은 DSM이 사용 중임을 runtime에서 확인
- [x] 공개 TLS 구조를 `DSM Reverse Proxy/TLS -> NAS loopback Nginx edge`로 확정
- [x] app의 직접 egress를 제거하고 `model-tunnel`만 outbound network를 갖도록 Compose 분리
- [x] PostgreSQL, app, model-tunnel은 host port를 publish하지 않는 구조로 변경
- [x] 과거 Office Docker model-host/Caddy 자산 제거
- [x] production `model-tunnel`을 exact Office ED25519 host key algorithm으로 pin하고 최신 CI regression 성공

현재 target 구조:

```text
Internet
  -> DSM Reverse Proxy / TLS
  -> 127.0.0.1:18083 OpsMate Nginx edge
  -> Spring Boot
       -> PostgreSQL internal only
       -> model_link internal only
            -> non-root SSH tunnel
                 -> Office SSH
                 -> 127.0.0.1:11434 native Ollama
```

`58889`는 public HTTPS source/router 후보 포트이며 실제 DSM/router 설정 완료 전 검증된 public port가 아니다.

#### P20 실행 상태

- [x] 최신 DSM/Nginx/tunnel 구조 전체 CI 성공: PR `#12`, `Verify Portfolio` run `32659673514`
- [x] OpsMate 전용 SSH key를 Synology에 생성하고 private key를 NAS-local secret으로만 보관
- [x] Office `authorized_keys`에 `restrict`, `port-forwarding`, `permitopen="127.0.0.1:11434"`, `command="/bin/false"` 제한 key 등록 및 확인
- [x] exact Office ED25519 host key를 NAS-local known_hosts로 배치
- [ ] 실제 production Alpine `model-tunnel` container -> Office `/api/version` E2E 성공
- [ ] app/tunnel linux/amd64 immutable GHCR image full digest를 NAS에서 실제 확인
- [ ] 두 GHCR container package의 anonymous pull 허용
- [ ] Synology에서 두 GHCR full digest 실제 pull 성공
- [ ] NAS-local `deploy/.env`/DB secret 준비
- [ ] 실제 공개 전 OpsMate stack `CLOSED` 상태 확인
- [ ] DSM Reverse Proxy/TLS source와 router ingress 구성

#### 최신 runtime evidence

- `device-control`의 dedicated tunnel bootstrap에서 NAS key와 known_hosts, Office restricted authorized key가 실제 target에 설치·검증됐다.
- portfolio PR `#12`에서 production tunnel에 `HostKeyAlgorithms=ssh-ed25519` pin을 적용했고 run `32659673514`가 성공했다.
- `device-control`에 exact portfolio source SHA만 받는 bounded NAS container preflight를 추가했고 CI를 통과했다.
- NAS preflight run `32660575997`에서 비대화형 SSH PATH 문제를 확인한 뒤 기존 Synology runtime의 Container Manager Docker 경로를 재사용하도록 수정했다.
- 재실행 run `32660707257`은 Docker daemon까지 정상 진입하고 `e2e_stage=image-pull`에 도달했지만 `ghcr.io/son1004007/opsmate-local:<source-sha>` anonymous pull이 `denied`되어 container 실행 전에 중단됐다.
- 따라서 현재 blocker는 NAS Docker나 Office tunnel credential이 아니라 GHCR package access/visibility gate다.

보안 원칙:

- Office Ollama `11434`를 인터넷에 공개하지 않는다.
- OpsMate tunnel `11434`도 NAS host에 publish하지 않는다.
- 전용 SSH key는 shell/agent/X11/임의 forwarding 용도로 사용하지 않고 Ollama loopback destination에만 제한한다.
- app은 `http://model-tunnel:11434`만 allowlist한다.
- 실제 SSH key, known_hosts 원문, DB password와 host credential은 공개 저장소에 넣지 않는다.
- NAS에 장기 GHCR PAT를 저장하지 않는 방향을 우선한다. 공개 포트폴리오 소스에서 재현 가능한 container image만 anonymous pull 대상으로 사용한다.

현재 사용자 작업: GitHub Packages에서 `opsmate-local`과 `opsmate-model-tunnel` 두 Container package의 visibility를 확인하고 **Public**으로 변경한다. GitHub Container Registry의 public package는 anonymous pull이 가능하며, GitHub 정책상 public으로 바꾼 package는 다시 private으로 되돌릴 수 없으므로 이 변경은 사용자 UI에서 명시적으로 수행한다. 완료 후 같은 exact source SHA preflight를 즉시 재실행한다.

완료 조건: NAS↔Office restricted production-container model connection, immutable image digests, NAS-local runtime input과 public ingress 직전 closed preflight가 실제 target에서 성공한다.

### P30. 외부 네트워크·보안 gate — `pending`

목표: 공개 app만 외부에서 접근 가능하고 DB/model/admin 경계는 닫혀 있음을 증명한다.

- [ ] DSM HTTPS 인증서/public origin 확인
- [ ] 외부 전체 persona smoke
- [ ] 서로 다른 두 세션 cross-workspace 격리
- [ ] DB 외부 비노출 확인
- [ ] model endpoint 외부 비노출 확인
- [ ] app direct egress 부재/tunnel-only model path runtime 확인
- [ ] Nginx 익명 요청 rate limit `429` 확인
- [ ] 공개 로그에 credential/민감 endpoint가 남지 않는지 확인

사용자 작업: DSM/router UI나 물리 외부망 확인처럼 연결된 도구로 수행할 수 없는 단계만 요청한다.

완료 조건: 외부에서 app은 정상 사용 가능하고 내부 의존성은 노출되지 않으며 rate/egress 통제가 검증된다.

### P40. close/reopen lifecycle rehearsal — `pending`

목표: 공개 데모를 안전하게 닫고 동일 artifact로 다시 열 수 있음을 실제로 검증한다.

- [ ] normal close: edge -> app -> model-tunnel -> synthetic purge -> DB
- [ ] environment-independent emergency close
- [ ] closed verifier: Compose state + loopback/public live marker 부재
- [ ] same app/tunnel image digest reopen
- [ ] public smoke 재검증
- [ ] 최종 상태를 `CLOSED`로 유지

Office native Ollama 자체는 공유 runtime이므로 OpsMate lifecycle이 임의 종료하지 않는다. OpsMate 모델 접근 경계는 restricted SSH tunnel 수명주기로 통제한다.

사용자 작업: 없음. 단, target이 사람 승인을 강제하는 보안 정책을 사용하면 그 승인만 요청한다.

완료 조건: 닫기·비상 종료·동일 digest 재개가 실제 target에서 성공하고 최종적으로 닫힌 상태가 확인된다.

### P50. 두 번째 Java/Spring 사례 공개 — `pending`

- [ ] `03_portfolio/case-study-index.md`에서 우선 후보 선택
- [ ] authorized evidence에서 본인 기여·공개 경계 재확인
- [ ] 합성 도메인으로 독립 구현
- [ ] 정상·실패·경계 테스트
- [ ] 공개 문서와 코드 검수
- [ ] Pages 게시

사용자 작업: 회사 내부 사실 중 저장소/기존 authorized evidence로 확정할 수 없는 항목이 실제로 필요한 경우에만 사실 확인을 요청한다.

### P60. 포트폴리오 유지관리 — `pending`

- [ ] Pages 링크와 배포 상태 정기 확인
- [ ] 물리 모바일 화면 최종 검수
- [ ] 회사 GitHub evidence 월말 갱신
- [ ] 완료·미검증 badge와 테스트 수 동기화

## 현재 사용자에게 필요한 작업

현재 즉시 필요한 사용자 작업은 **GitHub Container Registry package 2개의 visibility를 Public으로 변경하는 것**이다.

대상:

- `opsmate-local`
- `opsmate-model-tunnel`

이 단계가 완료되면 ChatGPT/device-control이 동일 source SHA로 anonymous pull, RepoDigest 추출, production tunnel container E2E를 다시 수행한다. 이후 DSM Reverse Proxy/router 설정이 실제 blocker가 될 때까지 NAS runtime 준비는 자동화 경로로 계속 진행한다.

## 완료 판정

전체 포트폴리오를 단순히 `완료`라고 부르지 않고 다음을 분리한다.

1. GitHub Pages 포트폴리오: `published`
2. OpsMate 코드/컴포넌트: `implemented` + 최신 CI regression 성공
3. 실제 모델 adapter E2E: `verified` 범위 명시
4. public application/network/lifecycle: 해당 gate 완료 전까지 `pending`/`tested-component`
5. 회사 업무 사례: 원본 검토와 독립 공개 샘플 검증 상태를 분리
