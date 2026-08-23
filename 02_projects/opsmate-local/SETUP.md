# OpsMate Local Setup

## 요구사항

- JDK 21
- 기본 통합 테스트: Docker daemon과 Testcontainers
- 실제 모델 E2E: 승인된 Ollama 호환 `/api/chat` endpoint
- 공개 배포: Synology Container Manager/Docker Compose v2, DSM Reverse Proxy/TLS, Office native Ollama

Maven Wrapper가 포함되어 있어 전역 Maven 설치는 필요하지 않습니다. 최초 실행에는 Maven 배포본과 의존성을 내려받을 네트워크 연결이 필요합니다.

## 전체 자동화 검증

프로젝트 디렉터리에서 실행합니다.

```powershell
.\mvnw.cmd -q clean verify
```

`2026-08-04` 전체 실행에서는 54개 테스트가 성공했고 실패·오류·건너뜀은 0개였습니다. 검증 범위에는 다음이 포함됩니다.

- 초안 → 제출 → 승인/반려 → 발주 → 감사 이벤트
- 역할, 자기 승인 차단, 객체 소유권·상태·workspace 격리
- 웹 CSRF, session, persona 전환과 공개 `/api/**` 거부
- workspace TTL·수용량·시작률 제한
- 모델 장애·malformed/unknown/oversized 응답과 출력 제한
- 동일 key single-flight, follower·queue·workspace·전체 호출량 제한
- 멱등성, optimistic lock, DB constraint와 트랜잭션 롤백
- PostgreSQL Flyway migration, runtime 최소 권한과 cascade
- Javadoc/doclint

## 모델 없이 로컬 실행

모델을 활성화하지 않는 것이 안전한 기본값입니다. 역할별 비밀번호는 현재 프로세스의 환경변수에만 둡니다. 모델이 비활성 상태이면 서버는 기동되지만 모델 기반 초안 생성은 저장 전에 `fail-closed`로 중단됩니다.

## 로컬 오픈웨이트 모델 연결

```powershell
$env:OPSMATE_LLM_ENABLED = "true"
$env:OPSMATE_LLM_BASE_URL = "http://127.0.0.1:11434"
$env:OPSMATE_LLM_ALLOWED_HOSTS = "127.0.0.1,localhost"
$env:OPSMATE_LLM_MODEL = "<installed-open-weight-model:tag>"
$env:OPSMATE_LLM_MAX_OUTPUT_TOKENS = "512"
$env:OPSMATE_LLM_MAX_RESPONSE_BYTES = "65536"
.\mvnw.cmd spring-boot:run
```

사용자 요청으로 base URL, 경로나 provider를 바꿀 수 없고 adapter의 호출 경로는 `/api/chat`으로 고정됩니다.

## 승인된 실제 모델 E2E

기본 `clean verify`는 실제 모델을 호출하지 않습니다. 승인된 endpoint에서만 다음 gate를 명시적으로 실행합니다.

```powershell
$env:OPSMATE_REAL_MODEL_E2E = "YES"
$env:OPSMATE_LLM_BASE_URL = "<approved-model-base-url>"
$env:OPSMATE_LLM_ALLOWED_HOSTS = "<approved-model-host>"
$env:OPSMATE_LLM_MODEL = "<approved-model:tag>"
$env:OPSMATE_REAL_MODEL_P95_MAX_MS = "30000"
.\mvnw.cmd -q -Preal-model-e2e verify
```

### 2026-08-23 검증 기록

승인된 GPU 호스트의 native Ollama `0.13.5`와 `gemma3:12b`를 사용해 source commit `ff67df0990cbed3a41cf5051a5e2701a7b2a7b50`의 `RealOpenWeightModelE2EIT`를 실행했습니다.

- 9개 합성 요청: 9/9 성공
- 실제 `/api/chat` 구조화 출력과 서버 측 category·policy ID 검증 통과
- 요청·감사 이벤트 저장 건수 각각 9건
- 관측 p95: 21,076ms
- gate: p95 `<= 30,000ms`
- Maven exit code: 0
- 모델: `gemma3:12b`

상세 경계는 [`docs/REAL_MODEL_E2E_EVIDENCE.md`](docs/REAL_MODEL_E2E_EVIDENCE.md)에 기록합니다.

## one-shot PostgreSQL migration

공개 배포에서는 장기 실행 앱이 Flyway credential을 갖지 않습니다. 같은 jar/image를 migration 전용 명령으로 한 번 실행합니다.

```text
java -jar opsmate-local.jar --opsmate-migrate-only
```

실제 값은 `OPSMATE_DB_URL`, `OPSMATE_FLYWAY_USERNAME`, `OPSMATE_FLYWAY_PASSWORD`, `OPSMATE_FLYWAY_APP_ROLE`로 주입합니다. 운영 비밀값을 명령행, 문서, Git 또는 CI log에 넣지 않습니다.

## 공개 배포 아키텍처

