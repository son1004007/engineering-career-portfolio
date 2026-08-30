# Portfolio Content Strategy

- 결정일: `2026-08-03`
- 상태 갱신일: `2026-08-30`
- 상태: `approved`
- 목적: 특정 언어나 framework가 아니라 문제 해결, 시스템 설계, AI 활용, 검증과 운영 역량이 먼저 보이도록 공개 포트폴리오를 구성

## 최종 포지셔닝

이 포트폴리오의 상위 정체성은 다음입니다.

> 업무 문제를 시스템으로 구조화하고, 적절한 기술과 AI를 활용해 구현하며, 테스트·보안·운영 증거까지 남기는 소프트웨어/백엔드/플랫폼 엔지니어

Java/Spring, Python/FastAPI, SQL, Docker와 LLM은 중요한 실제 사용 기술이지만 포트폴리오의 첫 분류 기준으로 사용하지 않습니다.

ML 모델 연구, 학습 또는 파인튜닝을 핵심 정체성으로 두지 않습니다. AI는 기존 소프트웨어 엔지니어링을 확장하는 기능이자 개발 도구로 다룹니다.

## 독자 우선순위

공개 문서는 아래 독자가 같은 페이지를 읽을 수 있어야 합니다.

1. HR / recruiter
2. hiring manager
3. backend/platform engineer
4. security/data/AI engineer

문서는 2단 구조로 작성합니다.

```text
1단:
왜 중요한 문제인지, 무엇을 했는지, 어떤 결과가 있었는지 쉽게 설명

2단:
system boundary, transaction, RBAC, fail-closed, provenance 같은 전문 용어와 구현·검증 근거 제공
```

전문 용어를 없애지 않습니다. 대신 전문 용어를 이해해야만 성과를 알 수 있게 쓰지 않습니다.

## 첫 화면에서 보여줄 순서

README와 GitHub Pages 홈은 다음 순서를 유지합니다.

```text
Engineer Identity
-> 제가 잘하는 일
-> 숫자로 확인된 증거
-> 대표 프로젝트
-> AI를 활용해 개발하는 방식
-> 실무 문제 해결 Case Study
-> 사용하는 기술
```

기술 목록은 capability와 evidence 뒤에 둡니다.

## Capability Map

### 1. Problem Framing / System Design

쉽게 설명하면:

- 요청을 그대로 코딩하지 않고 실제 문제, 사용자 흐름, 제약과 완료 기준으로 정리
- 화면 하나의 변경도 API, DB, batch, 운영까지 영향 범위를 확인

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

### 3. AI Integration / AI-Augmented Engineering

쉽게 설명하면:

- AI가 도와줄 일과 서버 또는 사람이 최종 책임질 일을 분리
- 개발 과정에서도 AI를 조사, 구현, 테스트와 검토에 활용

기술적으로:

- LLM integration
- Agent
- RAG
- Text2SQL/NL2SQL
- structured output validation
- context engineering

### 4. Verification

쉽게 설명하면:

- 코드가 있다는 사실보다 실제로 맞게 동작하는지 확인
- 정상 상황뿐 아니라 실패, 권한, 중복과 잘못된 입력도 검증

기술적으로:

- unit/integration/E2E
- regression
- AI evaluation
- runtime evidence

### 5. Security / Governance

쉽게 설명하면:

- 사용자가 해도 되는 일과 하면 안 되는 일을 시스템이 확인
- 오류나 AI 실패가 중요한 업무 처리로 이어지지 않게 차단
- 결과가 어떻게 만들어졌는지 다시 확인할 수 있게 기록

기술적으로:

- authentication / authorization
- RBAC
- least privilege
- audit
- fail-closed
- artifact / provenance

### 6. Operations / Recovery

쉽게 설명하면:

- 내 PC에서 실행되는 것으로 끝내지 않고 실제 배포, 상태 확인, 중단과 복구까지 고려

기술적으로:

- Linux / Docker
- Tomcat / Nginx
- CI/CD
- health check
- rollback
- immutable release

## Portfolio Evidence Tracks

포트폴리오는 언어별 프로젝트가 아니라 서로 다른 역량을 증명하는 4개 evidence track으로 구성합니다.

### Track A. Controlled AI Integration - OpsMate Local

AI Agent를 기업 업무 트랜잭션에 안전하게 연결하는 대표 프로젝트입니다.

쉽게 설명하면:

> AI는 구매 요청 초안을 도와주지만, 권한 확인, 업무 상태 변경과 발주는 기존 서버 규칙과 사람 승인 안에서만 이루어지게 만든 시스템입니다.

주로 증명하는 것:

- AI와 업무 규칙 책임 분리
- 권한, 상태 전이와 중복 방지
- 모델 장애와 잘못된 출력의 안전 처리
- session/network isolation
- 실제 모델과 Internet HTTPS bounded E2E
- close/reopen/recovery

현재 공개 검증:

- `2026-08-23`: 실제 Ollama `gemma3:12b` E2E 9/9, 관측 p95 `21,076ms`
- `2026-08-25`: Synology 내부 deployment/network/lifecycle bounded E2E
- `2026-08-29`: 실제 Internet HTTPS, session isolation, rate/non-exposure, lifecycle bounded E2E

