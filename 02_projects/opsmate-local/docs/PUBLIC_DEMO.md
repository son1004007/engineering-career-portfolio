# OpsMate Local 공개 데모 설계

## 문서 상태

- 상태: `implemented`, `tested-component`; 실제 모델 adapter E2E 경계 `verified`
- 대상: 채용 담당자가 브라우저에서 직접 실행할 수 있는 제한형 공개 데모
- 실제 모델 증거: `2026-08-23` Ollama `gemma3:12b`, 9/9 성공, 관측 p95 21,076ms(`<= 30,000ms` gate)
- 아직 미검증: NAS -> restricted SSH tunnel -> Office Ollama 실제 연결, public DSM ingress, 외부 DB/model 비노출, close/reopen rehearsal

전체 ERP나 실제 조직 인증 시스템을 재현하는 것이 목적이 아닙니다. **자연어 구매 요청 -> 모델 기반 구조화 초안 -> 사람 승인 -> 발주 -> 감사 기록**의 수직 업무 흐름에서 AI와 업무 트랜잭션의 통제 경계를 보여주는 것이 목적입니다.

## 현재 배포 아키텍처

```mermaid
flowchart LR
    B["External browser"] -->|"HTTPS"| DSM["Synology DSM Reverse Proxy / TLS"]
    DSM -->|"NAS loopback"| EDGE["OpsMate Nginx edge"]
    EDGE --> WEB["Spring MVC + Thymeleaf"]
    WEB --> APP["Application Services"]
    APP --> DB[("Private PostgreSQL + Flyway")]
    APP --> GUARD["single-flight + quota + concurrency 1"]
    GUARD --> LINK["Docker-internal model_link"]
    LINK --> TUNNEL["non-root SSH tunnel"]
    TUNNEL -->|"strict SSH"| OFFICE["Approved Office SSH"]
    OFFICE --> OLLAMA["127.0.0.1:11434 native Ollama"]

    B -. "direct access denied" .-> DB
    B -. "direct access denied" .-> OLLAMA
```

### 왜 DSM + loopback Nginx인가

Synology runtime 실측에서 80/443은 DSM이 이미 사용하고 있습니다. OpsMate가 별도 Caddy로 해당 포트를 점유하면 기존 NAS ingress와 충돌하므로 DSM이 공개 TLS를 유지하고 OpsMate는 loopback high port의 Nginx edge만 사용합니다.

Nginx edge는 다음을 담당합니다.

- NAS host에서는 `127.0.0.1:<high-port>`에만 bind
- `/actuator/**` 차단
- 16KB request body 제한
- security headers와 `X-OpsMate-Demo: live` marker
- 익명 요청 global rate limit과 `429` 응답
- Docker-internal Spring app으로 reverse proxy

DSM source hostname/HTTPS port와 router forwarding은 실제 공개 직전에 현재 상태를 확인해 연결합니다.

### 왜 SSH tunnel인가

Office 실측 결과 native Ollama는 동작하지만 Docker는 설치되어 있지 않습니다. 공개 데모 때문에 Office에 별도 Docker/NVIDIA model-host stack을 추가하지 않습니다.

대신 Synology의 `model-tunnel` 컨테이너가 기존 Office SSH endpoint에 접속해 **Office loopback `127.0.0.1:11434` 한 곳만** 전달합니다.

- Ollama `11434`는 인터넷에 publish하지 않습니다.
- tunnel `11434`도 Synology host port로 publish하지 않습니다.
- app은 `http://model-tunnel:11434`만 allowlist합니다.
- app 자체는 일반 인터넷 egress network에 연결하지 않습니다.
- tunnel만 별도 egress network를 가집니다.
- Office 전용 SSH public key는 `permitopen="127.0.0.1:11434"`에 해당하는 목적지 제한을 적용합니다.
- Office SSH host key는 exact known_hosts로 pin하고 `StrictHostKeyChecking=yes`를 사용합니다.

## 공개 demo 프로필

- Spring MVC + Thymeleaf 동일 출처 UI
- HttpSession 기반 `DemoPrincipal`
- CSRF 검증
- `Secure`, `HttpOnly`, `SameSite=Lax` session cookie
- 서버가 생성한 `workspaceId`와 persona
- 외부 `/api/**` 거부
- 외부 `/actuator/**` 차단
- 브라우저가 actor, workspace 또는 권한을 임의 지정하지 못함

기존 stateless Basic REST API는 로컬/자동화 검증 프로필에만 유지하며 공개 `demo` 프로필에서는 업무 흐름 우회 경로로 사용하지 않습니다.

## 사용자 흐름

한 방문자는 하나의 합성 workspace에서 다음 persona를 순서대로 체험합니다.

1. `REQUESTER`: 자연어 구매 요청 -> 모델 초안/정책 근거 -> 승인 대기 제출
2. `APPROVER`: 현재 workspace의 승인 대기 요청 승인 또는 반려
3. `BUYER`: 승인된 요청 한 건당 발주 한 건 생성
4. `AUDITOR`: 요청·발주·감사 이벤트 타임라인 확인

persona 전환은 데모 체험용일 뿐 실제 조직 인증을 대체하지 않습니다. endpoint RBAC, service RBAC, workspace 범위와 도메인 상태 전이는 서버에서 계속 검증합니다.

## workspace·DB 통제