```text
Internet
  -> Synology DSM Reverse Proxy / TLS
  -> 127.0.0.1:<OPSMATE_EDGE_HOST_PORT>
  -> OpsMate Nginx edge
  -> Spring Boot
       -> PostgreSQL internal network
       -> model_link internal network
            -> non-root SSH tunnel
                 -> Office SSH
                 -> 127.0.0.1:11434 native Ollama
```

NAS 실측에서 80/443은 DSM이 이미 사용하고 있으므로 OpsMate Compose가 이 포트를 직접 bind하지 않습니다. 예시 설정은 loopback edge `18083`과 별도 public HTTPS 후보 포트 `58889`를 사용하지만 실제 공개 ingress를 구성하기 전 다시 충돌을 확인합니다.

## 공개 배포 입력 준비

`deploy/.env.example`을 NAS의 비추적 `deploy/.env`로 복사하고 실제 값으로 채웁니다. 실제 secret 파일과 `.env`는 Git에 커밋하지 않습니다.

필수 범주:

- 검증된 `OPSMATE_APP_IMAGE` full SHA-256 digest
- 검증된 `OPSMATE_TUNNEL_IMAGE` full SHA-256 digest
- public hostname/HTTPS port와 loopback edge port
- 서로 다른 PostgreSQL admin·migration·runtime 역할과 긴 비밀번호
- Office SSH endpoint metadata
- OpsMate 전용 Office SSH private key의 NAS-local 파일
- exact Office SSH host key를 담은 NAS-local known_hosts 파일
- `OPSMATE_LLM_BASE_URL=http://model-tunnel:11434`
- `OPSMATE_LLM_ALLOWED_HOSTS=model-tunnel`
- `OPSMATE_LLM_MODEL=gemma3:12b`

전용 Office public key는 `authorized_keys`에서 Ollama loopback 한 곳만 local forwarding하도록 제한해야 합니다. 앱 자체에는 일반 인터넷 egress network가 없고 tunnel 컨테이너만 Office SSH로 나갑니다.

## 이미지 발행

`Publish OpsMate Images` GitHub Actions workflow는 관련 `main` 소스가 변경되면 linux/amd64 app/tunnel 이미지를 GHCR에 발행하고 full digest를 기록합니다.

실제 NAS 배포에는 mutable tag가 아니라 다음 형태의 digest reference를 사용합니다.

```text
ghcr.io/.../opsmate-local:<source-sha>@sha256:<digest>
ghcr.io/.../opsmate-model-tunnel:<source-sha>@sha256:<digest>
```

NAS에서 실제 pull 가능 여부까지 확인해야 이미지 발행 gate가 완료됩니다.

## DSM Reverse Proxy/TLS

OpsMate Compose의 `edge` 서비스는 NAS loopback high port에만 bind합니다. DSM Reverse Proxy가 공개 HTTPS origin을 해당 loopback endpoint로 전달합니다.

실제 source hostname/port, 인증서와 공유기 port forwarding은 배포 직전 현재 DSM/router 상태를 확인한 뒤 구성합니다. 소스 저장소에서 DSM 내부 설정을 추측하거나 80/443을 직접 점유하지 않습니다.

## 공개 서비스 열기와 닫기

DSM ingress와 NAS-local secret이 준비된 뒤 NAS에서 실행합니다.

```sh
./deploy/open-demo.sh
```

순서는 다음과 같습니다.

1. immutable image/DB/SSH/network gate 확인
2. restricted `model-tunnel` 시작 및 `/api/version` health
3. PostgreSQL 시작
4. one-shot Flyway migration
5. runtime app 시작
6. loopback Nginx edge 시작
7. 실제 public HTTPS smoke

정상 종료:

```sh
./deploy/close-demo.sh
```

edge -> app -> model-tunnel을 먼저 닫고 합성 workspace를 삭제한 뒤 DB를 중단합니다. Office native Ollama 자체는 공유 개발 runtime이므로 OpsMate 종료 과정에서 임의로 중단하지 않습니다.

환경 파일을 사용할 수 없는 사고 상황:

```sh
./deploy/emergency-close.sh opsmate-demo
```

Compose label로 해당 OpsMate project만 중단합니다. 다른 NAS workload와 Office Ollama는 건드리지 않으며 합성 데이터 purge는 정상 close에서 별도로 수행합니다.

## 현재 검증 경계

- 전체 애플리케이션·PostgreSQL: `2026-08-04` clean verify 54개 성공
- 실제 모델 E2E: `2026-08-23 PASS`, `gemma3:12b`, 9/9, 관측 p95 21,076ms
- Synology Docker/x86_64, DSM 80/443 점유: runtime probe 확인
- Office native Ollama/model: runtime probe 확인
- Synology→restricted SSH tunnel→Office Ollama: 아직 실제 E2E 미검증
- public DSM ingress, 외부 smoke, DB/model 외부 비노출: 미검증
- normal/emergency close와 same-digest reopen: 미검증
- 외부 유료 API fallback: 없음
