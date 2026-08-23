# OpsMate Local

OpsMate Local은 자연어 구매 요청을 정책 근거가 연결된 초안으로 만들고, 사람의 승인 이후에만 발주를 생성하는 Java/Spring 포트폴리오 프로젝트입니다. 모델은 구조화된 초안만 제안하며 권한, 상태 전이, 멱등성, 트랜잭션과 발주는 Spring 서비스와 데이터베이스가 최종 통제합니다.

현재 구매 요청부터 승인, 발주, 감사까지의 수직 기능과 공개 Thymeleaf 데모, 방문자별 workspace 격리, PostgreSQL/Flyway 역할 분리, 모델 호출 보호, Docker 배포·중단 자산을 구현했습니다. `2026-08-04` 전체 `clean verify`에서 54개 테스트가 성공했고 실패·오류·건너뜀은 0개였습니다. `2026-08-23` 승인된 GPU 호스트의 native Ollama `gemma3:12b`로 실제 모델 E2E 9/9와 관측 p95 21,076ms(`<= 30,000ms` gate)를 확인했습니다.

실제 public URL, Synology -> 제한된 SSH tunnel -> Office Ollama 전체 경로, 외부 DB/model 비노출과 close/reopen rehearsal은 아직 검증 중이므로 전체 상태는 `tested-component`입니다.

## 구현 범위

```text
브라우저 데모 세션과 합성 workspace
-> 자연어 구매 요청
-> 서버 주도 정책 조회
-> 오픈웨이트 모델의 구조화 초안
-> 서버 측 출력·정책 검증
-> 요청자 제출
-> 사람 승인 또는 반려
-> 승인된 요청만 발주
-> workspace 범위 감사 이벤트
```

전체 ERP를 재현하기보다 AI 제안을 업무 트랜잭션에 연결할 때 필요한 최소 안전 경계를 하나의 수직 기능으로 구현했습니다. 외부 유료 API fallback과 가짜 운영 모델 fallback은 없습니다.

## 보여주는 역량

- Java 21, Spring Boot, Spring MVC, Thymeleaf, Spring Security, Spring Data JPA
- 명시적인 구매 요청 상태 전이와 요청자·승인자·구매자·감사자 역할 분리
- 서버 주도 정책 조회와 구조화된 모델 출력의 재검증
- 모델 장애·malformed·oversized 응답에서 저장 전 `fail-closed`
- 동일 요청 single-flight, 전체 동시 모델 호출 `1`, workspace·전체 quota와 bounded queue/follower
- 방문자별 workspace 격리, TTL, 수용량·시작률 제한과 합성 데이터 정리
- PostgreSQL/Flyway migration 역할과 장기 실행 runtime 역할 분리
- 요청·발주 멱등성, DB 제약과 optimistic lock
- non-root/read-only 컨테이너, 내부 Docker network, 제한된 SSH model transport
- normal close, credential-independent emergency close와 same-artifact reopen을 위한 운영 자산

## 안전 경계

| 주체 | 할 수 있는 일 | 할 수 없는 일 |
|---|---|---|
| LLM | 서버가 전달한 합성 정책 근거를 바탕으로 구조화된 초안 제안 | 정책 조회 포트·DB·임의 URL 접근, 승인·반려·발주 실행 |
| REQUESTER | 초안 생성, 자신의 초안 제출 | 승인, 발주 |
| APPROVER | 제출된 요청 승인·반려 | 자기 요청 승인, 발주 |
| BUYER | 승인된 요청의 발주 생성 | 미승인·반려 요청 발주 |
| AUDITOR | 현재 workspace의 요청과 감사 이벤트 조회 | 업무 상태 변경 |
| Spring 서비스 | workspace, 권한, 상태, 입력, 정책 근거, 멱등성 검증 | 검증 실패를 무시한 자동 진행 |

LLM gateway는 설정된 고정 base URL과 `/api/chat` 경로만 사용합니다. 허용 host 밖의 주소, 잘못된 timeout·출력 제한·응답 크기 제한은 시작 시 거부합니다. 공개 배포에서는 앱이 `http://model-tunnel:11434`만 허용하며 일반 인터넷 egress network에 연결되지 않습니다.

공개 `demo` 프로필에서는 `/api/**`를 `403`으로 거부하고 Basic 인증 challenge를 노출하지 않습니다. 동일 출처 HTML form은 CSRF token을 사용하며 workspace와 actor는 브라우저 입력이 아니라 서버 세션의 `DemoPrincipal`에서 가져옵니다.

## 상태 전이

```text
DRAFT -> PENDING_APPROVAL -> APPROVED -> ORDERED
                          \-> REJECTED
```

허용되지 않은 전이는 도메인 객체에서 거부합니다. 발주는 `APPROVED` 상태에서만 생성되고 요청당 발주 한 건이라는 DB 고유 제약을 함께 적용합니다.

## 현재 공개 배포 구조

