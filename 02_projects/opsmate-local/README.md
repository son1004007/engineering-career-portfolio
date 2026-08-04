# OpsMate Local

OpsMate Local은 자연어 구매 요청을 정책 근거가 연결된 초안으로 만들고, 사람의 승인 이후에만 발주를 생성하는 Java/Spring 포트폴리오 프로젝트입니다. 모델은 구조화된 초안만 제안하고, 권한·상태 전이·멱등성·트랜잭션과 발주는 Spring 서비스와 데이터베이스가 최종 통제합니다.

현재 공개 웹 데모, 방문자별 workspace 격리, PostgreSQL migration, 모델 호출 보호, Docker/Caddy 배포와 닫기·다시 열기 자산까지 구현했습니다. `2026-08-04` 전체 `clean verify`에서 54개 테스트가 성공했고 실패·오류·건너뜀은 0개였습니다. 승인된 실제 모델 E2E, 공개 URL, 외부 smoke test, 호스트 egress allowlist와 edge/WAF rate limit, 애플리케이션·모델 양쪽 호스트의 close/reopen rehearsal은 아직 검증하지 않았으므로 전체 상태는 `tested-component`입니다.

## 구현 범위

```text
브라우저 데모 세션과 합성 workspace
-> 자연어 구매 요청
-> 서버 주도 정책 조회
-> 승인된 사설 GPU 모델 호스트의 구조화 초안
-> 요청자 제출
-> 사람 승인 또는 반려
-> 승인된 요청만 발주
-> workspace 범위 감사 이벤트
```

전체 ERP를 재현하기보다 AI 제안을 업무 트랜잭션에 연결할 때 필요한 최소 안전 경계를 하나의 수직 기능으로 구현했습니다. 외부 유료 API fallback과 가짜 운영 모델 fallback은 없습니다.

## 보여주는 역량

- Java 21, Spring Boot, Spring MVC, Thymeleaf, Spring Security, Spring Data JPA
- 명시적인 구매 요청 상태 전이와 요청자·승인자·구매자·감사자 역할 분리
- 서버 주도 정책 조회 포트와 구조화된 모델 출력 검증
- 모델 장애·잘못된 출력·과대 응답에서 모델 의존 초안 생성을 저장 전 `fail-closed`
- 동일 요청 single-flight, 전체 동시 모델 호출 제한, workspace·전체 호출량과 대기자 수 제한
- 방문자별 workspace 격리, TTL, 수용량·시작률 제한과 합성 데이터 정리
- PostgreSQL/Flyway migration 계정과 장기 실행 runtime 계정 분리
- 요청 생성과 발주 생성의 멱등성, DB 제약과 optimistic lock
- HTTPS edge, 제한된 컨테이너 권한, 분리된 네트워크와 재현 가능한 open/close 자산
- 업무 규칙과 변경 영향을 설명하는 한국어 Javadoc·주석 기준

## 안전 경계

| 주체 | 할 수 있는 일 | 할 수 없는 일 |
|---|---|---|
| LLM | 서버가 전달한 합성 정책 근거를 바탕으로 구조화된 초안 제안 | 정책 조회 포트·DB·임의 URL 접근, 승인·반려·발주 실행 |
| REQUESTER | 초안 생성, 자신의 초안 제출 | 승인, 발주 |
| APPROVER | 제출된 요청 승인·반려 | 자기 요청 승인, 발주 |
| BUYER | 승인된 요청의 발주 생성 | 미승인·반려 요청 발주 |
| AUDITOR | 현재 workspace의 요청과 감사 이벤트 조회 | 업무 상태 변경 |
| Spring 서비스 | workspace, 권한, 상태, 입력, 정책 근거, 멱등성 검증 | 검증 실패를 무시한 자동 진행 |

LLM gateway는 설정된 고정 base URL과 `/api/chat` 경로만 사용합니다. 허용 host 밖의 주소, 잘못된 timeout·출력 제한·응답 크기 제한은 시작 시 거부합니다. 모델 응답은 제한된 바이트만 읽고 출력 토큰 상한을 함께 전달합니다.

공개 `demo` 프로필에서는 `/api/**`를 `403`으로 거부하고 Basic 인증 challenge를 노출하지 않습니다. 동일 출처 HTML form은 실제 CSRF token을 사용하며, workspace와 actor는 브라우저 입력이 아니라 서버 세션의 `DemoPrincipal`에서 가져옵니다.

## 상태 전이

```text
DRAFT -> PENDING_APPROVAL -> APPROVED -> ORDERED
                          \-> REJECTED
```

허용되지 않은 전이는 도메인 객체에서 거부합니다. 발주는 `APPROVED` 상태에서만 생성되고, 요청당 발주 한 건이라는 DB 고유 제약을 함께 적용합니다.

## 공개 데모 경로

