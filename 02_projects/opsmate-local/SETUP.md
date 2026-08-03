# OpsMate Local Setup

## 요구사항

- JDK 21
- 선택: Ollama 호환 `/api/chat`을 제공하는 로컬 오픈웨이트 모델 서버

Maven Wrapper가 포함되어 있어 전역 Maven 설치는 필요하지 않습니다. 최초 실행에는 Maven 배포본과 의존성을 Maven Central에서 내려받기 위한 네트워크 연결이 필요합니다.

이 프로젝트는 Spring Boot 3.5 계열을 사용합니다. [Spring Boot 공식 요구사항](https://docs.spring.io/spring-boot/3.5/system-requirements.html)은 Java 17 이상과 Maven 3.6.3 이상입니다.

## 테스트

프로젝트 디렉터리에서 실행합니다.

```powershell
.\mvnw.cmd test
```

업무 흐름 테스트는 실제 모델 대신 명시적인 test double을 사용하고, Ollama HTTP adapter는 mock HTTP 서버로 검증합니다. 다음을 포함합니다.

- 정상: 초안 → 제출 → 승인 → 발주 → 감사 이벤트
- 권한 없음: 잘못된 역할의 쓰기 요청 차단
- 객체 권한: 소유권과 상태별 조회, 임의 UUID `403` 마스킹
- 모델 장애: 요청·발주를 생성하지 않고 `503`
- 모델 adapter: 비허용 host, 고정 path, 5xx, 빈·malformed·unknown-field 응답 거부
- 중복: 동일 키 재시도는 같은 결과, 다른 키의 중복 발주는 차단
- 충돌: stale JPA version과 DB constraint를 `409 WRITE_CONFLICT`로 정규화
- 롤백: 후처리 실패 시 발주·상태·성공 감사 이벤트 원복

## 모델 없이 실행

모델을 활성화하지 않는 것이 안전한 기본값입니다. 먼저 역할별 비밀번호를 환경변수로 지정합니다.

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

위 값은 현재 프로세스 환경에만 두고 셸 기록·스크립트·Git 파일에는 저장하지 마세요. 포트폴리오의 in-memory 계정은 로컬 검증 전용이며 실제 운영에서는 OIDC/SSO로 교체해야 합니다.

모델이 비활성 상태이면 서버는 기동되지만 초안 생성은 `503 Service Unavailable`로 실패합니다. 이것이 의도한 `fail-closed` 동작입니다.

## 로컬 오픈웨이트 모델 연결

모델 서버를 먼저 별도 프로세스로 실행하고, 현재 셸에서 다음 값을 지정합니다.

```powershell
$env:OPSMATE_LLM_ENABLED = "true"
$env:OPSMATE_LLM_BASE_URL = "http://127.0.0.1:11434"
$env:OPSMATE_LLM_ALLOWED_HOSTS = "127.0.0.1,localhost"
$env:OPSMATE_LLM_MODEL = "<installed-open-weight-model>"
.\mvnw.cmd spring-boot:run
```

사내 GPU 서버처럼 고정된 내부 host를 사용할 때만 `OPSMATE_LLM_ALLOWED_HOSTS`에 그 host를 명시적으로 추가합니다. 사용자 요청으로 base URL이나 경로를 전달할 수는 없습니다.

HTTP adapter의 고정 경로는 `/api/chat`입니다. adapter는 다음 조건을 기대합니다.

- `stream: false`
- JSON schema 형식 응답
- 응답 본문 `message.content`에 JSON 객체

## 데모 계정 역할

사용자명은 고정되고 비밀번호는 환경변수에서만 읽습니다.

| 사용자명 | 역할 |
|---|---|
| `requester` | REQUESTER |
| `approver` | APPROVER |
| `buyer` | BUYER |
| `auditor` | AUDITOR |

운영 인증 설계를 의미하지 않습니다. 실제 배포에서는 OIDC/SSO와 조직 권한 원장을 연결해야 합니다.

## 빌드와 실행

```powershell
.\mvnw.cmd clean package
java -jar target/opsmate-local-0.1.0-SNAPSHOT.jar
```

H2 데이터는 기본적으로 메모리에 저장되며 프로세스 종료 시 사라집니다.

## 현재 검증 범위

- 전체 자동화 테스트: 2026-08-03, 19개 통과
- 로컬 LLM gateway: test double과 mock HTTP 서버로 고정 path·정상 응답·5xx·빈 응답·malformed JSON·unknown field 거부 검증
- 실제 오픈웨이트 모델 E2E: 미검증
- HTTP timeout 시간 의존 테스트와 동시성 부하·stress test: 미검증
- 외부 유료 API fallback: 없음
