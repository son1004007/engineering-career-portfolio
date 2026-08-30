# Career Summary

손기석은 **업무 요구사항을 데이터, 권한, 처리 상태와 실패 조건이 명확한 백엔드 시스템으로 구현하고, AI 기능을 서버 검증과 실제 실행 근거 안에서 연결하는 Backend Engineer**입니다.

상위 역할은 Backend Engineer로 명확히 두되 특정 언어나 프레임워크 하나로 제한하지 않습니다. Java/Spring, Python/FastAPI, SQL, Docker와 LLM은 실제 업무 문제에 맞게 선택해 온 구현 도구입니다.

## 어떤 일을 해왔는가

공공 및 데이터 분석 서비스 프로젝트에서 다음 범위를 다뤘습니다.

- 웹 애플리케이션과 API 구현
- Oracle/PostgreSQL 기반 데이터 조회와 처리
- 분석 결과의 화면/API 서비스화
- Python/FastAPI 기반 Text2SQL/NL2SQL 기능과 다중 모델 검증
- AI 작업의 사용자별 공간 분리와 결과 추적 구성요소
- 인증, 권한, 감사와 실패 처리
- Linux/Docker 기반 실행환경과 배포, 운영
- 테스트, 실행 결과와 문서를 연결한 검증

## 일을 보는 방식

단순히 요청된 기능을 만드는 것보다 다음을 중요하게 봅니다.

1. 해결해야 할 문제와 제약을 먼저 정리합니다.
2. 화면, API, DB, batch와 운영까지 실제 영향 범위를 봅니다.
3. AI가 제안할 일과 서버 또는 사람이 책임질 일을 분리합니다.
4. 잘못된 요청이나 결과는 중요한 처리로 이어지지 않게 차단합니다.
5. 구현 완료 여부를 테스트와 실제 실행 결과로 확인합니다.
6. 다른 개발자나 AI가 이어받을 수 있도록 상태와 근거를 문서화합니다.

## 핵심 역량

### 백엔드 시스템 설계

모호한 요구사항을 사용자 흐름, 상태, 데이터, 권한, 실패 조건과 완료 기준으로 구체화하고 API와 DB 구조로 연결합니다.

### 백엔드와 데이터 연결

업무 규칙을 API와 DB에 구현하고, 분석 결과나 AI 출력을 실제 사용자 기능으로 연결합니다.

### AI 기능 통합

LLM 결과를 그대로 업무 상태 변경이나 데이터 저장으로 사용하지 않습니다. AI가 제안할 범위와 서버 검증, 사람 승인 범위를 나누고 실패 시 안전하게 중단되도록 설계합니다.

### 검증과 운영

정상 동작뿐 아니라 실패, 권한, 중복, 외부 연계 장애와 배포, 복구 경계를 확인합니다.

### 보안과 통제 관점

정보보안 경험을 바탕으로 인증, 인가, 감사, 최소 권한과 안전한 실패 처리를 백엔드 설계에 함께 적용합니다.

## 공개 포트폴리오가 보여주는 것

- `OpsMate Local`: AI 초안과 기존 업무 규칙, 사람 승인을 안전하게 연결한 백엔드 프로젝트
- Text2SQL/NL2SQL 경험: Python/API/데이터/LLM 검증 역량
- 독립 Case Study: 인증, 데이터 정합성, 배포와 복구, 업무 규칙 일관성 문제를 합성 코드와 테스트로 재현
- `HOW_I_ENGINEER.md`: 문제 정의 -> AI 활용 탐색과 구현 -> 테스트 -> 실제 실행 검증 -> 결과와 한계 기록으로 이어지는 개발 방식

## 사용 기술

- Backend: Java, Spring Boot, Python, FastAPI
- Data: Oracle, PostgreSQL, SQL
- AI integration: Text2SQL/NL2SQL, LLM integration, structured output validation, RAG/Agent patterns
- Operations: Linux, Docker, Tomcat, Nginx, Jenkins, GitHub Actions
- Engineering: authentication, authorization, transaction, state, audit, fail-closed, testing

## 한 줄 요약

**백엔드 시스템을 중심으로 데이터, AI, 보안과 운영을 연결하고, 실제 실패 조건과 실행 결과까지 검증하는 엔지니어입니다.**