| 메서드와 경로 | 역할 | 설명 |
|---|---|---|
| `GET /` | 익명 | 설명과 데모 시작 화면 |
| `POST /demo/sessions` | 익명 | 서버가 새 workspace와 REQUESTER persona 생성 |
| `GET /demo` | 활성 세션 | 현재 persona의 작업함과 감사 타임라인 렌더링 |
| `POST /demo/personas` | 활성 세션 | allowlist 안의 persona 전환 |
| `POST /demo/drafts` | REQUESTER | 자연어 요청으로 정책 근거가 있는 초안 생성 |
| `POST /demo/requests/{id}/submit` | REQUESTER | 현재 workspace의 자신의 초안 제출 |
| `POST /demo/requests/{id}/decisions` | APPROVER | 현재 workspace의 승인 대기 요청 승인·반려 |
| `POST /demo/orders` | BUYER | 현재 workspace의 승인된 요청으로 발주 생성 |
| `POST /demo/reset` | 활성 세션 | 현재 합성 workspace를 삭제하고 새 workspace 생성 |
| `POST /demo/end` | 활성 세션 | 현재 합성 workspace 삭제와 세션 종료 |

로컬·자동화 검증용 REST API는 별도 Basic 보안 체인으로 유지합니다. 경로는 [`ARCHITECTURE.md`](ARCHITECTURE.md)에 정리했습니다.

## 구현과 검증 경계

| 범위 | 현재 상태 |
|---|---|
| 구매 요청→제출→승인/반려→발주→감사 수직 흐름 | `implemented`, 컴포넌트·통합 테스트 존재 |
| 공개 Thymeleaf session UI, CSRF, `/api/**` 거부, workspace 격리·TTL | `implemented`, 자동화 테스트 존재 |
| model single-flight·동시 실행 1·queue/follower/workspace/전체 호출량 제한 | `implemented`, 자동화 테스트 존재 |
| PostgreSQL Flyway migration·runtime 역할 분리·DB 제약 | `implemented`, Testcontainers 검증 경로 존재 |
| Docker/Caddy, 고정 digest, `restart: no`, 제한 로그·권한·네트워크 | `implemented`, 구성 검증 자산 존재 |
| 앱 호스트와 모델 호스트의 open/normal close/emergency close/closed 확인 | `implemented`, 실제 양 호스트 rehearsal 미검증 |
| 최신 전체 `clean verify` | 2026-08-04, 54개 성공, 실패·오류·건너뜀 0개 |
| 승인된 실제 오픈웨이트 모델 9개 합성 prompt E2E | 미검증 |
| 공개 URL·외부 모바일 smoke·외부 포트 차단 | 미검증 |
| host egress allowlist·edge/WAF 익명 요청 rate limit 증거 | 외부 배포 gate, 미검증 |

모델이 비활성 또는 사용할 수 없는 상태이면 해당 초안 생성은 저장 전에 중단되므로 그 요청에서 구매 요청이나 후속 발주가 만들어지지 않습니다. 이미 제출된 요청의 승인·반려·발주는 모델 가용성과 분리됩니다. 실제 모델의 생성 품질·응답 시간·GPU 요구량은 승인된 모델 호스트에서 별도로 측정해야 합니다.

## 열기, 닫기, 다시 열기

배포 자산은 애플리케이션 호스트와 모델 호스트로 분리되어 있습니다.

- 애플리케이션: `deploy/open-demo.sh`, `close-demo.sh`, `verify-closed.sh`, `emergency-close.sh`
- 모델: `deploy/model-host/open-model.sh`, `close-model.sh`, `verify-private.sh`, `emergency-close.sh`

정상 close는 먼저 공개 edge와 애플리케이션을 중단한 뒤 합성 workspace를 삭제하고 PostgreSQL을 멈춥니다. 모델 호스트는 별도로 proxy와 Ollama를 닫아 GPU를 해제합니다. 두 절차를 모두 실행해야 전체 서비스가 닫힌 상태입니다. 데이터베이스와 모델 volume은 보존하되 공개 세션 데이터는 복원하지 않습니다.

reopen은 이전에 검증한 애플리케이션 image의 정확한 digest와 승인한 모델 tag·content ID를 다시 사용하고 smoke test를 통과한 뒤에만 공개합니다. 환경 파일을 사용할 수 없는 사고 상황에서는 project label로 컨테이너를 찾는 emergency close로 진입점을 먼저 차단할 수 있습니다.

실제 운영 명령과 선행 조건은 [`docs/SERVICE_RUNBOOK.md`](docs/SERVICE_RUNBOOK.md)를 따릅니다.

## 문서

- [`ARCHITECTURE.md`](ARCHITECTURE.md): 신뢰 경계, 상태 전이, 데이터·모델·배포 통제
- [`SETUP.md`](SETUP.md): 로컬 빌드, 테스트, 실제 모델 E2E와 배포 전 준비
- [`docs/PUBLIC_DEMO.md`](docs/PUBLIC_DEMO.md): 공개 UI와 배포 완료 검수 기준
- [`docs/THREAT_MODEL.md`](docs/THREAT_MODEL.md): 위협, 구현 통제와 외부 검증 gate
- [`docs/SERVICE_RUNBOOK.md`](docs/SERVICE_RUNBOOK.md): 앱·모델 호스트의 open/close/reopen 절차
- [`../../03_portfolio/code-explanation-standard.md`](../../03_portfolio/code-explanation-standard.md): 재사용 가능한 코드 설명 주석 기준

## 공개 범위

모든 도메인, 정책, 계정과 데이터는 이 포트폴리오를 위해 독립적으로 만든 합성 예시입니다. 회사 코드, 고객 데이터, 내부 URL, 접속 정보와 실제 업무 규칙을 포함하지 않습니다.