이 결과를 24x7 SLA나 장기 대규모 운영으로 확대 해석하지 않습니다.

### Track B. Data / AI Service Integration

Text2SQL/NL2SQL과 AI Runtime 업무 evidence를 통해 Python/API/data/LLM 평가 역량을 보여줍니다.

주로 증명하는 것:

- 자연어 질문과 SQL/DB 연결
- FastAPI 기반 AI 응용 API
- validation set
- multi-model comparison
- workspace isolation
- artifact/provenance traceability

회사 비공개 코드는 공개하지 않으며 확인된 역할과 범위만 비식별 claim으로 사용합니다.

### Track C. Engineering Problem Case Studies

실제 업무 문제를 회사 코드와 독립된 합성 샘플로 재구현합니다.

Case Study는 framework 이름보다 문제를 먼저 보여줍니다.

| 내부 기술명 | 공개 문제 중심 표현 |
|---|---|
| Spring Security 인증 브리지 | 사용자 로그인과 권한을 안전하게 통합 |
| MyBatis 기간 조회 | 복잡한 기간 조회에서 데이터 정합성 유지 |
| WAR 배포 이식성 | 환경이 달라도 배포하고 복구할 수 있는 구조 |
| 업무 규칙 정합성 | 사용자 식별과 업무 기준을 여러 계층에서 일관되게 유지 |

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

기술은 마지막에 설명합니다.

### Track D. Engineering Method

[`../HOW_I_ENGINEER.md`](../HOW_I_ENGINEER.md)에서 개발 방식을 공개합니다.

```text
문제와 제약 정의
-> 설계
-> AI를 활용한 탐색과 구현
-> 자동 테스트
-> runtime verification
-> evidence와 문서 갱신
```

AI 사용 자체보다 다음을 증명하는 것이 목적입니다.

- AI가 바뀌어도 프로젝트 상태와 제약을 복원할 수 있음
- AI 결과를 그대로 신뢰하지 않고 테스트로 검증함
- 사람이 목표, 제한과 최종 판단을 유지함
- 다른 개발자 또는 AI가 작업을 이어받을 수 있음

## 두 독자에게 동시에 보여주는 방법

모든 대표 프로젝트와 Case Study는 다음 형태를 권장합니다.

```text
제목:
비기술자도 문제를 이해할 수 있는 표현

첫 문단:
문제 -> 행동 -> 결과

기술 세부:
architecture / code / framework / security term

검증:
test / E2E / run / evidence

한계:
검증하지 않은 범위
```

예:

```text
사용자 역할에 따라 할 수 있는 일을 제한하고,
잘못된 AI 결과는 저장 전에 중단했습니다.

기술적으로는 RBAC, server-side validation,
state transition, fail-closed policy를 적용했습니다.
```

## 회사 업무 사례 공개 규칙

회사 소유 비공개 저장소는 증거 확인에만 사용합니다.

- 회사 저장소명, 고객명, 내부 URL, 원본 commit message와 파일 경로를 공개하지 않음
- 회사 source, diff, SQL, 설정과 데이터를 복사하지 않음
- 본인 귀속과 담당 범위를 원본에서 먼저 확인
- 게시물은 비식별 문제·제약·판단·검증 중심으로 새로 작성
- 코드를 보여줄 필요가 있으면 합성 데이터와 일반화한 domain으로 독립 재구현
- 원본 검토, 공개 안전성 검토와 재현 코드 검증이 끝나기 전에는 게시 완료로 표시하지 않음

개인 공개 저장소도 게시 전에 credential, 개인정보, 라이선스와 README-구현 일치 여부를 검사합니다.

## 다음 프로젝트를 고르는 기준

다음 Case Study나 프로젝트는 `Java 사례를 하나 더 만든다`는 방식으로 선택하지 않습니다.

Capability map의 증거 공백을 기준으로 선택합니다.

우선순위 예:

```text
1. 현재 공개 evidence에서 부족한 역량 확인
2. 실제 경력 근거가 있는 문제인지 확인
3. 회사 자산 없이 독립 재현 가능한지 확인
4. 기존 프로젝트와 다른 판단이나 검증을 보여주는지 확인
5. 채용 담당자가 문제를 이해할 수 있는지 확인
```

현재 Java/Spring 사례 4건은 충분한 backend depth evidence로 유지하고, 다음 신규 공개 사례는 data/AI/platform/security/operations 중 증거 공백이 더 큰 영역을 우선합니다.

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
- `HOW_I_ENGINEER.md`: 언어에 독립적인 개발 방식
- `01_profile/`: 정체성, 강점, 방향
- `03_portfolio/case-studies/`: 문제 중심 실무 사례
- `02_projects/`: 실행 가능한 공개 코드와 evidence
- `evidence/company-github/`: 비식별 업무 근거와 claim

## 다음 실행 순서

1. README, AI context, profile과 overview를 capability-first 문구로 유지합니다.
2. case-study 제목과 도입부를 HR이 이해할 수 있는 문제 중심 문장으로 순차 개선합니다.
3. 공개 evidence 수치와 상태가 실제 run/test 결과와 일치하는지 유지합니다.
4. capability map에서 다음 증거 공백을 선택해 신규 사례를 결정합니다.
5. GitHub Pages 링크, 모바일 렌더링과 공개 안전성 regression을 유지합니다.
