# OpsMate Local 공개 데모 운영 Runbook

## 문서 상태

- 운영 자산: `implemented`, Synology + restricted SSH tunnel 구조로 전환 중
- 실제 모델 adapter E2E: `verified` (`2026-08-23`, `gemma3:12b`, 9/9, 관측 p95 21,076ms <= 30,000ms)
- public ingress / NAS→Office tunnel E2E / 실제 close-reopen: 아직 `unverified`
- 기본 운영 상태: `CLOSED`

이 문서는 **개인 Synology NAS의 공개 앱**과 **승인된 Office GPU 서버의 native Ollama**를 연결하는 현재 운영 경계를 정의합니다. 과거 Office Docker/NVIDIA model-host 설계는 현재 Office runtime에 Docker가 없다는 실측 결과와 맞지 않으므로 사용하지 않습니다.

공개 문서에는 credential, 실제 host/IP, SSH 계정, known_hosts 원문과 승인 문서 원문을 기록하지 않습니다.

## 현재 구조

```mermaid
flowchart LR
    USER["External browser"] -->|"HTTPS"| DSM["Synology DSM Reverse Proxy / TLS"]
    DSM -->|"loopback"| EDGE["Nginx edge"]
    EDGE --> APP["OpsMate app"]
    APP --> DB["PostgreSQL 16"]
    APP -->|"model_link"| TUNNEL["non-root model-tunnel"]
    TUNNEL -->|"strict SSH only"| OFFICE["Office SSH"]
    OFFICE -->|"permitopen -> loopback"| OLLAMA["127.0.0.1:11434 native Ollama"]
```

## 보안 경계

- DSM이 public TLS와 external ingress를 담당합니다.
- OpsMate Nginx edge는 NAS loopback high port 한 곳에만 bind합니다.
- PostgreSQL, app, model-tunnel은 NAS host port를 publish하지 않습니다.
- app은 일반 인터넷 egress network에 연결하지 않습니다.
- `model-tunnel`만 Office SSH를 위한 outbound network를 가집니다.
- tunnel은 `StrictHostKeyChecking=yes`, exact known_hosts, public-key-only 인증을 사용합니다.
- OpsMate 전용 Office public key는 Ollama loopback 한 곳으로의 local forwarding만 허용합니다.
- Ollama `11434`는 Office public interface에 새로 노출하지 않습니다.
- OpsMate close는 model-tunnel만 닫으며 공유 native Ollama daemon 자체를 임의로 종료하지 않습니다.

## 현재 runtime evidence

`2026-08-23` read-only probe 기준:

- Synology: Docker `24.0.2`, `x86_64`.
- Synology: 80/443은 DSM이 이미 LISTEN 중이므로 OpsMate가 직접 점유하지 않습니다.
- Synology: loopback `18083`은 OpsMate edge 후보이며 실제 open 직전 다시 충돌을 검사합니다.
- Office: Ollama `0.13.5`, `gemma3:12b` 존재, API 응답 확인.
- Office: Docker 없음.
- 실제 모델 adapter E2E는 통과했지만 NAS -> SSH tunnel -> Office Ollama -> NAS app 전체 경로는 별도 검증합니다.

## 배포 입력값

실제 값은 NAS-local `deploy/.env`와 NAS-local secret files에만 둡니다.

필수 범주:

- `OPSMATE_APP_IMAGE`: 검증된 app image full digest
- `OPSMATE_TUNNEL_IMAGE`: 검증된 tunnel image full digest
- `DEMO_DOMAIN`, `DEMO_PUBLIC_PORT`, `OPSMATE_EDGE_HOST_PORT`
- DB admin / migration / runtime 역할과 서로 다른 비밀번호
- Office SSH endpoint metadata
- OpsMate 전용 tunnel private key 파일
- exact Office SSH known_hosts 파일
- `OPSMATE_LLM_BASE_URL=http://model-tunnel:11434`
- `OPSMATE_LLM_ALLOWED_HOSTS=model-tunnel`
- `OPSMATE_LLM_MODEL=gemma3:12b`

SSH-forwarded loopback 경로에서는 별도 model API Bearer token을 사용하지 않습니다. 인증과 목적지 제한은 SSH key + host-key 검증 + Office `authorized_keys` 제한으로 수행합니다.

`.env`, private key와 real known_hosts는 Git, Issue, PR, workflow log에 저장하지 않습니다.

## 이미지 발행

`Publish OpsMate Images` workflow는 관련 `main` 소스가 바뀌면 linux/amd64 이미지를 GHCR에 발행하도록 구성합니다.

- `ghcr.io/.../opsmate-local:<source-sha>`
- `ghcr.io/.../opsmate-model-tunnel:<source-sha>`

