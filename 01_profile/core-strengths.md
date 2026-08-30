# Core Strengths

이 문서는 특정 기술 스택이 아니라 **어떤 문제를 어떻게 해결하는지**를 기준으로 강점을 정리합니다.

## 1. 문제를 시스템 구조로 바꾸는 능력

쉽게 말하면:

- 모호한 요청을 바로 코딩하기보다 실제 사용자 흐름과 완료 기준으로 정리합니다.
- 한 기능이 화면, API, DB, batch와 운영에 어떤 영향을 주는지 함께 봅니다.
- 잘못된 상태 변경이나 예외 상황을 미리 고려합니다.

기술적으로는:

- system boundary
- state transition
- transaction boundary
- acceptance criteria
- failure handling

## 2. 백엔드와 데이터를 실제 서비스로 연결하는 능력

쉽게 말하면:

- 데이터를 조회하는 것에서 끝내지 않고 사용자가 쓸 수 있는 화면과 API까지 연결합니다.
- 분석 결과나 AI 결과를 업무 기능 안에 넣을 수 있도록 구조화합니다.

경험:

- Java/Spring Boot 기반 웹 애플리케이션과 API
- Python/FastAPI 기반 API 서버
- Oracle, PostgreSQL, MySQL과 SQL
- 데이터 분석 결과의 서비스화
- 화면, backend, DB를 연결하는 end-to-end 흐름

## 3. AI를 기능과 개발 과정 모두에 활용하는 능력

쉽게 말하면:

- AI에게 중요한 업무 결정을 그대로 맡기지 않습니다.
- AI가 잘하는 초안, 탐색, 요약과 사람이 또는 서버가 책임져야 할 판단을 나눕니다.
- 개발 과정에서도 AI를 조사, 구현, 테스트와 검토에 활용하되 결과를 다시 검증합니다.

경험:

- Text2SQL/NL2SQL
- RAG와 LLM 응용
- local LLM adapter
- Agentic AI Runtime
- validation set과 다중 모델 비교
- artifact/provenance 추적

## 4. 잘못된 결과를 막는 검증 중심 개발

쉽게 말하면:

- 코드가 존재한다고 완료로 보지 않습니다.
- 정상 동작뿐 아니라 실패, 권한, 중복, 잘못된 입력과 외부 연계 장애를 테스트합니다.
- 실제 실행 환경에서 확인할 수 있는 것은 E2E와 runtime evidence로 다시 확인합니다.

기술적으로는:

- unit/integration/E2E test
- regression test
- AI evaluation
- fail-closed
- health check
- evidence-driven verification

## 5. 보안과 권한을 개발 문제로 다루는 능력

쉽게 말하면:

- 누가 어떤 일을 할 수 있는지 시스템이 확인하도록 만듭니다.
- 잘못된 사용이나 실패가 중요한 업무 처리로 이어지지 않도록 차단합니다.
- 누가 무엇을 했는지 다시 확인할 수 있는 기록을 중요하게 봅니다.

기술적으로는:

- authentication / authorization
- RBAC
- session boundary
- audit event
- least privilege
- secure failure handling

정보보안 업무 경험이 있어 개발과 운영 과정에서 이러한 관점을 함께 적용할 수 있습니다.

## 6. 배포와 운영까지 연결하는 능력

쉽게 말하면:

- 내 PC에서 실행되는 것만으로 완료라고 보지 않습니다.
- 실제 서버에서 실행하고, 상태를 확인하고, 문제가 생기면 복구할 수 있는 구조를 고려합니다.

경험:

- Linux
- Docker
- Tomcat / Nginx
- Jenkins / GitHub Actions
- remote development environment
- deployment, health verification, rollback and recovery

## 7. 다른 사람이 이어받을 수 있게 만드는 능력

쉽게 말하면:

- 작업 방법과 현재 상태를 개인 기억에만 두지 않습니다.
- 개발자나 다른 AI가 프로젝트를 이어받아도 목표와 제한, 검증 결과를 파악할 수 있도록 정리합니다.

경험:

- Git 기반 협업
- 실행/개발환경 가이드
- 공통 코드와 작업 규칙 정리
- repository 상태 문서와 evidence 관리
- AI Agent 작업 context와 검증 절차 구조화

## 8. 기술을 목적이 아니라 도구로 보는 관점

Java/Spring, Python/FastAPI, SQL, Docker, LLM 등 다양한 기술을 사용하지만 핵심 강점은 특정 문법이나 framework 암기가 아닙니다.

```text
문제 이해
-> 적절한 구조 선택
-> 필요한 도구 선택
-> 구현
-> 검증
-> 운영과 evidence
```

이 흐름을 끝까지 연결하는 것을 강점으로 봅니다.
