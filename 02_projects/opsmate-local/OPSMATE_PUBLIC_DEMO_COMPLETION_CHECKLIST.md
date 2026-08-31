# OpsMate Public Demo Completion Checklist

- 기준일: `2026-08-24`
- 범위: OpsMate Local을 Synology NAS에 안전하게 배포하고, Office GPU의 native Ollama를 restricted SSH tunnel로 사용하며, 외부 HTTPS 공개와 close/reopen 검증까지 완료한다.
- 원칙: 사용자에게 수동 작업을 요청하기 전에 ChatGPT/device-control로 수행 가능한 항목을 모두 완료한다.
- source of truth: `son1004007/ai-agent-workflow-playbook/CONTROL.md` > runtime evidence > GitHub Actions/commit > 이 문서

## 1. Artifact / CI

- [x] Java 21/Spring Boot baseline regression 통과
- [x] container/config 정적 검증 통과
- [x] production tunnel에 `HostKeyAlgorithms=ssh-ed25519` pin
- [x] app/tunnel linux/amd64 image GHCR publish
- [x] immutable digest 재-pull 검증
- [x] Synology에서 동일 image digest 실제 pull

현재 고정 artifact:

- source commit: `e01d0f66c437218e016a70f4513ec44b1ebccf0d`
- app: `ghcr.io/son1004007/opsmate-local@sha256:f560770bb34c10f9685bfddcaa80d6226e010749724d471694eac54dd4b6d41e`
- model tunnel: `ghcr.io/son1004007/opsmate-model-tunnel@sha256:d9ba872b8f897135d0ff349ae80ed63be5e76338d515648f3985cf7ccf353a77`

검증 근거: portfolio publish workflow run `32662503508`.

## 2. NAS -> Office model path

- [x] OpsMate 전용 Ed25519 key를 NAS-local secret으로 생성
- [x] Office `authorized_keys`에 restricted forwarding key 설치
- [x] destination을 `127.0.0.1:11434`로 제한
- [x] exact Office Ed25519 host key를 NAS known_hosts에 고정
- [x] production Alpine tunnel container 실제 실행
- [x] tunnel을 통한 Office Ollama `/api/version` 성공
- [x] tunnel host port 미공개 확인

검증 근거: device-control runtime run `32662804497`, `ollama_version=0.13.5`, `opsmate_nas_container_preflight=PASS`.

## 3. Synology runtime readiness

- [x] NAS `x86_64`
- [x] Docker `24.0.2`
- [x] Docker Compose `2.20.1-6047-g6817716`
- [x] `curl/grep/awk/base64/openssl/git/netstat` 사용 가능
- [x] candidate edge port `18083` free
- [x] candidate public HTTPS/router port `58889` free
- [x] OpsMate running containers `0`
- [x] tunnel key / known_hosts 존재
- [x] persistent PostgreSQL volume은 아직 없음 확인

검증 근거: device-control runtime run `32662986274`, `opsmate_nas_runtime_status=PASS`.

## 4. CLOSED runtime preparation

ChatGPT/device-control 수행 항목:

- [ ] 검증된 deploy assets를 source-SHA-specific NAS release directory로 배치
- [ ] NAS에서 DB 계정별 랜덤 password 생성, 로그/저장소 미노출
- [ ] `.env` mode `600` 생성
- [ ] app/tunnel exact immutable digest 고정
- [ ] tunnel secret path를 기존 NAS-local secret으로 연결
- [ ] persistent PostgreSQL volume 생성
- [ ] Compose config 검증
- [ ] `verify-closed.sh` 통과
- [ ] app/DB/tunnel/edge가 실행되지 않은 CLOSED 상태 확인

이 단계에서는 DSM ingress, router rule, DB initialization, app start를 수행하지 않는다.

## 5. Internal open / pre-public verification

DSM/router 설정 전에 ChatGPT/device-control로 완료한다.

- [ ] model-tunnel 시작 및 Office Ollama health 확인
- [ ] PostgreSQL 시작 및 one-shot migration 성공
- [ ] Spring Boot app 시작 및 internal health 확인
- [ ] Nginx edge를 `127.0.0.1:18083`에만 bind
- [ ] NAS localhost persona smoke 성공
- [ ] DB host port 없음 확인
- [ ] model tunnel host port 없음 확인
- [ ] app direct host port 없음 확인
- [ ] app direct internet egress가 없고 model-tunnel만 outbound 가능함을 runtime 확인
- [ ] Nginx rate limit `429` 검증
- [ ] `/actuator/` external-edge path `404` 검증
- [ ] 공개 로그에 credential/secret이 없는지 확인
- [ ] 다시 CLOSED로 종료

## 6. 사용자 전용 작업 gate

아래 단계는 1~5가 모두 검증된 후에만 사용자에게 요청한다.

예상 사용자 작업:

- [ ] DSM Reverse Proxy source 생성: HTTPS public origin -> `http://127.0.0.1:18083`
- [ ] 필요한 경우 공유기 port forwarding: public `58889` -> NAS `58889`
- [ ] DSM 인증서/hostname 선택

정확한 UI 입력값은 직전 runtime 상태를 다시 확인한 뒤 사용자에게 한 번에 제공한다.

## 7. External HTTPS verification

사용자 ingress 작업 직후 ChatGPT가 수행한다.

- [ ] public HTTPS health/persona smoke
- [ ] 서로 다른 두 세션의 cross-workspace isolation
- [ ] DB 외부 비노출
- [ ] Ollama/model endpoint 외부 비노출
- [ ] rate limit `429`
- [ ] security headers 확인
- [ ] mobile/LTE 외부 접근 검증이 필요하면 사용자에게 최소 확인만 요청

## 8. Lifecycle rehearsal

- [ ] normal close: edge -> app -> model-tunnel -> synthetic purge -> DB
- [ ] emergency close 검증
- [ ] CLOSED verifier 통과
- [ ] 동일 app/tunnel digest로 reopen
- [ ] public HTTPS smoke 재검증
- [ ] 최종 CLOSED 유지

Office native Ollama daemon은 공유 runtime이므로 OpsMate lifecycle에서 종료하지 않는다.

## 9. Evidence / documentation finalization

- [ ] 실제 runtime evidence를 공개 가능한 범위로 요약
- [ ] `PORTFOLIO_COMPLETION_PLAN.md` 최신화
- [ ] README/Pages의 OpsMate 상태를 실제 검증 범위에 맞게 갱신
- [ ] 미검증 항목을 완료로 표시하지 않았는지 최종 검사
- [ ] 최종 CI/Jekyll build 통과

## 현재 사용자 작업

**없음.**

ChatGPT/device-control이 4~5단계를 먼저 완료한다. 사용자만 가능한 DSM/router ingress 단계가 실제 blocker가 되었을 때 이 섹션을 구체적인 UI 입력값으로 갱신한다.
