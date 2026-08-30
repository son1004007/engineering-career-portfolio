# Backend Engineer - AI Integration & Reliable Systems

[공개 포트폴리오 보기](https://son1004007.github.io/engineering-career-portfolio/)

업무 요구사항을 **데이터, 권한, 처리 상태와 실패 조건이 명확한 백엔드 시스템으로 구현**합니다.

AI 기능은 결과를 그대로 신뢰하지 않고 서버의 검증과 사람의 승인 안에서 실제 업무와 연결합니다. Java/Spring과 Python/FastAPI를 업무 특성에 따라 사용해 왔고, 현재 공개 재현 샘플은 Java/Spring 쪽이 더 강합니다. SQL, Docker와 LLM은 필요한 문제를 해결하기 위한 도구로 사용하며, 자동 테스트와 실제 실행 환경 검증으로 동작 범위와 실패 조건을 확인합니다.

## 제가 잘하는 일

| 역량 | 쉽게 설명하면 | 기술적으로는 |
|---|---|---|
| 업무 요구사항을 시스템으로 구조화 | 모호한 요청을 구현 범위, 처리 단계와 완료 조건으로 정리합니다 | system boundary, state, transaction, acceptance criteria |
| 백엔드와 데이터 연결 | 화면이나 AI 기능이 실제 업무 데이터와 안전하게 연결되도록 만듭니다 | API, SQL, Oracle, PostgreSQL, Spring Boot, FastAPI |
| AI 기능 통합 | AI가 제안할 일과 서버 또는 사람이 최종 책임질 일을 분리합니다 | LLM integration, structured output validation, Text2SQL/NL2SQL |
| 잘못된 처리 차단 | 권한이 없거나 입력과 결과가 잘못되면 중요한 처리가 진행되지 않게 합니다 | RBAC, validation, idempotency, fail-closed |
| 실제 동작 검증 | 정상 상황뿐 아니라 실패, 권한, 중복과 외부 장애까지 확인합니다 | unit/integration/E2E, regression, eval |
| 배포와 복구 | 개발한 시스템을 실행하고 상태를 확인하며 문제가 생기면 복구할 수 있게 합니다 | Linux, Docker, Tomcat, Nginx, CI/CD, health check, rollback |

## 공개 검증으로 확인한 것

- `OpsMate Local`: 실제 Ollama `gemma3:12b` 모델로 9개 핵심 업무 시나리오 E2E 전건 성공
- `OpsMate Local`: `2026-08-29` 실제 Internet HTTPS 경로에서 사용자 작업 분리, rate limit, DB/model 비노출, close/reopen 검증
- 인증과 권한, 데이터 정합성, 배포와 복구, 업무 규칙 일관성 문제를 회사 코드와 독립된 합성 샘플로 재현하고 정상, 실패, 경계 조건을 자동 테스트로 검증

각 결과는 해당 프로젝트의 공개 evidence와 테스트 결과에서 다시 확인할 수 있습니다. 실제 모델 응답시간과 프로젝트 gate 같은 세부 수치는 evidence 문서에 남기고, 장기 SLA, 대규모 사용자 운영, 실제 회사 운영 성능처럼 검증하지 않은 범위는 주장하지 않습니다.

## 대표 작업

### 1. AI가 업무를 대신 결정하지 않도록 통제한 백엔드 시스템 - OpsMate Local

자연어 구매 요청에 AI가 초안을 제안하되, 권한 확인, 업무 상태 변경, 승인과 발주는 서버가 최종 통제하도록 만든 Spring Boot 프로젝트입니다.

쉽게 말하면 **AI는 도와주지만 중요한 업무 결정은 기존 시스템의 규칙과 사람의 승인 안에서만 일어나도록 만든 것**입니다.

기술적으로는 다음을 검증했습니다.

- 역할과 상태에 따른 승인, 반려, 발주 통제
- 중복 발주 방지와 감사 이벤트 기록
- 잘못된 모델 결과와 모델 장애 시 저장 전 안전 중단
- 사용자별 session workspace 격리와 rate limit
- PostgreSQL 역할 분리, Docker network와 외부 노출 제한
- 실제 로컬 LLM E2E와 public HTTPS 경계 검증
- normal close, same-digest reopen, emergency close와 recovery 절차

[프로젝트 설명과 코드](02_projects/opsmate-local/README.md) | [실제 모델 E2E 증거](02_projects/opsmate-local/docs/REAL_MODEL_E2E_EVIDENCE.md) | [공개 배포 E2E 증거](02_projects/opsmate-local/docs/PUBLIC_DEPLOYMENT_E2E_EVIDENCE.md)

### 2. 자연어 질문을 데이터 조회로 연결 - Text2SQL / NL2SQL

사용자의 자연어 질문을 SQL로 변환하고 데이터베이스 조회 결과로 연결하는 서비스를 구현하고 검증했습니다.

단순히 LLM을 호출하는 것이 아니라 SQL 문법, 실행 가능성, 업무 정답 여부를 따로 확인하고 여러 모델의 결과를 비교하는 방식으로 접근했습니다.

- Python / FastAPI 기반 API
- Text2SQL/NL2SQL, SQL 검증과 실행
- validation set과 다중 모델 비교
- 결과 기록과 실패 유형 분류

이 항목은 비식별 실무 evidence가 중심이며, Java/Spring 사례와 같은 수준의 독립 공개 Python 재현 샘플은 다음 보강 대상으로 남겨 두고 있습니다.

### 3. 기존 시스템의 문제를 작게 재현하고 검증 - Engineering Case Studies

회사 코드를 공개하지 않고, 실제 업무에서 다뤘던 문제를 일반화한 합성 샘플로 다시 구현해 판단 과정과 테스트를 보여줍니다.

| 문제 | 쉽게 설명하면 | 검증 초점 |
|---|---|---|
| [사용자 로그인과 권한 통합](03_portfolio/case-studies/spring-security-auth-bridge.md) | 서로 다른 로그인 방식에서도 사용자와 권한이 잘못 연결되지 않게 함 | 인증 위조, 권한 오류, session 경계 |
| [복잡한 기간 조회의 데이터 정합성](03_portfolio/case-studies/mybatis-query-correctness.md) | 여러 연도와 월을 조회할 때 빠지거나 겹치는 데이터가 없도록 함 | 기간 경계, 중복, 누락 |
| [배포와 복구를 환경에 덜 의존하게 구성](03_portfolio/case-studies/war-deployment-portability.md) | 서버 환경이 달라도 같은 애플리케이션을 안전하게 배포하고 복구할 수 있게 함 | 설정 분리, health check, rollback |
| [업무 규칙을 여러 계층에서 일관되게 유지](03_portfolio/case-studies/business-rule-consistency.md) | 사용자 식별과 최신 기준이 화면, API, DB에서 다르게 적용되지 않게 함 | 계층 간 규칙 일관성, 잘못된 상태 차단 |

## AI를 개발에 활용하는 방식

AI는 조사, 구현과 리뷰를 빠르게 만드는 도구로 사용합니다. 완료 여부는 AI의 설명이 아니라 테스트와 실제 실행 결과로 판단합니다.

```text
문제와 제약 정리
-> 구현 계획
-> AI를 활용한 탐색과 구현
-> 자동 테스트와 독립 검토
-> 실제 실행 환경 검증
-> 결과와 한계 기록
```

사람은 목표, 제약, 허용 범위와 최종 판단을 맡고, AI는 탐색, 구현과 검토를 가속합니다. 다른 AI나 개발자가 작업을 이어받아도 현재 상태와 검증 기준을 복원할 수 있도록 repository 규칙, 상태 문서와 evidence를 함께 관리합니다.

자세한 방식은 [How I Engineer](HOW_I_ENGINEER.md)에서 설명합니다.

## 실무 경험 하이라이트

- 백엔드 웹 서비스와 API, 데이터베이스 조회와 업무 규칙 구현
- Python/FastAPI 기반 데이터와 AI 응용 서비스 구현과 검증
- 데이터 분석 결과를 실제 사용자 화면과 API로 서비스화
- AI 작업의 사용자별 공간 분리와 결과물 추적 구조 구현
- 인증, 권한, 감사와 실패 처리 관점을 반영한 시스템 설계
- Linux, Docker, Tomcat, Nginx와 CI/CD 기반 배포와 운영
- 테스트, 로그, 실행 결과와 문서를 연결한 검증 중심 업무 방식

비공개 업무 사례는 회사 자산을 공개하지 않는 범위에서 [기술 사례](03_portfolio/case-studies/README.md)로 정리했습니다.

## 사용하는 기술

기술은 목적에 따라 선택합니다. 아래는 실제 프로젝트에서 사용하거나 공개 샘플로 검증한 주요 도구입니다.

| 영역 | 사용 경험 |
|---|---|
| Backend | Java, Spring Boot, Spring MVC, Spring Security, JPA, MyBatis, Python, FastAPI |
| Data | Oracle, PostgreSQL, SQL, CSV/Excel 처리 |
| AI integration | Text2SQL/NL2SQL, LLM integration, structured output validation |
| Operations | Linux, Docker, Tomcat, Nginx, Jenkins, GitHub Actions |
| Engineering | 인증, 권한, 상태 전이, 멱등성, 트랜잭션, 감사 이벤트, 테스트와 evidence 관리 |

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