```text
External browser
  -> HTTPS / Synology DSM Reverse Proxy + TLS
  -> NAS loopback Nginx edge
  -> Spring Boot
       -> PostgreSQL (Docker internal only)
       -> model_link (Docker internal only)
            -> non-root SSH tunnel
                 -> Office SSH
                 -> 127.0.0.1:11434 native Ollama
```

- DSM이 이미 NAS의 80/443을 사용하므로 OpsMate 컨테이너가 해당 host port를 점유하지 않습니다.
- OpsMate Nginx edge는 NAS loopback high port 하나에만 bind합니다.
- PostgreSQL, app, model-tunnel은 public host port를 갖지 않습니다.
- app은 일반 egress network가 없고, `model-tunnel`만 Office SSH를 위한 outbound network를 가집니다.
- 전용 Office SSH key는 Ollama loopback 목적지 한 곳만 local forwarding하도록 제한합니다.
- OpsMate close는 전용 tunnel을 닫지만 공유 native Ollama daemon 자체를 임의 종료하지 않습니다.

## 구현과 검증 경계

| 범위 | 현재 상태 |
|---|---|
| 구매 요청→제출→승인/반려→발주→감사 수직 흐름 | `implemented`, 컴포넌트·통합 테스트 존재 |
| 공개 Thymeleaf session UI, CSRF, `/api/**` 거부, workspace 격리·TTL | `implemented`, 자동화 테스트 존재 |
| model single-flight·동시 실행 1·queue/follower/workspace/전체 호출량 제한 | `implemented`, 자동화 테스트 존재 |
| PostgreSQL Flyway migration·runtime 역할 분리·DB 제약 | `implemented`, Testcontainers 검증 경로 존재 |
| Synology loopback Nginx edge + internal networks + restricted SSH tunnel 자산 | `implemented`, CI/config 검증 중 |
| 최신 전체 `clean verify` | `2026-08-04`, 54개 성공, 실패·오류·건너뜀 0개 |
| 실제 오픈웨이트 모델 9개 합성 prompt E2E | `2026-08-23 PASS`, `gemma3:12b`, 9/9, 관측 p95 21,076ms (`<= 30,000ms`) |
| NAS→SSH tunnel→Office Ollama runtime E2E | 미검증 |
| public URL·외부 모바일 smoke·DB/model 외부 차단 | 미검증 |
| normal/emergency close + same-digest reopen | 미검증 |

실제 모델 E2E의 환경·명령·측정 경계는 [`docs/REAL_MODEL_E2E_EVIDENCE.md`](docs/REAL_MODEL_E2E_EVIDENCE.md)에 기록했습니다. 이 결과는 9개 합성 요청과 해당 GPU 호스트에 한정된 관측값이며 일반적인 생성 품질이나 용량 계획을 보장하지 않습니다.

## 열기, 닫기, 다시 열기

Synology 앱 호스트의 운영 진입점은 다음 네 개입니다.

- `deploy/open-demo.sh`
- `deploy/close-demo.sh`
- `deploy/verify-closed.sh`
- `deploy/emergency-close.sh`

`open-demo.sh`는 immutable app/tunnel image digest, DB 역할, NAS-local SSH secret, tunnel-only model path와 edge gate를 확인한 뒤 model-tunnel -> DB/migration -> app -> loopback Nginx edge -> public HTTPS smoke 순서로 진행합니다.

정상 close는 edge와 app, model-tunnel을 먼저 중단한 뒤 합성 workspace를 삭제하고 PostgreSQL을 멈춥니다. emergency close는 환경 파일 없이 Compose label로 OpsMate project의 컨테이너만 중단하며 다른 NAS workload와 Office Ollama는 건드리지 않습니다.

reopen은 이전에 검증한 app/tunnel image의 정확한 digest를 다시 사용하고 tunnel health, migration/readiness와 public smoke를 다시 통과해야 same-artifact reopen으로 인정합니다.

## 문서

- [`ARCHITECTURE.md`](ARCHITECTURE.md): 신뢰 경계, 상태 전이, 데이터·모델·배포 통제
- [`SETUP.md`](SETUP.md): 로컬 빌드, 테스트, 실제 모델 E2E와 배포 준비
- [`docs/REAL_MODEL_E2E_EVIDENCE.md`](docs/REAL_MODEL_E2E_EVIDENCE.md): 실제 모델 E2E 환경·측정·한계
- [`docs/PUBLIC_DEMO.md`](docs/PUBLIC_DEMO.md): 공개 UI와 배포 완료 검수 기준
- [`docs/THREAT_MODEL.md`](docs/THREAT_MODEL.md): 위협, 구현 통제와 외부 검증 gate
- [`docs/SERVICE_RUNBOOK.md`](docs/SERVICE_RUNBOOK.md): Synology app/tunnel open·close·reopen 절차
- [`../../03_portfolio/code-explanation-standard.md`](../../03_portfolio/code-explanation-standard.md): 코드 설명 주석 기준

## 공개 범위

모든 도메인, 정책, 계정과 데이터는 이 포트폴리오를 위해 독립적으로 만든 합성 예시입니다. 회사 코드, 고객 데이터, 내부 URL, 접속 정보, credential과 실제 업무 규칙을 포함하지 않습니다.
