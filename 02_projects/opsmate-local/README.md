# OpsMate Local

OpsMate Local은 자연어 구매 요청을 정책 근거가 연결된 초안으로 만들고, 사람의 승인 이후에만 발주를 생성하는 Java/Spring 포트폴리오 프로젝트입니다.

이 프로젝트의 핵심은 LLM이 업무 트랜잭션을 직접 실행하게 만드는 것이 아닙니다. 모델은 초안 작성에만 참여하고, 권한·상태 전이·멱등성·중복 발주 차단·트랜잭션은 Spring 서비스와 데이터베이스가 최종 통제합니다.

## 구현 범위

```text
자연어 구매 요청
-> 고정된 정책 검색 도구
-> 로컬 오픈웨이트 LLM 초안
-> 요청자 제출
-> 사람 승인 또는 반려
-> 승인된 요청만 발주
-> 감사 이벤트
```

의도적으로 전체 ERP를 만들지 않았습니다. AI Agent를 업무 트랜잭션에 연결할 때 필요한 최소 안전 경계를 하나의 수직 기능으로 검증합니다.

## 보여주는 역량

- Java 21, Spring Boot, Spring MVC, Spring Security, Spring Data JPA
- 명시적인 구매 요청 상태 전이와 역할 분리
- 고정된 typed tool과 구조화된 모델 출력 검증
- 로컬 오픈웨이트 LLM gateway와 모델 장애 시 `fail-closed`
- 요청 생성과 발주 생성의 멱등성
- DB 고유 제약을 이용한 중복 발주 차단
- 트랜잭션 롤백과 감사 이벤트 일관성
- 정상·권한 없음·모델 장애·중복·롤백 시나리오 테스트

## 안전 경계

| 주체 | 할 수 있는 일 | 할 수 없는 일 |
|---|---|---|
| LLM | 검색된 정책 근거를 바탕으로 구조화된 초안 제안 | DB 접근, 임의 URL 호출, 승인·반려·발주 실행 |
| REQUESTER | 초안 생성, 자신의 초안 제출 | 승인, 발주 |
| APPROVER | 제출된 요청 승인·반려 | 자기 요청 승인, 발주 |
| BUYER | 승인된 요청의 발주 생성 | 미승인·반려 요청 발주 |
| AUDITOR | 요청 상세와 감사 이벤트 조회 | 업무 상태 변경 |
| Spring 서비스 | 권한, 상태, 입력, 정책 근거, 멱등성 검증 | 검증 실패를 무시한 자동 진행 |

LLM gateway는 설정된 고정 base URL과 `/api/chat` 경로만 사용합니다. 허용 호스트 목록 밖의 주소는 애플리케이션 시작 시 거부하며, 외부 유료 API fallback은 없습니다.

요청 상세 조회도 객체 단위로 통제합니다. REQUESTER는 자신의 요청, APPROVER는 `PENDING_APPROVAL`, BUYER는 `APPROVED` 또는 `ORDERED`, AUDITOR는 감사 목적의 전체 요청만 볼 수 있습니다. 비감사 역할이 존재하지 않거나 읽을 수 없는 UUID를 조회하면 객체 열거를 막기 위해 모두 `403`을 반환합니다.

## 상태 전이

```text
DRAFT -> PENDING_APPROVAL -> APPROVED -> ORDERED
                          \-> REJECTED
```

허용되지 않은 전이는 도메인 객체에서 거부합니다. 발주는 `APPROVED` 상태에서만 생성되고, 요청당 발주 한 건이라는 DB 고유 제약을 함께 적용합니다.

## API 요약

| 메서드와 경로 | 역할 | 설명 |
|---|---|---|
| `POST /api/purchase-requests/drafts` | REQUESTER | 자연어 요청으로 정책 근거가 있는 초안 생성 |
| `POST /api/purchase-requests/{id}/submit` | REQUESTER | 자신의 초안을 승인 대기로 제출 |
| `POST /api/purchase-requests/{id}/decisions` | APPROVER | 승인 또는 반려 |
| `POST /api/purchase-orders` | BUYER | 승인된 요청으로 발주 생성 |
| `GET /api/purchase-requests/{id}` | 역할·소유권·상태 정책 통과 사용자 | 객체 단위 권한으로 요청 조회 |
| `GET /api/audit-events` | AUDITOR | 감사 이벤트 조회 |

초안 생성과 발주 생성 API에는 `Idempotency-Key` 헤더가 필요합니다. 같은 행위자·키·입력의 재시도는 기존 결과를 반환하고, 같은 키에 다른 입력을 사용하면 충돌로 거부합니다. 제출·승인·반려는 허용된 상태에서 한 번만 실행됩니다.

## 프로젝트 상태

| 항목 | 상태 | 한계 |
|---|---|---|
| 도메인·API·보안·DB 수직 기능 | `verified` | 2026-08-03 integration test 통과, 포트폴리오용 합성 도메인 |
| 테스트 대역을 사용한 Agent orchestration | `verified` | 실제 모델 품질을 검증하지 않음 |
| 실제 로컬 모델 연동 | `untested` | HTTP adapter는 mock 검증, 모델 서버 E2E 미검증 |
| 운영 배포 | `not-deployed` | 로컬 H2 검증 구성 |

2026-08-03에 Maven Wrapper로 19개 테스트를 실행해 모두 통과했습니다. 실제 로컬 모델이 없을 때 초안 API는 `503 Service Unavailable`로 중단되며, 구매 요청이나 발주를 만들지 않습니다.

동일한 초안 idempotency key가 동시에 처음 들어오면 두 요청이 저장되지는 않지만, 저장 전 모델 호출은 중복될 수 있습니다. single-flight 조정과 rate limit은 구현하지 않았으며 운영 전 보완 대상입니다. 동시성 부하·stress test도 수행하지 않았습니다.

## 문서

- [`ARCHITECTURE.md`](ARCHITECTURE.md): 신뢰 경계, 상태 전이, 트랜잭션과 위협 통제
- [`SETUP.md`](SETUP.md): 빌드, 테스트, 로컬 모델 연결과 재현 절차

## 공개 범위

모든 도메인, 정책, 계정과 데이터는 이 포트폴리오를 위해 독립적으로 만든 합성 예시입니다. 회사 코드, 고객 데이터, 내부 URL과 실제 업무 규칙을 포함하지 않습니다.