실제 배포에는 tag만 사용하지 않고 workflow가 기록한 `@sha256:<digest>` full reference를 사용합니다. NAS에서 해당 full digest가 실제 pull되는 것까지 확인해야 release artifact gate가 완료됩니다.

## 공개 전 필수 gate

### Office model path

- [x] 공개 포트폴리오 추론 용도에 대한 조직 승인 확인
- [x] native Ollama와 `gemma3:12b` runtime 확인
- [x] 실제 모델 adapter E2E 성공
- [ ] OpsMate 전용 SSH key 생성 및 NAS-local secret 배치
- [ ] Office `authorized_keys`에 destination-restricted key 등록
- [ ] exact Office host key를 NAS known_hosts로 배치
- [ ] NAS `model-tunnel` health가 Office loopback Ollama까지 성공
- [ ] Office `11434` 외부 비노출 확인

### Synology app path

- [x] Docker와 x86_64 runtime 확인
- [x] DSM 80/443 점유 확인
- [ ] app/tunnel GHCR image full digest 확보 및 NAS pull
- [ ] DSM Reverse Proxy/TLS source 설정
- [ ] DB 역할 분리와 NAS-local secret 구성
- [ ] loopback edge와 기존 NAS workload port 충돌 없음 확인
- [ ] Compose app direct-egress 부재 검증
- [ ] Nginx rate limit 실제 동작 검증
- [ ] 공개 전 closed 상태 확인

`OPSMATE_HOST_EGRESS_POLICY_VERIFIED=YES`와 `OPSMATE_EDGE_RATE_LIMIT_VERIFIED=YES`는 실제 검증 증거가 있을 때만 설정합니다. 단순 flag 변경은 검증을 대신하지 않습니다.

## DSM Reverse Proxy/TLS

OpsMate Compose의 `edge`는 `127.0.0.1:<OPSMATE_EDGE_HOST_PORT>`에만 bind합니다. DSM Reverse Proxy source는 공개 HTTPS hostname/port를 받고 destination은 해당 loopback HTTP endpoint로 설정합니다.

현재 예시 후보는 다음과 같습니다.

```text
public HTTPS: <DEMO_DOMAIN>:58889
DSM destination: http://127.0.0.1:18083
```

`58889`는 현재 후보일 뿐 실제 DSM/router 설정 완료 전 검증된 public port로 간주하지 않습니다. source port, 인증서와 공유기 forwarding은 공개 직전 현재 UI/runtime을 확인해 설정합니다.

## 서비스 열기

DSM ingress와 NAS-local secret이 준비된 뒤 NAS에서 실행합니다.

```sh
./deploy/open-demo.sh
```

`open-demo.sh`의 순서:

1. app/tunnel image가 full digest인지 확인합니다.
2. DB 역할 분리와 비밀번호 기준을 확인합니다.
3. Office SSH target 형식과 NAS-local key/known_hosts 파일 존재를 확인합니다.
4. model URL이 정확히 `http://model-tunnel:11434`인지 확인합니다.
5. tunnel-only egress와 edge rate-limit 검증 flag를 확인합니다.
6. loopback edge port가 현재 비어 있는지 확인합니다.
7. immutable app/tunnel/edge image를 pull/inspect합니다.
8. `model-tunnel`을 시작하고 Ollama `/api/version` health를 기다립니다.
9. PostgreSQL을 시작합니다.
10. 같은 app image의 one-shot Flyway migration을 실행합니다.
11. migration credential이 없는 runtime app을 시작합니다.
12. loopback Nginx edge를 시작합니다.
13. 실제 public HTTPS smoke를 수행합니다.

중간 실패 시 edge -> app -> model-tunnel을 닫고 쓰기 주체가 중단된 경우에만 합성 workspace 삭제를 시도한 뒤 DB를 닫습니다.

## 공개 smoke 기준

`deploy/smoke-test.sh`는 실제 HTTPS origin에서 다음을 확인합니다.

- root 응답과 `X-OpsMate-Demo: live` marker
- standard 443 배포라면 HTTP -> HTTPS redirect
- 공개 `/api/**`의 `403`과 Basic challenge 부재
- XSRF/JSESSIONID의 `Secure`, `HttpOnly`, `SameSite=Lax`
- 합성 workspace 시작
- 실제 모델 기반 서버 검증 초안 생성
- submit -> approve -> order
- AUDITOR `ORDER_CREATED`
- smoke workspace 삭제

이 smoke가 성공하지 않으면 공개 open은 완료된 것이 아닙니다. 서로 다른 두 외부 세션의 cross-workspace 격리, 외부 DB/model port 차단과 모바일 네트워크 확인은 별도 운영 검수로 추가합니다.

## 정상 닫기

NAS에서 실행합니다.

```sh
./deploy/close-demo.sh
```

순서:

1. loopback Nginx edge 중단
2. app graceful stop
3. model-tunnel 중단
4. 세 서비스가 실제 중단됐는지 확인
5. PostgreSQL을 유지/기동해 `demo_workspaces`를 `TRUNCATE ... CASCADE`
6. 남은 workspace `0` 확인
7. migrate/DB 중단
8. `verify-closed.sh`로 edge/app/tunnel/DB 중단 + PostgreSQL volume 보존 확인
9. public origin에서 live marker가 사라졌는지 확인

Office native Ollama는 다른 개발 업무와 공유될 수 있으므로 정상 close가 Ollama daemon을 종료하지 않습니다.

## 긴급 닫기

환경 파일이나 credential을 읽을 수 없어도 다음을 실행할 수 있습니다.

```sh
./deploy/emergency-close.sh opsmate-demo
```

Compose label을 기준으로 **해당 OpsMate project의** edge -> app -> model-tunnel -> migrate -> DB만 중단합니다. 다른 NAS container나 Office Ollama를 건드리지 않습니다.

긴급 닫기는 DB credential이 없으므로 합성 데이터 삭제를 수행하지 않습니다. 환경을 복구한 뒤 정상 close를 수행해 purge까지 확인해야 reopen할 수 있습니다.

## 동일 artifact reopen

1. Office 사용 승인이 여전히 유효한지 확인합니다.
2. Office native Ollama/model이 이전 검증 경계와 호환되는지 확인합니다.
3. 이전과 동일한 `OPSMATE_APP_IMAGE@sha256`과 `OPSMATE_TUNNEL_IMAGE@sha256`를 사용합니다.
4. emergency close 이력이 있다면 정상 close로 합성 데이터 삭제를 먼저 확인합니다.
5. `open-demo.sh` 전체 preflight/tunnel health/migration/readiness/HTTPS smoke를 다시 통과합니다.
6. 외부 DB/model 비노출을 재확인합니다.
7. rehearsal 종료 후 최종 상태는 다시 `CLOSED`로 둡니다.

소스, image digest, model ID, migration 또는 주요 runtime이 바뀌면 same-artifact reopen이 아니므로 새 release 검증이 필요합니다.

## 장애 대응

### 모델 또는 SSH tunnel 장애

- draft 생성은 fail-closed로 유지합니다.
- 유료 API나 다른 모델로 자동 fallback하지 않습니다.
- edge/app/tunnel을 닫고 Office Ollama와 SSH 경계를 별도로 진단합니다.
- 복구 뒤 tunnel health + 실제 모델 경로 + public smoke 전에는 reopen하지 않습니다.

### workspace 간 데이터 노출

- 즉시 emergency close합니다.
- 모든 합성 workspace를 정리합니다.
- repository query/service guard와 cross-workspace 회귀 테스트를 추가합니다.
- 전체 regression 전에는 reopen하지 않습니다.

### SSH key 또는 credential 노출

- edge와 tunnel을 즉시 닫습니다.
- Office restricted key를 `authorized_keys`에서 회수하고 새 key를 발급합니다.
- NAS-local secret을 교체합니다.
- Git history/image layer/log 노출 범위를 확인합니다.

## 검증 기록 양식

공개 기록에는 다음만 남깁니다.

```text
검증 시각/timezone:
source commit:
application image digest:
tunnel image digest:
DB migration version:
공개 가능한 모델 식별자:
clean verify: PASS / FAIL / NOT RUN
NAS -> SSH tunnel -> Ollama health: PASS / FAIL / NOT RUN
public HTTPS smoke: PASS / FAIL / NOT RUN
cross-workspace isolation: PASS / FAIL / NOT RUN
external DB/model non-exposure: PASS / FAIL / NOT RUN
edge rate limit: PASS / FAIL / NOT RUN
normal close: PASS / FAIL / NOT RUN
emergency close: PASS / FAIL / NOT RUN
same-digest reopen: PASS / FAIL / NOT RUN
synthetic workspace purge: PASS / FAIL / NOT RUN
known limitations:
```

실제 host/IP/user, SSH key, known_hosts 원문과 조직 승인 문서 원문은 공개 기록에서 제외합니다.

## 완료 기준

다음 항목이 모두 검증돼야 OpsMate public deployment를 완료로 표시합니다.

- 최신 Maven regression과 container/config CI 성공
- immutable app/tunnel image digest 발행 및 NAS pull 성공
- destination-restricted SSH tunnel의 실제 NAS -> Office E2E 성공
- public HTTPS 전체 persona smoke 성공
- PostgreSQL과 Ollama 외부 비노출 확인
- edge rate-limit과 tunnel-only model egress 확인
- normal/emergency close 성공
- 동일 app/tunnel digest reopen 성공
- close 후 합성 workspace 삭제와 최종 `CLOSED` 확인
- README/evidence/state 문서가 실제 결과와 일치
