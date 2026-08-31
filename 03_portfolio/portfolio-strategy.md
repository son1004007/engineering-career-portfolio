# Portfolio Content Strategy

- 결정일: `2026-08-03`
- 상태 갱신일: `2026-08-31`
- 상태: `approved`
- 목적: HR이 첫 화면에서 역할을 즉시 이해하고, 엔지니어는 공개 코드와 검증 근거로 깊이를 확인할 수 있게 구성

## 최종 공개 포지셔닝

이 포트폴리오의 상위 정체성은 다음입니다.

> Backend Engineer - AI Integration & Reliable Systems

한국어로 풀면 다음과 같습니다.

> 업무 요구사항을 데이터, 권한, 처리 상태와 실패 조건이 명확한 백엔드 시스템으로 구현하고, AI 기능을 서버 검증과 실제 실행 증거 안에서 안전하게 연결하는 엔지니어

핵심 원칙은 `특정 언어에 갇히지 않는다`와 `역할을 모호하게 만들지 않는다`를 동시에 만족하는 것입니다.

- 시장에서 바로 분류되는 주 역할은 `Backend Engineer`로 고정합니다.
- 차별점은 `AI Integration`과 `Reliable Systems`로 설명합니다.
- Java/Spring, Python/FastAPI, SQL, Docker와 LLM은 역할이 아니라 구현 도구입니다.
- Data, Security, Operations 경험은 백엔드 시스템을 더 안정적으로 구현하는 인접 역량으로 보여줍니다.
- `Software Engineer`는 너무 일반적이므로 headline에 병렬로 두지 않습니다.
- `Platform Engineer`는 현재 공개 증거보다 역할 범위가 넓으므로 headline에서 사용하지 않습니다.

ML 모델 연구, 학습 또는 파인튜닝을 핵심 정체성으로 두지 않습니다. AI는 기존 백엔드 시스템에 통합하고 검증하는 기능으로 다룹니다.

## 독립 검토 반영

`2026-08-30`에 Codex와 Gemini 계열 에이전트가 당시 공개 main을 read-only로 독립 검토했습니다.

공통 판단은 다음과 같습니다.

- 공개 증거는 Backend 쪽이 가장 강합니다.
- `Software / Backend / Platform Engineer`는 첫 화면에서 역할을 넓게 보이게 해 HR 분류를 어렵게 합니다.
- `Platform Engineer`를 주장하려면 IaC, Kubernetes, 내부 개발자 플랫폼, 반복 가능한 인프라 자동화, 관측성과 대규모 운영 같은 직접 증거가 더 필요합니다.
- AI 통합은 OpsMate와 Text2SQL 검증으로 실질적인 engineering capability가 보입니다.
- 단순 테스트 개수보다 실패 조건, 권한 경계, 실제 모델과 실제 배포 환경에서 무엇을 검증했는지가 더 강한 evidence입니다.
- 쉬운 설명을 먼저 두고 전문 용어와 구현 세부를 뒤에 두는 2단 구조는 유지합니다.
- Python/FastAPI 공개 재현 evidence가 당시 Java/Spring보다 약했으므로 하나의 독립 실행 사례를 보강하는 것이 유효합니다.

이후 Python/FastAPI 공백은 `Text2SQL Workspace`의 독립 공개 구현, PostgreSQL/Docker runtime verification과 portfolio Pages publication으로 보강했습니다. 포지셔닝 자체는 바꾸지 않고 claim과 evidence를 정렬합니다.

## 독자 우선순위

공개 문서는 아래 독자가 같은 페이지를 읽을 수 있어야 합니다.

1. HR / recruiter
2. hiring manager
3. backend engineer
4. data / AI integration / security / operations engineer

문서는 2단 구조로 작성합니다.

```text
1단:
왜 중요한 문제인지, 무엇을 했는지, 어떤 결과가 있었는지 쉽게 설명

2단:
system boundary, transaction, RBAC, fail-closed 같은 전문 용어와 구현, 검증 근거 제공
```

전문 용어를 없애지 않습니다. 대신 전문 용어를 이해해야만 성과를 알 수 있게 쓰지 않습니다.

## 첫 화면에서 보여줄 순서

README와 GitHub Pages 홈은 다음 순서를 유지합니다.

