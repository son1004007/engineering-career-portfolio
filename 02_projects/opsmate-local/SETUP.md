# OpsMate Local Setup

## 요구사항

- JDK 21
- 기본 테스트: Docker daemon과 Testcontainers가 사용할 PostgreSQL image
- 실제 모델 E2E: 승인된 Ollama 호환 `/api/chat` endpoint
- 공개 배포: Linux, Docker Compose v2, HTTPS domain, 승인된 사설 GPU 모델 호스트

Maven Wrapper가 포함되어 있어 전역 Maven 설치는 필요하지 않습니다. 최초 실행에는 Maven 배포본과 의존성을 내려받을 네트워크 연결이 필요합니다.

## 전체 자동화 검증

프로젝트 디렉터리에서 실행합니다.

```powershell
.\mvnw.cmd -q clean verify
```

이 명령은 기본 회귀 테스트, PostgreSQL Testcontainers 통합 경로와 Javadoc 검사를 실행합니다. `2026-08-04` 실행에서는 54개 테스트가 성공했고 실패·오류·건너뜀은 0개였습니다. Docker를 사용할 수 없으면 PostgreSQL 검증은 완료로 간주할 수 없습니다.

검증 범위에는 다음이 포함됩니다.

- 초안 → 제출 → 승인/반려 → 발주 → 감사 이벤트
- 역할, 자기 승인 차단, 객체 소유권·상태·workspace 격리
- 웹 CSRF, session, persona 전환과 공개 `/api/**` 거부
- workspace TTL·수용량·시작률 제한과 거부 요청의 session 미할당
- 모델 장애·malformed/unknown/oversized 응답, 출력 token·응답 byte 제한
- 동일 key single-flight, follower·queue·workspace·전체 호출량 제한
- 멱등성, optimistic lock, DB constraint와 트랜잭션 롤백
- PostgreSQL Flyway migration, runtime DML, runtime DDL·Flyway history 접근 거부와 cascade
- 코드 설명 표준과 Javadoc doclint

## 모델 없이 로컬 API 실행

모델을 활성화하지 않는 것이 안전한 기본값입니다. 역할별 비밀번호는 현재 PowerShell 프로세스의 환경변수에만 둡니다.

```powershell
$requesterCredential = Get-Credential -UserName requester -Message "REQUESTER 임시 비밀번호"
$approverCredential = Get-Credential -UserName approver -Message "APPROVER 임시 비밀번호"
$buyerCredential = Get-Credential -UserName buyer -Message "BUYER 임시 비밀번호"
$auditorCredential = Get-Credential -UserName auditor -Message "AUDITOR 임시 비밀번호"
$env:OPSMATE_REQUESTER_PASSWORD = $requesterCredential.GetNetworkCredential().Password
$env:OPSMATE_APPROVER_PASSWORD = $approverCredential.GetNetworkCredential().Password
$env:OPSMATE_BUYER_PASSWORD = $buyerCredential.GetNetworkCredential().Password
$env:OPSMATE_AUDITOR_PASSWORD = $auditorCredential.GetNetworkCredential().Password
.\mvnw.cmd spring-boot:run
```

모델이 비활성 상태이면 서버는 기동되지만 초안 생성은 `503 Service Unavailable`로 실패하고 해당 초안 데이터를 만들지 않습니다. in-memory 계정과 H2는 로컬 API 검증 전용입니다.

## 로컬 오픈웨이트 모델 연결

모델 서버를 별도 프로세스로 실행하고 현재 셸에 고정 endpoint와 모델을 지정합니다.

```powershell
$env:OPSMATE_LLM_ENABLED = "true"
$env:OPSMATE_LLM_BASE_URL = "http://127.0.0.1:11434"
$env:OPSMATE_LLM_ALLOWED_HOSTS = "127.0.0.1,localhost"
$env:OPSMATE_LLM_MODEL = "<installed-open-weight-model:tag>"
$env:OPSMATE_LLM_MAX_OUTPUT_TOKENS = "512"
$env:OPSMATE_LLM_MAX_RESPONSE_BYTES = "65536"
.\mvnw.cmd spring-boot:run
```

인증 proxy가 있으면 `OPSMATE_LLM_AUTH_TOKEN`도 현재 프로세스에만 추가합니다. 사용자 요청으로 base URL, 경로나 provider를 바꿀 수 없고 adapter의 호출 경로는 `/api/chat`으로 고정됩니다.

## 승인된 실제 모델 E2E

기본 `clean verify`는 실제 모델을 호출하지 않습니다. 승인된 endpoint에서만 다음 gate를 명시적으로 실행합니다.

