# Software / Backend / Platform Engineer

[공개 포트폴리오 보기](https://son1004007.github.io/engineering-career-portfolio/)

업무에서 필요한 기능을 **문제와 제약조건으로 정리하고, 적절한 기술과 AI를 활용해 실제로 사용할 수 있는 시스템으로 구현하는 개발자**입니다.

특정 언어나 프레임워크 자체보다 다음을 중요하게 봅니다.

- 요구사항을 시스템 구조와 완료조건으로 바꾸는 것
- 데이터, 권한, 상태와 실패 조건을 명확하게 설계하는 것
- AI를 코드 작성 도구와 서비스 기능 양쪽에 실용적으로 활용하는 것
- 테스트와 실제 실행 결과로 구현이 맞는지 검증하는 것
- 배포, 보안, 장애와 복구까지 고려해 운영 가능한 형태로 끝내는 것

Java/Spring, Python/FastAPI, SQL, Docker와 LLM은 이 일을 하기 위해 사용하는 도구입니다.

## 제가 잘하는 일

| 역량 | 쉽게 설명하면 | 기술적으로는 |
|---|---|---|
| 문제를 시스템으로 구조화 | 모호한 요청을 구현 범위와 완료조건으로 정리합니다 | system boundary, state, transaction, acceptance criteria |
| 백엔드와 데이터 연결 | 화면이나 AI 기능이 실제 업무 데이터와 안전하게 연결되도록 만듭니다 | API, SQL, Oracle, PostgreSQL, Spring Boot, FastAPI |
| AI를 실제 업무에 적용 | AI가 잘하는 일과 서버가 책임져야 할 일을 분리합니다 | LLM integration, Agent, RAG, Text2SQL/NL2SQL, structured output |
| 잘못된 처리를 시스템에서 차단 | 권한이 없거나 결과가 잘못되면 안전하게 중단되도록 설계합니다 | RBAC, session boundary, validation, idempotency, fail-closed |
| 결과를 검증 | 만들었다는 설명보다 테스트와 실제 실행 결과를 남깁니다 | unit/integration/E2E, regression, eval, evidence |
| 운영까지 연결 | 배포하고 닫고 다시 열 수 있는 구조와 복구 절차를 고려합니다 | Linux, Docker, Tomcat, Nginx, CI/CD, health check, rollback |

## 숫자로 확인된 공개 증거

- `OpsMate Local`: 실제 Ollama `gemma3:12b` 모델을 사용한 합성 업무 요청 E2E `9/9` 성공
- `OpsMate Local`: 실제 모델 응답 관측 p95 `21,076ms`, 프로젝트 gate `<= 30,000ms` 충족
- `OpsMate Local`: `2026-08-29` 실제 Internet HTTPS 경로에서 session 격리, rate limit, DB/model 비노출, close/reopen 경계 검증
- 인증·권한 통합 사례: 자동 테스트 `24개` 성공
- 복잡한 기간 조회 정합성 사례: 자동 테스트 `12개` 성공
- WAR 배포 이식성 사례: 자동 테스트 `10개` 성공
- 업무 규칙 정합성 사례: 자동 테스트 `11개` 성공

각 수치는 해당 프로젝트의 공개 evidence와 테스트 결과에서 다시 확인할 수 있습니다. 장기 SLA, 대규모 사용자 운영, 실제 회사 운영 성능처럼 검증하지 않은 범위는 주장하지 않습니다.

## 대표 작업

### 1. AI가 업무를 대신 결정하지 않도록 통제한 업무 시스템 - OpsMate Local

자연어 구매 요청에 AI가 초안을 제안하되, 권한 확인, 업무 상태 변경, 승인과 발주는 서버가 최종 통제하도록 만든 Spring Boot 프로젝트입니다.

쉽게 말하면 **AI는 도와주지만 중요한 업무 결정은 기존 시스템의 규칙과 사람의 승인 안에서만 일어나도록 만든 것**입니다.

기술적으로는 다음을 검증했습니다.

- 역할과 상태에 따른 승인·반려·발주 통제
- 중복 발주 방지와 감사 이벤트 기록
- 잘못된 모델 결과와 모델 장애 시 저장 전 안전 중단
- 사용자별 session workspace 격리와 rate limit
- PostgreSQL 역할 분리, Docker network와 외부 노출 제한
- 실제 로컬 LLM E2E와 public HTTPS 경계 검증
- normal close, same-digest reopen, emergency close와 recovery 절차

[프로젝트 설명과 코드](02_projects/opsmate-local/README.md) · [실제 모델 E2E 증거](02_projects/opsmate-local/docs/REAL_MODEL_E2E_EVIDENCE.md) · [공개 배포 E2E 증거](02_projects/opsmate-local/docs/PUBLIC_DEPLOYMENT_E2E_EVIDENCE.md)

### 2. 자연어 질문을 데이터 조회로 연결 - Text2SQL / NL2SQL

사용자의 자연어 질문을 SQL로 변환하고 데이터베이스 조회 결과로 연결하는 서비스를 구현·검증했습니다.

단순히 LLM을 호출하는 것이 아니라 SQL 문법, 실행 가능성, 업무 정답 여부를 따로 확인하고 여러 모델의 결과를 비교하는 방식으로 접근했습니다.

- Python / FastAPI 기반 API
- Text2SQL/NL2SQL, SQL 검증과 실행
- validation set과 다중 모델 비교
- 결과 기록과 실패 유형 분류

### 3. 기존 시스템의 문제를 작게 재현하고 검증 - Engineering Case Studies

회사 코드를 공개하지 않고, 실제 업무에서 다뤘던 문제를 일반화한 합성 샘플로 다시 구현해 판단 과정과 테스트를 보여줍니다.

| 문제 | 쉽게 설명하면 | 공개 검증 |
|---|---|---:|
| [사용자 로그인과 권한 통합](03_portfolio/case-studies/spring-security-auth-bridge.md) | 서로 다른 로그인 방식에서도 사용자와 권한이 잘못 연결되지 않게 함 | 24 tests |
| [복잡한 기간 조회의 데이터 정합성](03_portfolio/case-studies/mybatis-query-correctness.md) | 여러 연도와 월을 조회할 때 빠지거나 겹치는 데이터가 없도록 함 | 12 tests |
| [배포와 복구를 환경에 덜 의존하게 구성](03_portfolio/case-studies/war-deployment-portability.md) | 서버 환경이 달라도 같은 애플리케이션을 안전하게 배포·복구할 수 있게 함 | 10 tests |
| [업무 규칙을 여러 계층에서 일관되게 유지](03_portfolio/case-studies/business-rule-consistency.md) | 사용자 식별과 최신 기준이 화면·API·DB에서 다르게 적용되지 않게 함 | 11 tests |

## AI를 개발에 활용하는 방식

AI를 단순한 코드 자동완성 도구로만 사용하지 않습니다.

```text
문제와 제약 정리
-> 구현 계획
-> AI를 활용한 조사와 구현
-> 자동 테스트와 독립 검토
-> 실제 실행 환경 검증
-> 증거와 문서 갱신
```

사람은 목표, 제약, 허용 범위와 최종 판단을 맡고, AI는 탐색·구현·검토를 가속합니다. 다른 AI나 개발자가 작업을 이어받아도 현재 상태와 검증 기준을 복원할 수 있도록 repository 규칙, 상태 문서와 evidence를 함께 관리합니다.

자세한 방식은 [How I Engineer](HOW_I_ENGINEER.md)에서 설명합니다.

## 실무 경험 하이라이트

- 백엔드 웹 서비스와 API, 데이터베이스 조회와 업무 규칙 구현
- Python/FastAPI 기반 데이터·AI 응용 서비스 구현과 검증
- 데이터 분석 결과를 실제 사용자 화면과 API로 서비스화
- AI Runtime의 작업 공간 격리와 결과물 추적 구조 구현
- 인증·권한·감사와 실패 처리 관점을 반영한 시스템 설계
- Linux, Docker, Tomcat, Nginx와 CI/CD 기반 배포·운영
- 테스트, 로그, 실행 결과와 문서를 연결한 검증 중심 업무 방식

비공개 업무 사례는 회사 자산을 공개하지 않는 범위에서 [기술 사례](03_portfolio/case-studies/README.md)로 정리했습니다.

## 사용하는 기술

기술은 목적에 따라 선택합니다. 아래는 실제 프로젝트에서 사용하거나 공개 샘플로 검증한 주요 도구입니다.

| 영역 | 사용 경험 |
|---|---|
| Backend | Java, Spring Boot, Spring MVC, Spring Security, JPA, MyBatis, Python, FastAPI |
| Data | Oracle, PostgreSQL, SQL, CSV·Excel 처리 |
| AI integration | Text2SQL/NL2SQL, RAG, local LLM adapter, structured output validation, Agent workflow |
| Operations | Linux, Docker, Tomcat, Nginx, Jenkins, GitHub Actions |
| Engineering | 인증·인가, 상태 전이, 멱등성, 트랜잭션, 감사 이벤트, 테스트와 evidence 관리 |

## 테스트 실행

대표 공개 구현은 각 프로젝트의 Maven Wrapper로 재현할 수 있습니다.

```powershell
cd 02_projects\opsmate-local
.\mvnw.cmd -q clean verify

cd ..\case-study-samples\spring-security-auth-bridge
.\mvnw.cmd -q clean verify
```

저장소 전체 링크, 공개 문구와 상태 정합성 검사는 다음 명령으로 확인합니다.

```powershell
python -B -m unittest discover -s tests -p "test_*.py" -v
```

## 공개 범위

이 저장소에는 회사 코드, 고객 데이터, 내부 URL, 접속 정보와 실제 업무 규칙을 포함하지 않습니다. 실무 사례는 본인이 담당한 문제를 일반화해 설명하고, 공개 코드는 합성 데이터로 별도 구현합니다. 검증하지 않은 성능, 장기 운영이나 팀 전체 성과를 개인 성과로 확대해서 표현하지 않습니다.