```text
Backend Engineer identity
-> 해결하는 문제와 차별점
-> 실제 검증 evidence
-> 대표 프로젝트
-> 실무 문제 해결 Case Study
-> AI를 활용한 개발 방식
-> 사용하는 기술
```

기술 목록은 역할, 문제와 evidence 뒤에 둡니다.

## Capability Map

### 1. Backend System Design

쉽게 설명하면:

- 요청을 그대로 코딩하지 않고 실제 문제, 사용자 흐름, 제약과 완료 기준으로 정리
- API, DB, 상태 변경과 실패 조건을 하나의 백엔드 흐름으로 설계

기술적으로:

- system boundary
- state transition
- transaction
- acceptance criteria
- failure handling

### 2. Backend / Data Integration

쉽게 설명하면:

- 업무 규칙과 데이터를 실제 사용자 기능으로 연결
- 분석 결과나 AI 결과를 API와 화면에서 사용할 수 있게 서비스화

기술적으로:

- Java/Spring Boot
- Python/FastAPI
- Oracle/PostgreSQL/SQL
- API and data contract

### 3. AI Integration

쉽게 설명하면:

- AI가 제안할 일과 서버 또는 사람이 최종 책임질 일을 분리
- 잘못된 모델 출력이 중요한 상태 변경이나 데이터 저장으로 이어지지 않게 차단

기술적으로:

- LLM integration
- structured output validation
- Text2SQL/NL2SQL
- RAG/Agent patterns where evidence exists

`context engineering`, `agent workflow`, `Agentic AI Runtime` 같은 용어는 실제 구현 증거보다 앞에 두지 않습니다. 필요한 경우 구체적으로 무엇을 구현하고 검증했는지 설명한 뒤 보조 용어로 사용합니다.

### 4. Verification

쉽게 설명하면:

- 코드가 있다는 사실보다 실제로 맞게 동작하는지 확인
- 정상 상황뿐 아니라 실패, 권한, 중복과 잘못된 입력도 검증

기술적으로:

- unit/integration/E2E
- regression
- AI evaluation
- runtime evidence

### 5. Security by Design

쉽게 설명하면:

- 사용자가 해도 되는 일과 하면 안 되는 일을 시스템이 확인
- 오류나 AI 실패가 중요한 업무 처리로 이어지지 않게 차단
- 누가 무엇을 했는지 다시 확인할 수 있게 기록

기술적으로:

- authentication / authorization
- RBAC
- least privilege
- audit
- fail-closed

### 6. Operations / Recovery

쉽게 설명하면:

- 내 PC에서 실행되는 것으로 끝내지 않고 실제 배포, 상태 확인, 중단과 복구까지 고려

기술적으로:

- Linux / Docker
- Tomcat / Nginx
- CI/CD
- health check
- rollback
- runtime verification

## Portfolio Evidence Tracks

포트폴리오는 언어별 프로젝트가 아니라 서로 다른 백엔드 역량을 증명하는 evidence track으로 구성합니다.

### Track A. Controlled AI Integration - OpsMate Local

AI를 기업 업무 트랜잭션에 안전하게 연결하는 대표 프로젝트입니다.

쉽게 설명하면:

> AI는 구매 요청 초안을 도와주지만, 권한 확인, 업무 상태 변경과 발주는 서버 규칙과 사람 승인 안에서만 이루어지게 만든 시스템입니다.

주로 증명하는 것:

- AI와 업무 규칙 책임 분리
- 권한, 상태 전이와 중복 방지
- 모델 장애와 잘못된 출력의 안전 처리
- 사용자 작업과 network boundary 분리
- 실제 모델과 Internet HTTPS E2E
- close/reopen/recovery

현재 공개 검증:

- `2026-08-23`: 실제 Ollama `gemma3:12b` E2E 9/9, 관측 p95 `21,076ms`
- `2026-08-25`: Synology 내부 deployment/network/lifecycle E2E
- `2026-08-29`: 실제 Internet HTTPS, 사용자 작업 분리, rate limit, 비노출, lifecycle E2E

이 결과를 24x7 SLA나 장기 대규모 운영으로 확대 해석하지 않습니다.

### Track B. Data / AI Service Integration - Text2SQL Workspace

회사 코드와 독립된 Python/FastAPI 프로젝트로 자연어 질문을 안전한 데이터 조회 기능으로 연결하는 방식을 보여줍니다.

쉽게 설명하면:

> 모델이 SQL을 만들었다는 이유만으로 실행하지 않고, 서버 정책과 데이터베이스 권한을 모두 통과한 읽기 질의만 실행하며 실제 결과가 기대값과 맞는지 별도로 평가합니다.

주로 증명하는 것:

- Python/FastAPI 기반 멀티사용자 API
- 사용자별 workspace/query ownership
- replaceable Text2SQL model boundary
- SQLGlot 기반 single-statement / SELECT-only / table allowlist
- unsafe SQL의 executor 이전 차단
- read-only, row/time bounded execution
- generation / validation / execution / correctness 상태 분리
- SQL 문자열이 아니라 결과 semantics 기반 evaluation
- PostgreSQL 17 전용 analytics reader와 Docker runtime
- reader `SELECT` 성공 / `INSERT` 실패
- application metadata와 analytics query authority 분리
- loopback-only app host binding과 PostgreSQL host-port 비노출

현재 공개 검증:

- `2026-08-31`: 독립 public project main CI에서 Python test와 Docker/PostgreSQL E2E 모두 PASS
- deterministic evaluation fixture는 2개 bounded case의 pipeline correctness를 검증하며 외부 LLM 정확도 주장이 아님
- 공개 security/disclosure review PASS
- portfolio publication PR regression 8개 job PASS
- portfolio main verify 8개 job + Pages build/deploy PASS
- 현재 상태: `published`

미검증 범위:

- production authentication/external IdP
- external real LLM E2E와 statistically meaningful model-quality metrics
- arbitrary production database connector
- production concurrency/load/SLA/large-user operation

### Track C. Engineering Problem Case Studies

실제 업무 문제를 회사 코드와 독립된 합성 샘플로 재구현합니다.

Case Study는 framework 이름이나 테스트 개수보다 문제와 실패 조건을 먼저 보여줍니다.

| 내부 기술명 | 공개 문제 중심 표현 | 우선 검증할 실패 조건 |
|---|---|---|
| Spring Security 인증 브리지 | 사용자 로그인과 권한을 안전하게 통합 | 잘못된 사용자 식별, 권한 오류 |
| MyBatis 기간 조회 | 복잡한 기간 조회에서 데이터 정합성 유지 | 기간 경계, 중복, 누락 |
| WAR 배포 이식성 | 환경이 달라도 배포하고 복구할 수 있는 구조 | 설정 차이, health failure, rollback |
| 업무 규칙 정합성 | 사용자 식별과 업무 기준을 여러 계층에서 일관되게 유지 | 계층 간 기준 불일치 |
| Text2SQL 안전 실행·평가 | 자연어 질문을 읽기 전용 데이터 조회로 안전하게 연결 | 사용자 작업 혼선, 위험 SQL, DB 쓰기, 결과 오류 |

각 게시물은 다음 질문에 답합니다.

1. 어떤 문제가 있었는가
2. 왜 중요한가
3. 어떤 제약이 있었는가
4. 본인이 담당한 범위는 무엇인가
5. 어떤 대안을 검토했는가
6. 왜 해당 설계를 선택했는가
7. 어떻게 구현하고 검증했는가
8. 결과와 한계는 무엇인가
9. 어떤 기술을 사용했는가

기술은 문제와 결과를 설명한 뒤에 배치합니다.

### Track D. Engineering Method

[`../HOW_I_ENGINEER.md`](../HOW_I_ENGINEER.md)에서 개발 방식을 공개합니다.

```text
문제와 제약 정의
-> 설계
-> AI를 활용한 탐색과 구현
-> 자동 테스트
-> runtime verification
-> 결과와 한계 기록
```

AI 사용 자체보다 다음을 증명하는 것이 목적입니다.

- AI가 바뀌어도 프로젝트 상태와 제약을 복원할 수 있음
- AI 결과를 그대로 신뢰하지 않고 테스트로 검증함
- 사람이 목표, 제한과 최종 판단을 유지함
- 다른 개발자 또는 AI가 작업을 이어받을 수 있음

## Evidence 표현 규칙

첫 화면에서는 테스트 개수 자체를 성과처럼 강조하지 않습니다.

우선순위는 다음과 같습니다.

```text
실제 사용자 또는 모델 흐름에서 무엇이 성공했는가
-> 어떤 실패와 권한 경계를 차단했는가
-> 어떤 실행 환경에서 다시 확인했는가
-> 필요하면 테스트 수와 세부 측정값을 근거로 제공
```