```powershell
$env:OPSMATE_REAL_MODEL_E2E = "YES"
$env:OPSMATE_LLM_BASE_URL = "<approved-private-model-base-url>"
$env:OPSMATE_LLM_ALLOWED_HOSTS = "<approved-model-host>"
$env:OPSMATE_LLM_MODEL = "<approved-model:tag>"
$env:OPSMATE_LLM_AUTH_TOKEN = "<ephemeral-proxy-token>"
$env:OPSMATE_REAL_MODEL_P95_MAX_MS = "30000"
.\mvnw.cmd -q -Preal-model-e2e verify
```

이 gate는 9개 합성 요청을 실제 `/api/chat`에 보내고 서버 검증을 통과한 분류, 저장 건수와 p95 상한을 확인합니다. host, token과 원문 응답은 공개 검증 기록에 남기지 않습니다. 승인이 없거나 endpoint를 사용할 수 없으면 실행하지 않고 `미검증`으로 유지합니다.

## one-shot PostgreSQL migration

공개 배포에서는 장기 실행 앱이 Flyway credential을 갖지 않습니다. 같은 jar를 migration 전용 명령으로 한 번 실행합니다.

```text
java -jar opsmate-local.jar --opsmate-migrate-only
```

실제 값은 `OPSMATE_DB_URL`, `OPSMATE_FLYWAY_USERNAME`, `OPSMATE_FLYWAY_PASSWORD`, `OPSMATE_FLYWAY_APP_ROLE` 환경변수로 주입합니다. 앱은 migration 성공 뒤 runtime 계정으로 `ddl-auto=validate`를 수행합니다. 운영 비밀값을 명령행, 문서, Git 또는 CI log에 넣지 않습니다.

## 공개 배포 준비

저장소 예시 파일을 실제 호스트의 비추적 `.env`로 복사합니다.

```text
deploy/.env.example
deploy/model-host/.env.example
```

두 파일은 서로 다른 호스트에서 사용합니다. 실제 값에는 다음이 필요합니다.

- 검증한 애플리케이션 image의 full SHA-256 digest
- full digest로 고정한 Ollama·Caddy image
- 승인한 모델의 명시적 tag와 실제 content ID
- 서로 다른 PostgreSQL admin·migration·runtime 역할과 충분히 긴 비밀번호
- 승인된 VPN IPv4에만 bind한 model proxy와 임시 Bearer token
- 공개 domain과 ACME 연락 주소
- 실제 host egress allowlist와 edge/WAF rate limit을 확인한 증거 flag

`.env`, backup, token, host/IP, tunnel과 승인 문서 원문을 저장소에 추가하지 않습니다.

## 공개 서비스 열기와 닫기

모델 호스트를 먼저 연 뒤 애플리케이션 호스트를 엽니다.

```sh
./deploy/model-host/open-model.sh
./deploy/open-demo.sh
```

`open-demo.sh`는 private model health, digest와 환경을 확인하고 DB→one-shot migration→app→Caddy를 시작한 뒤 HTTPS 실제 모델 smoke를 완료해야 성공합니다. host egress allowlist나 edge/WAF rate limit 증거가 없으면 fail-closed로 중단합니다.

개발과 검증이 끝나면 각 호스트에서 별도로 닫습니다.

```sh
./deploy/close-demo.sh
./deploy/model-host/close-model.sh
```

앱 close는 공개 edge·app을 먼저 멈춘 뒤 합성 workspace를 삭제하고 DB를 중단합니다. 모델 close는 proxy와 Ollama를 멈추고 GPU를 해제합니다. 두 closed verification을 모두 통과해야 전체 서비스가 닫혔다고 판단합니다.

환경 파일을 사용할 수 없는 사고 상황에서는 명시적인 Compose project 이름으로 emergency close를 실행합니다.

```sh
./deploy/emergency-close.sh opsmate-demo
./deploy/model-host/emergency-close.sh opsmate-model-host
```

emergency close는 credential 없이 컨테이너를 멈추지만 합성 데이터 purge는 하지 않습니다. 환경을 복구한 뒤 정상 close와 삭제 확인이 필요합니다. 자세한 절차는 [`docs/SERVICE_RUNBOOK.md`](docs/SERVICE_RUNBOOK.md)에 있습니다.

## 현재 검증 경계

- 2026-08-03 기준 과거 baseline: 기본 자동화 테스트 19개 성공. 이 기록은 당시 구현의 역사적 근거입니다.
- 최신 애플리케이션·PostgreSQL 컴포넌트: `2026-08-04` 전체 `clean verify` 54개 성공, 실패·오류·건너뜀 0개
- 배포 자산: `implemented`; 실제 양 호스트 rehearsal과 외부 gate는 미검증
- 실제 승인 모델 E2E: 미검증
- 실제 public domain/ACME, 외부 모바일 smoke, DB·모델 외부 차단: 미검증
- host egress allowlist와 edge/WAF rate limit: 외부 배포 gate, 미검증
- 앱·모델 양쪽 호스트의 normal/emergency close와 same-digest reopen rehearsal: 미검증
- 외부 유료 API fallback: 없음