모든 쓰기와 조회는 `workspace_id` 조건을 포함합니다. 전역 조회 후 애플리케이션에서 필터링하는 방식은 공개 경로에서 사용하지 않습니다. workspace에는 TTL이 있으며 정상 종료와 cleanup은 합성 workspace 관련 데이터를 삭제합니다.

공개 프로필은 PostgreSQL 16 + `ddl-auto=validate`를 사용하며 DB admin, Flyway migration, runtime app 역할을 분리합니다. `migrate`는 **동일 app image**의 one-shot 실행이고 runtime app에는 migration credential이 없습니다.

주요 DB 경계:

- workspace FK 및 cascade cleanup
- request/order idempotency unique constraint
- workspace + status 작업함 index
- workspace + occurred_at audit index
- runtime DDL·Flyway history 접근 제한

## 모델 호출 통제

`DraftGenerationCoordinator`는 다음을 적용합니다.

- `(workspaceId, actor, idempotencyKey)` single-flight
- workspace/global quota
- 전체 모델 동시 실행 수 `1`
- bounded queue/follower wait
- timeout / malformed / oversized / unavailable fail-closed

모델 출력은 제안일 뿐 최종 업무 데이터가 아닙니다. 서버가 구조, 카테고리, 정책 ID와 업무 규칙을 다시 검증한 뒤에만 저장 단계로 이동합니다.

## 공개 배포 network 경계

| network | 연결 서비스 | 외부 egress |
|---|---|---|
| `edge_to_app` | Nginx edge, app | 없음 (`internal`) |
| `app_to_db` | app, migrate, DB | 없음 (`internal`) |
| `model_link` | app, model-tunnel | 없음 (`internal`) |
| `tunnel_egress` | model-tunnel | Office SSH 접속만 필요 |

PostgreSQL, app, model-tunnel에는 host port mapping이 없습니다. Nginx edge 하나만 NAS loopback에 publish됩니다.

## 이미지와 공급망

app과 model-tunnel 이미지는 GitHub Actions에서 linux/amd64 GHCR image로 발행하고 실제 배포에는 full digest를 사용합니다.

```text
OPSMATE_APP_IMAGE=ghcr.io/.../opsmate-local:<sha>@sha256:<digest>
OPSMATE_TUNNEL_IMAGE=ghcr.io/.../opsmate-model-tunnel:<sha>@sha256:<digest>
```

두 runtime image는 non-root이며 read-only root filesystem, dropped capabilities, `no-new-privileges` 경계를 사용합니다.

## 공개 전 gate

- [x] 전체 Maven/Java regression 기록
- [x] PostgreSQL migration rehearsal
- [x] 실제 `gemma3:12b` adapter E2E
- [x] Synology Docker/x86_64 runtime 확인
- [x] Synology 80/443 DSM 점유 확인
- [x] Office native Ollama/model 확인
- [x] Office 사용 승인 확인
- [ ] 최신 app/tunnel immutable GHCR digest 발행 및 NAS pull 검증
- [ ] destination-restricted Office SSH key bootstrap
- [ ] NAS -> model-tunnel -> Office Ollama 실제 health
- [ ] DSM Reverse Proxy/TLS public ingress
- [ ] public Nginx rate-limit 실제 응답 확인
- [ ] public persona smoke
- [ ] 외부 DB/model 비노출
- [ ] normal/emergency close + same-digest reopen

위 외부 gate가 끝나기 전에는 프로젝트 전체 상태를 `tested-component`보다 높이지 않습니다.

## 공개 smoke 기준

실제 HTTPS에서 다음을 검증합니다.

- root/live marker
- standard 443 배포라면 HTTP -> HTTPS redirect
- 공개 `/api/**` 거부와 Basic challenge 부재
- cookie 보안 속성
- 합성 workspace 생성
- 실제 모델 기반 draft
- submit -> approve -> order
- AUDITOR audit event
- workspace cleanup

추가로 서로 다른 두 외부 세션의 cross-workspace 격리, 외부 PostgreSQL/model 포트 차단과 LTE/5G UX를 확인합니다.

## lifecycle

### normal close

`deploy/close-demo.sh`는 Nginx edge -> app -> model-tunnel 순으로 중단하고 합성 workspace를 삭제한 뒤 DB를 닫습니다. Office native Ollama는 공유 runtime이므로 중단하지 않습니다.

### emergency close

`deploy/emergency-close.sh`는 env/credential 없이 Compose label을 사용해 해당 OpsMate project의 edge, app, model-tunnel, migrate, DB만 중단합니다. 다른 NAS workload나 Office Ollama를 중단하지 않습니다.

### reopen

같은 app/tunnel image digest로 다시 열고 tunnel health, migration/readiness, public smoke를 재통과해야 same-artifact reopen으로 인정합니다. rehearsal 종료 후 최종 상태는 다시 `CLOSED`로 둡니다.

세부 명령과 장애 대응은 [`SERVICE_RUNBOOK.md`](SERVICE_RUNBOOK.md)를 따릅니다.

## 공개 증거 원칙

공개 가능한 것은 설계, 모델명, 소스 commit, image digest, 검증 시각/결과와 알려진 제한입니다. 실제 host/IP/user, SSH key, known_hosts 원문, 조직 승인 문서 원문과 runtime credential은 공개하지 않습니다.