좋은 예:

```text
권한이 없는 사용자의 승인 요청을 서버에서 차단했고,
실제 HTTPS 경로에서도 사용자별 작업이 섞이지 않는지 확인했습니다.
```

세부 evidence에서만 다음처럼 숫자를 추가할 수 있습니다.

```text
자동 테스트 24개 성공
실제 모델 E2E 9/9
p95 21,076ms, gate <= 30,000ms
```

## Platform 제목 사용 기준

향후 아래와 같은 공개 증거가 충분히 쌓이기 전에는 `Platform Engineer`를 headline으로 사용하지 않습니다.

- IaC 기반 반복 가능한 인프라 구성
- Kubernetes 또는 동등한 orchestration 운영
- 내부 개발자 플랫폼 또는 공용 배포/개발 기능
- 중앙 observability와 운영 자동화
- 멀티환경, 장애 대응과 capacity 관련 실제 검증

이 증거가 추가되더라도 시장 분류와 지원 직무를 기준으로 title을 다시 검토합니다.

## 회사 업무 사례 공개 규칙

회사 소유 비공개 저장소는 증거 확인에만 사용합니다.

- 회사 저장소명, 고객명, 내부 URL, 원본 commit message와 파일 경로를 공개하지 않음
- 회사 source, diff, SQL, 설정과 데이터를 복사하지 않음
- 본인 귀속과 담당 범위를 원본에서 먼저 확인
- 게시물은 비식별 문제, 제약, 판단과 검증 중심으로 새로 작성
- 코드를 보여줄 필요가 있으면 합성 데이터와 일반화한 domain으로 독립 재구현
- 원본 검토, 공개 안전성 검토와 재현 코드 검증이 끝나기 전에는 게시 완료로 표시하지 않음

개인 공개 저장소도 게시 전에 credential, 개인정보, 라이선스와 README-구현 일치 여부를 검사합니다.

## 다음 프로젝트를 고르는 기준

다음 Case Study나 프로젝트는 `Java 사례를 하나 더 만든다` 또는 `Python 프로젝트 수를 늘린다`는 방식으로 선택하지 않습니다.

현재 Backend, Java/Spring, Python/FastAPI, AI integration의 핵심 공개 evidence는 역할 분류에 필요한 수준으로 보완되어 있습니다. 따라서 새 프로젝트는 실제 지원 직무에서 필요한 **새로운 capability gap이 확인될 때만** 추가합니다.

후보 우선순위:

```text
1. 실제 장애, 실패와 복구를 더 강하게 보여주는 reliability evidence
2. concurrency, queue/cache, resource-bound 같은 backend scale evidence가 실제 근거와 함께 필요할 경우 독립 재현
3. security automation 또는 access/audit backend evidence
4. 특정 지원 직무에서 분석 UI나 다른 데이터 역량이 명확히 요구될 경우 해당 evidence
```

대규모 트래픽, 분산 시스템이나 platform 경험은 실제 근거 없이 포트폴리오용으로 만들어 경력처럼 표현하지 않습니다.

## 결과물 위치

```text
README.md
HOW_I_ENGINEER.md
01_profile/
03_portfolio/case-studies/
02_projects/case-study-samples/
02_projects/opsmate-local/
evidence/company-github/
```

- `README.md`: HR과 엔지니어가 함께 보는 첫 화면
- `HOW_I_ENGINEER.md`: 백엔드 시스템을 구현하고 검증하는 방식
- `01_profile/`: 정체성, 강점, 방향
- `03_portfolio/case-studies/`: 문제 중심 실무 사례
- `02_projects/`: 실행 가능한 공개 코드와 evidence
- `evidence/company-github/`: 비식별 업무 근거와 claim

## 다음 실행 순서

1. 현재 published evidence와 landing page의 상태·한계를 유지합니다.
2. 첫 화면에서는 AI buzzword와 단순 테스트 숫자를 줄이고 실제 실패 경계와 실행 검증을 계속 우선합니다.
3. GitHub profile과 Pages의 공개 역할·evidence 요약을 동기화합니다.
4. GitHub Pages 링크, 모바일 렌더링과 공개 안전성 regression을 유지합니다.
5. 새 공개 프로젝트는 실제 지원 직무에서 새로운 capability gap이 확인될 때만 추가합니다.
