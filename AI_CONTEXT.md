# AI Context for Engineering Career Portfolio

> 이 저장소 URL만 받은 AI는 `README.md`, `HOW_I_ENGINEER.md`, 이 문서, [`WORKS.md`](WORKS.md), [`PORTFOLIO_COMPLETION_PLAN.md`](PORTFOLIO_COMPLETION_PLAN.md), [`03_portfolio/portfolio-strategy.md`](03_portfolio/portfolio-strategy.md), [`03_portfolio/case-study-index.md`](03_portfolio/case-study-index.md), [`03_portfolio/evidence-index.md`](03_portfolio/evidence-index.md)를 먼저 읽습니다.

- 기준일: `2026-08-31`
- 공개 범위: `public`
- 공개 사이트: [GitHub Pages 포트폴리오](https://son1004007.github.io/engineering-career-portfolio/)
- 역할: 기술 방향, 구현 샘플, 공개 가능한 경력과 기술 근거

## 핵심 정체성

상위 공개 역할은 다음입니다.

> Backend Engineer - AI Integration & Reliable Systems

한국어로는 다음과 같이 해석합니다.

> 업무 요구사항을 데이터, 권한, 처리 상태와 실패 조건이 명확한 백엔드 시스템으로 구현하고, AI 기능을 서버 검증과 실제 실행 근거 안에서 안전하게 연결하는 엔지니어

시장 분류는 Backend Engineer로 고정합니다. Java/Spring과 Python/FastAPI는 모두 구현 도구이며, 현재 공개 evidence는 서로 다른 문제를 보완적으로 증명합니다.

- Java/Spring 사례는 인증·권한, 데이터 정합성, 배포·복구, 업무 규칙 일관성을 독립 합성 샘플로 보여줍니다.
- Python/FastAPI는 `Text2SQL Workspace`에서 멀티사용자 API, SQL policy, PostgreSQL read-only 실행과 result-based evaluation을 독립 공개 구현과 CI/runtime evidence로 보여줍니다.
- OpsMate Local은 Spring 기반 업무 흐름에 실제 LLM을 통합하고 서버 규칙·사람 승인·배포 및 네트워크 경계를 검증한 대표 AI integration evidence입니다.

ML 모델 연구자나 파인튜닝 전문가로 포지셔닝하지 않습니다. AI 역량은 LLM 기능을 기존 백엔드 시스템에 통합하고 평가하며, 모델 실패가 중요한 업무 처리로 이어지지 않게 만드는 능력을 의미합니다.

`Platform Engineer`는 상위 title로 사용하지 않습니다. Linux, Docker, Nginx, Tomcat, CI/CD와 배포 경험은 operations evidence지만 IaC, Kubernetes, 내부 개발자 플랫폼, 중앙 observability와 대규모 멀티환경 운영을 검증한 것으로 확대하지 않습니다.

## 공개 문서 작성 원칙

HR, 채용담당자와 엔지니어가 함께 읽는 구조를 사용합니다.

```text
먼저: 어떤 문제를 해결했고 무엇이 실제로 동작하는지
그 다음: 어떤 실패와 권한 경계를 확인했는지
마지막: 어떤 기술, 테스트와 실행 근거가 있는지
```

전문 용어는 유지하되 전문 용어를 알아야만 내용을 이해할 수 있게 쓰지 않습니다.

AI 관련 용어도 같은 원칙을 적용합니다. `Agent`, `RAG`, `context engineering`, `Agentic AI Runtime`, `provenance` 같은 용어는 구체적인 구현·검증보다 앞에 두지 않습니다. 직접 공개 evidence가 약한 용어는 첫 화면 기술 목록에서 제외합니다.

첫 화면에서는 작은 표본의 p95나 raw test count보다 다음을 우선합니다.

- 어떤 사용자 흐름을 실제로 실행했는가
- 어떤 권한·실패 조건을 차단했는가
- 어떤 배포/runtime 경계를 확인했는가
- 무엇은 아직 검증하지 않았는가

## 포트폴리오 구성

### 1. Controlled AI Integration

`OpsMate Local`은 AI가 업무를 대신 결정하게 하지 않고, 기존 서버 규칙과 사람 승인 안에서 초안 생성 기능을 사용하는 대표 백엔드 프로젝트입니다.

주로 증명하는 것:

- 업무 규칙과 AI 역할 분리
- 권한, 상태 전이, 중복 방지
- 잘못된 모델 결과의 안전 중단
- 사용자 작업과 network boundary 분리
- 실제 모델과 public HTTPS 경계 검증
- 서비스 close/reopen과 복구 절차

현재 verified boundary:

- 실제 `gemma3:12b` 모델로 9개 핵심 합성 업무 시나리오 E2E 전건 성공
- Synology internal bounded E2E PASS
- 실제 Internet HTTPS에서 사용자 작업 분리, rate limit, DB/model 비노출, lifecycle PASS
- 최종 runtime `CLOSED`

세부 응답시간과 project gate는 상세 evidence에서 지원 근거로 유지하지만 일반적인 성능/SLA 증거로 확대하지 않습니다.

### 2. Data and AI Service Integration

`Text2SQL Workspace`는 회사 코드와 독립된 공개 Python/FastAPI 프로젝트로, 자연어 질문을 데이터 조회로 연결할 때 모델 출력과 데이터베이스 권한을 모두 통제하는 방식을 보여줍니다.

현재 `sample-verified` 경계:

- Python / FastAPI API
- synthetic signed identity와 사용자별 workspace/query isolation
- replaceable Text2SQL model interface와 deterministic fixture model
- 자연어 질문 -> SQL candidate -> SQLGlot policy validation -> read-only execution -> result
- generation / validation / execution / correctness 분리
- SQL string equality가 아닌 결과 기반 correctness evaluation
- single-statement, SELECT-only, table allowlist 정책
- unsafe/write SQL은 executor 호출 전에 차단
- PostgreSQL 17 Docker runtime과 전용 analytics reader
- 동일 reader의 `SELECT` 성공과 `INSERT` 실패를 직접 검증
- explicit read-only transaction, row limit, statement timeout
- FastAPI host 노출은 loopback으로 제한하고 PostgreSQL host port는 publish하지 않음
- public project main CI에서 Python test와 Docker/PostgreSQL E2E 모두 PASS (`2026-08-31`)
- 공개 disclosure review 완료

현재 한계:

- production authentication / external IdP는 미검증
- external/real LLM E2E와 statistically meaningful model-quality metrics는 미검증
- arbitrary production DB connector, concurrency/load/SLA/large-user operation은 미검증

포트폴리오의 Pages publication gate가 끝나기 전에는 `published`가 아니라 `sample-verified`로 해석합니다.

### 3. Engineering Case Studies

현재 published 독립 재현 사례:

- 사용자 로그인과 권한 통합
- 복잡한 기간 조회의 데이터 정합성
- 배포와 복구의 이식성
- 업무 규칙의 일관성

현재 publication 진행 중인 독립 재현 사례:

- Python/FastAPI Text2SQL 안전 실행과 평가 경계 (`CS-AI-01`, `sample-verified`)

각 사례는 회사 코드와 독립된 합성 샘플이며 실제 회사 전체 시스템 검증을 뜻하지 않습니다.

### 4. Engineering Method

[`HOW_I_ENGINEER.md`](HOW_I_ENGINEER.md)는 다음 흐름을 설명합니다.

```text
문제와 제약 정의
-> 설계
-> AI를 활용한 탐색과 구현
-> 자동 테스트
-> 실제 실행 검증
-> 결과와 한계 기록
```

AI가 코드를 작성했다는 사실보다 결과를 다시 테스트하고 실행해 확인할 수 있다는 점을 중요하게 봅니다.

## 독립 리뷰 상태

2026-08-30 공개 main을 Codex와 Gemini 계열 agent가 read-only로 독립 검토했습니다.

공통 결론:

- `Backend Engineer` 중심 포지셔닝은 유지
- 전면 재포지셔닝보다 `MINOR_REVISE` 수준의 claim/evidence 정렬 필요
- AI·서버·사람의 책임 분리와 evidence discipline은 유지
- Python/FastAPI 공개 재현 evidence가 Java/Spring보다 약했던 공백을 하나의 강한 독립 사례로 보강할 것
- unsupported AI buzzword와 first-screen 세부 성능 수치를 줄일 것

P0 문구 정렬은 반영했고, Python/FastAPI evidence gap은 `Text2SQL Workspace`의 독립 구현과 runtime verification으로 보강했습니다. 현재 남은 단계는 portfolio regression과 Pages publication이며, 그 이후 같은 독립 review path로 재검토할 수 있습니다.

## 공개 Case Study 상태

| 사례 | 상태 | 검증 초점 |
|---|---|---|
| 사용자 로그인과 권한 통합 | `published` | 인증 위조, 권한 오류, session 경계 |
| 복잡한 기간 조회 데이터 정합성 | `published` | 기간 경계, 중복, 누락 |
| 배포·복구 이식성 | `published` | 설정 차이, health failure, rollback |
| 업무 규칙 일관성 | `published` | identity/reference-period 기준 불일치 |
| Python/FastAPI Text2SQL 안전 실행·평가 | `sample-verified` | workspace isolation, SQL safety, PostgreSQL read-only, bounded execution, evaluation boundary |

## 공개 경계

회사 원본 소스, 실제 query/schema/data, 내부 식별자와 접속정보는 공개하지 않습니다.

```text
권한 있는 환경에서 본인 귀속과 범위 확인
-> 문제와 제약 비식별화
-> 필요하면 합성 데이터로 독립 구현
-> 테스트
-> 공개 가능한 claim만 게시
```

## 이 저장소만으로 하면 안 되는 판단

- 특정 언어 숙련도를 저장소 파일 수로 단정
- 회사·팀 전체 성과를 개인 성과로 해석
- bounded E2E를 장기 production 운영/SLA로 확대
- 합성 샘플을 실제 회사 운영환경 검증으로 확대
- 프로덕션 트래픽과 운영 규모
- 아직 검증하지 않은 Kafka/Redis/Kubernetes 실전 운영
- deterministic Text2SQL fixture 성공을 실제 외부 LLM 정확도 100%로 해석

## 증거 라벨

- `implemented`: 필요한 코드가 존재
- `tested-file-present`: 테스트 파일은 있으나 최근 성공 실행 미확인
- `tested-component`: 명시 구성요소 테스트 산출물이 존재
- `source-reviewed`: 권한 있는 비공개 원본에서 귀속·범위를 확인했으나 공개 재현 검증은 아직 없음
- `sample-verified`: 독립 공개 샘플의 최근 테스트 성공 기록이 있음
- `published`: 독립 공개 샘플과 publication gate까지 확인
- `partial`: 필요한 구성요소가 부족
- `planned` / `pending`: 착수 전 또는 문서상 계획

완료 판정은 설명이 아니라 test/build/runtime/Pages evidence를 우선합니다.
