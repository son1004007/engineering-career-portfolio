# Portfolio Overview

이 저장소는 손기석이 **어떤 기술을 아는지보다 어떤 문제를 어떻게 해결하는지**를 빠르게 보여주기 위한 공개 엔지니어 포트폴리오입니다.

## 나는 어떤 엔지니어인가

손기석은 업무 문제를 시스템으로 구조화하고, 적절한 기술과 AI를 활용해 구현하며, 테스트와 실제 실행 결과로 검증하는 소프트웨어/백엔드/플랫폼 엔지니어입니다.

쉽게 말하면 다음과 같은 일을 합니다.

- 복잡한 요청을 실제 구현 가능한 범위로 정리합니다.
- 데이터와 업무 규칙을 사용자 기능으로 연결합니다.
- AI가 도와줄 일과 서버 또는 사람이 책임질 일을 나눕니다.
- 권한이 없거나 결과가 잘못되면 시스템이 안전하게 중단되도록 만듭니다.
- 배포, 장애, 복구까지 고려해 실제로 운영 가능한 형태로 마무리합니다.
- 만들었다는 설명보다 테스트와 실행 evidence를 남깁니다.

Java/Spring, Python/FastAPI, SQL, Docker와 LLM은 이러한 일을 하기 위해 사용해 온 도구입니다.

## 핵심 역량

| 역량 | 쉽게 설명하면 | 기술적으로는 |
|---|---|---|
| 문제 구조화 | 무엇을 만들어야 실제 일이 해결되는지 먼저 정리 | boundary, state, transaction, acceptance criteria |
| 백엔드와 데이터 | 업무 규칙과 데이터를 API/화면에 연결 | Spring Boot, FastAPI, SQL, Oracle, PostgreSQL |
| AI 활용 | AI 초안·검색·질의 기능을 기존 업무 규칙 안에 연결 | LLM, Agent, RAG, Text2SQL/NL2SQL |
| 검증 | 정상·실패·권한·중복 상황을 테스트 | unit/integration/E2E, regression, eval |
| 보안과 통제 | 잘못된 사용자나 결과가 중요한 처리로 이어지지 않게 차단 | RBAC, session, audit, fail-closed |
| 운영과 복구 | 실제 서버 배포와 상태 확인, 중단·복구를 고려 | Linux, Docker, CI/CD, health check, rollback |

## 숫자로 확인된 공개 증거

- `OpsMate Local` 실제 모델 E2E `9/9` 성공
- 실제 모델 응답 관측 p95 `21,076ms`, 프로젝트 gate `<= 30,000ms`
- `2026-08-29` 실제 Internet HTTPS 경로에서 session isolation, rate limit, DB/model 비노출과 close/reopen 경계 검증
- 사용자 로그인과 권한 통합 독립 샘플: 자동 테스트 `24개`
- 복잡한 기간 조회 정합성 독립 샘플: 자동 테스트 `12개`
- 배포와 복구 이식성 독립 샘플: 자동 테스트 `10개`
- 업무 규칙 일관성 독립 샘플: 자동 테스트 `11개`

## 포트폴리오 구성

### 1. Controlled AI Integration

`OpsMate Local`은 AI가 구매 요청 초안을 도와주되, 중요한 업무 결정은 서버 규칙과 사람 승인 안에서만 일어나도록 만든 대표 프로젝트입니다.

주로 보여주는 것:

- AI와 기존 업무 시스템의 역할 분리
- 권한과 상태 통제
- 모델 실패 시 안전한 처리
- session/network isolation
- 실제 모델과 public HTTPS 검증
- 서비스 중단과 복구 절차

### 2. Data / AI Service Integration

Text2SQL/NL2SQL과 AI Runtime 업무 evidence를 통해 자연어 질문, SQL, DB 조회, 모델 평가와 결과 추적을 연결한 경험을 보여줍니다.

주로 보여주는 것:

- Python/FastAPI
- SQL validation and execution
- validation set
- multi-model comparison
- workspace isolation
- artifact/provenance traceability

### 3. Engineering Problem Case Studies

기존 업무 문제를 회사 코드 없이 일반화한 합성 샘플로 재현합니다.

| 문제 | 검증 |
|---|---:|
| 사용자 로그인과 권한을 안전하게 통합 | 24 tests |
| 복잡한 기간 조회에서 데이터 정합성 유지 | 12 tests |
| 환경이 달라도 배포하고 복구할 수 있는 구조 | 10 tests |
| 사용자 식별과 업무 기준을 여러 계층에서 일관되게 유지 | 11 tests |

각 사례는 framework 이름보다 문제, 제약, 판단과 검증을 먼저 설명합니다.

### 4. Engineering Method

[`../HOW_I_ENGINEER.md`](../HOW_I_ENGINEER.md)는 다음 개발 방식을 설명합니다.

```text
문제와 제약 정의
-> 설계
-> AI를 활용한 탐색과 구현
-> 자동 테스트
-> 실제 실행 검증
-> evidence와 문서 갱신
```

AI가 작성한 결과도 그대로 신뢰하지 않고 테스트와 runtime evidence로 확인하는 것을 중요하게 봅니다.

## 기술을 어떻게 보는가

기술은 목적에 맞춰 선택합니다.

- Java/Spring: 업무 규칙, 트랜잭션, 인증·권한과 엔터프라이즈 백엔드
- Python/FastAPI: 데이터 처리와 AI 응용 API
- SQL/DB: 데이터 정합성과 실제 업무 조회
- Docker/Linux: 실행 환경, 배포와 복구
- LLM/Agent: 자연어 이해, 초안, 검색·질의 지원

특정 언어에 역할을 가두기보다 문제 해결과 검증에 필요한 도구를 조합합니다.

## 이 저장소에서 보면 좋은 문서

1. [`../README.md`](../README.md)
2. [`../HOW_I_ENGINEER.md`](../HOW_I_ENGINEER.md)
3. [`portfolio-strategy.md`](portfolio-strategy.md)
4. [`case-study-index.md`](case-study-index.md)
5. [`../01_profile/career-summary.md`](../01_profile/career-summary.md)
6. [`../01_profile/core-strengths.md`](../01_profile/core-strengths.md)
7. [`../01_profile/career-direction.md`](../01_profile/career-direction.md)
8. [`evidence-index.md`](evidence-index.md)
9. [`../evidence/company-github/README.md`](../evidence/company-github/README.md)
10. [`../02_projects/README.md`](../02_projects/README.md)

## 한 줄 정리

손기석은 **언어나 프레임워크보다 문제 해결, 시스템 설계, AI 활용, 검증과 운영을 연결해 실제로 작동하는 서비스를 만드는 엔지니어**입니다.
