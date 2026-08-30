# AI Context for Engineering Career Portfolio

> 이 저장소 URL만 받은 AI는 `README.md`, `HOW_I_ENGINEER.md`, 이 문서, [`WORKS.md`](WORKS.md), [`PORTFOLIO_COMPLETION_PLAN.md`](PORTFOLIO_COMPLETION_PLAN.md), [`03_portfolio/portfolio-strategy.md`](03_portfolio/portfolio-strategy.md), [`03_portfolio/case-study-index.md`](03_portfolio/case-study-index.md), [`03_portfolio/evidence-index.md`](03_portfolio/evidence-index.md)를 먼저 읽습니다.

- 기준일: `2026-08-30`
- 공개 범위: `public`
- 공개 사이트: [GitHub Pages 포트폴리오](https://son1004007.github.io/engineering-career-portfolio/)
- 역할: 손기석의 기술 방향, 구현 샘플, 공개 가능한 경력과 기술 근거
- 범위 제한: 개인 연봉, 가족, 건강, 현재 회사 문제, 비공개 지원 전략은 이 저장소에서 판단하지 않음

## 핵심 정체성

상위 공개 역할은 다음입니다.

> Backend Engineer - AI Integration & Reliable Systems

한국어로는 다음과 같이 해석합니다.

> 업무 요구사항을 데이터, 권한, 처리 상태와 실패 조건이 명확한 백엔드 시스템으로 구현하고, AI 기능을 서버 검증과 실제 실행 근거 안에서 안전하게 연결하는 엔지니어

이 저장소는 특정 언어나 프레임워크 하나의 숙련도를 먼저 보여주는 포트폴리오가 아닙니다. 다만 역할 자체를 모호하게 만들지 않기 위해 시장 분류는 Backend Engineer로 고정합니다.

Java/Spring, Python/FastAPI, SQL, Docker, LLM은 중요한 실제 사용 기술이지만 상위 정체성 자체는 아닙니다.

이 저장소를 해석할 때 아래 역량을 먼저 봅니다.

1. 백엔드 문제 정의와 시스템 구조화
2. 백엔드와 데이터 연결
3. AI 기능의 안전한 통합과 검증
4. 테스트와 실제 실행을 통한 검증
5. 권한, 실패, 감사와 같은 보안 통제 경계
6. 배포, 운영, 복구와 재현 가능성

ML 모델 연구자나 파인튜닝 전문가로 포지셔닝하지 않습니다. AI 역량은 LLM 기능을 기존 백엔드 시스템에 통합하고 평가하며, 모델 실패가 중요한 업무 처리로 이어지지 않게 만드는 능력을 의미합니다.

`Platform Engineer`는 현재 상위 공개 title로 사용하지 않습니다. Linux, Docker, Nginx, Tomcat, CI/CD와 배포 경험은 중요한 operations evidence지만, IaC, Kubernetes, 내부 개발자 플랫폼, 중앙 observability와 멀티환경 운영 같은 직접 증거가 충분하지 않습니다.

## 공개 문서 작성 원칙

HR, 채용담당자와 엔지니어가 함께 읽는 구조를 사용합니다.

```text
먼저: 어떤 문제를 해결했고 무엇이 실제로 동작하는지
그 다음: 어떤 실패와 권한 경계를 확인했는지
마지막: 어떤 기술, 테스트와 실행 근거가 있는지
```

전문 용어는 유지하되 전문 용어를 알아야만 내용을 이해할 수 있게 쓰지 않습니다.

예:

```text
쉬운 설명:
권한이 없는 사용자의 중요한 요청은 서버에서 차단했습니다.

기술 설명:
RBAC, server-side validation, fail-closed policy를 적용했습니다.
```

Case Study 제목과 요약은 framework 이름이나 테스트 개수보다 문제와 실패 조건을 먼저 보여줍니다.

AI 관련 용어도 같은 원칙을 적용합니다. `Agent`, `RAG`, `context engineering`, `Agentic AI Runtime`, `provenance` 같은 용어를 먼저 나열하지 말고, 무엇을 구현하고 검증했는지를 먼저 설명합니다.

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

### 2. Data and AI Service Integration

회사 Text2SQL/NL2SQL 업무와 공개 가능한 관련 evidence는 자연어 질문을 SQL과 실제 데이터 조회 흐름으로 연결하고 모델 결과를 검증하는 경험을 보여줍니다.

주로 증명하는 것:

- Python/FastAPI API
- SQL validation and execution
- validation set
- multi-model comparison
- AI output evaluation

### 3. Engineering Case Studies

회사 코드를 공개하지 않고 실제 업무 문제를 일반화한 독립 샘플을 구현합니다.

현재 published 사례:

- 사용자 로그인과 권한 통합
- 복잡한 기간 조회의 데이터 정합성
- 배포와 복구의 이식성
- 업무 규칙의 일관성

이 사례는 `Java/Spring 사례집`으로만 해석하지 않습니다. 각각 identity boundary, data correctness, deployment/recovery, business-rule consistency라는 백엔드 문제 해결 증거입니다.

### 4. Engineering Method

[`HOW_I_ENGINEER.md`](HOW_I_ENGINEER.md)는 다음 흐름을 공개적으로 설명합니다.

```text
문제와 제약 정의
-> 설계
-> AI를 활용한 탐색과 구현
-> 자동 테스트
-> 실제 실행 검증
-> 결과와 한계 기록
```

AI가 코드를 작성했다는 사실보다, 결과를 다시 테스트하고 실행해 확인할 수 있다는 점을 중요하게 봅니다.

## OpsMate Local 현재 검증 상태

`OpsMate Local`은 `implemented`, `tested-component`이며 아래 bounded boundary는 `verified`입니다.

- `2026-08-23`: Ollama `gemma3:12b` 실제 모델 E2E `9/9`, 관측 p95 `21,076ms`, 프로젝트 gate `<= 30,000ms`
- `2026-08-25`: Synology 내부 stack/network/session/model/rate/log/lifecycle E2E
- `2026-08-29`: 실제 Internet HTTPS 경로의 persona flow, 외부 session isolation, DB/model/loopback 비노출, app direct egress 차단, public `429`, log scan, normal close, same-digest reopen, emergency close, recovery normal close와 final `CLOSED`

상세:

- [`02_projects/opsmate-local/docs/REAL_MODEL_E2E_EVIDENCE.md`](02_projects/opsmate-local/docs/REAL_MODEL_E2E_EVIDENCE.md)
- [`02_projects/opsmate-local/docs/NAS_INTERNAL_E2E_EVIDENCE.md`](02_projects/opsmate-local/docs/NAS_INTERNAL_E2E_EVIDENCE.md)
- [`02_projects/opsmate-local/docs/PUBLIC_DEPLOYMENT_E2E_EVIDENCE.md`](02_projects/opsmate-local/docs/PUBLIC_DEPLOYMENT_E2E_EVIDENCE.md)

모델이 없거나 잘못된 출력을 반환하면 모델 의존 초안 생성은 저장 전에 중단되며, 이미 제출된 요청의 승인, 반려와 발주는 모델 가용성과 분리됩니다.

이 증거는 bounded deployment E2E이며 24x7 가용성, SLA, 장기 부하나 대규모 실제 사용자 운영을 증명하지 않습니다.

## 공개 Case Study 상태

| 사례 | 쉽게 설명하면 | 상태 | 검증 초점 |
|---|---|---|---|
| Spring Security 인증 브리지 | 서로 다른 로그인 방식에서도 사용자와 권한이 잘못 연결되지 않게 함 | `published` | 인증 위조, 권한 오류, session 경계 |
| MyBatis 기간 조회 정합성 | 복잡한 기간 조건에서 빠지거나 겹치는 데이터가 없도록 함 | `published` | 기간 경계, 중복, 누락 |
| WAR 배포 이식성 | 서버 환경이 달라도 같은 애플리케이션을 배포하고 복구할 수 있게 함 | `published` | 설정 차이, health failure, rollback |
| 업무 규칙 정합성 | 사용자 식별과 최신 기준이 화면, API, DB에서 다르게 적용되지 않게 함 | `published` | 계층 간 기준 불일치 |

각 사례의 세부 문서에는 실제 자동 테스트 수와 실행 결과가 남아 있습니다. 첫 화면에서는 테스트 개수보다 검증한 실패 조건과 경계를 먼저 보여줍니다.

각 사례는 회사 코드와 독립된 합성 샘플입니다. 실제 회사 시스템 전체, 운영 DB, 장기 부하, SLA와 성능 수치를 확대해서 주장하지 않습니다.

## 회사 업무 evidence

기존 회사 업무는 원본 소스나 내부 식별자를 공개하지 않습니다.

게시물은 다음 순서를 지킵니다.

```text
권한 있는 환경에서 본인 귀속과 범위 확인
-> 문제와 제약 비식별화
-> 필요하면 합성 데이터로 독립 구현
-> 테스트
-> 공개 가능한 claim만 게시
```

현재 공개 evidence에는 회사 Text2SQL/NL2SQL 업무와 AI 작업 런타임의 사용자별 공간 분리, 결과 추적과 storage 관련 본인 구현 범위를 제한적으로 포함합니다.

## 이 저장소로 할 수 있는 판단

- 어떤 종류의 백엔드 문제를 시스템으로 구조화하는지
- 공개 코드와 테스트의 최근 검증 상태
- AI를 기존 업무 규칙과 서버 경계 안에 통합하는 방식
- 백엔드, 데이터, AI, 보안, 운영 역량의 조합
- 회사 코드를 공개하지 않고 문제를 독립 재현하는 방식
- 테스트와 runtime evidence를 완료 근거로 사용하는 습관

## 이 저장소만으로 하면 안 되는 판단

- 실무 경력 연수와 회사별 재직 기간 확정
- 특정 언어의 숙련도를 저장소 파일 수로 단정
- 회사와 팀 전체 성과를 개인 성과로 해석
- bounded E2E를 장기 production 운영 또는 SLA로 확대 해석
- 합성 샘플을 실제 회사 운영환경 검증으로 확대 해석
- 프로덕션 트래픽과 운영 규모
- 아직 검증하지 않은 Kafka/Redis/Kubernetes 실전 운영
- 팀 프로젝트 전체에서의 개인 기여 비율
- 특정 회사 입사, 연봉, 근무환경, 오퍼 수락 판단

## 읽기 순서

1. [`README.md`](README.md)
2. [`HOW_I_ENGINEER.md`](HOW_I_ENGINEER.md)
3. [`WORKS.md`](WORKS.md)
4. [`PORTFOLIO_COMPLETION_PLAN.md`](PORTFOLIO_COMPLETION_PLAN.md)
5. [`03_portfolio/portfolio-strategy.md`](03_portfolio/portfolio-strategy.md)
6. [`03_portfolio/case-study-index.md`](03_portfolio/case-study-index.md)
7. [`03_portfolio/evidence-index.md`](03_portfolio/evidence-index.md)
8. [`evidence/company-github/README.md`](evidence/company-github/README.md)
9. [`01_profile/career-summary.md`](01_profile/career-summary.md)
10. [`01_profile/core-strengths.md`](01_profile/core-strengths.md)
11. [`01_profile/career-direction.md`](01_profile/career-direction.md)
12. 검증하려는 프로젝트 또는 사례의 README, 코드, 테스트

## 증거 라벨

- `implemented`: 필요한 코드가 저장소에 존재
- `tested-file-present`: 테스트 파일은 있으나 현재 점검에서 실행 성공까지 확인하지 못함
- `tested-component`: 명시된 구성요소의 테스트 산출물은 있으나 전체 시스템 검증을 뜻하지 않음
- `source-reviewed`: 권한 있는 비공개 원본에서 본인 귀속과 구현 범위를 확인했으나 공개 재현 검증은 아직 없음
- `sample-verified`: 회사 코드와 독립된 공개 샘플의 최근 테스트 성공 기록이 있으나 실제 회사 시스템 검증을 뜻하지 않음
- `published`: 독립 공개 샘플과 문서의 검증 후 main Pages build/deploy까지 성공
- `verified`: 최근 실행일, 실행 경계, 환경과 성공 결과가 함께 기록됨
- `partial`: 일부 코드나 문서만 있고 주요 구성요소가 빠짐
- `planned`: 문서 또는 작업 목록에만 존재
- `self-described`: 경력 또는 프로필 문서의 자기기술이며 별도 근거 확인 필요
- `private-work-code-verified`: 권한 있는 환경에서 회사 비공개 코드와 본인 귀속을 확인하고 공개 문서에는 비식별 claim만 남김

기술 키워드, README 설명, 디렉터리 이름만으로 `verified`를 부여하지 않습니다.

## 현재 공개 증거 요약

| 항목 | 상태 | 판단 |
|---|---|---|
| `OpsMate Local` | `implemented`, `tested-component`; real-model, NAS internal, public Internet bounded boundary `verified` | 실제 모델, Synology 배포, private DB/model, 외부 HTTPS/session/rate/non-exposure와 lifecycle 검증. 24x7 SLA/장기 부하는 미검증 |
| 사용자 로그인과 권한 통합 사례 | `published` | 독립 합성 샘플 테스트 성공. 실제 외부 IdP/운영 DB/분산 세션은 미검증 |
| 기간 조회 데이터 정합성 사례 | `published` | 독립 합성 샘플 테스트 성공. 실제 Oracle 실행계획/운영 성능은 미검증 |
| 배포와 복구 이식성 사례 | `published` | 독립 합성 샘플 테스트 성공. 실제 외부 Tomcat 무중단 배포/SLA는 미검증 |
| 업무 규칙 일관성 사례 | `published` | 독립 합성 샘플 테스트 성공. 실제 회사 시스템 전체 정합성은 미검증 |
| `ai-rag-api` | `implemented`, `tested-file-present` | 코드와 테스트 파일은 있으나 최근 성공 실행 미확인 |
| `backend-platform-template` | `partial` | 현재 구조에 누락 모듈이 있음 |
| `security-audit-log` | `partial` | 일부 코드만 있고 완결된 공개 검증이 없음 |
| 회사 Text2SQL/NL2SQL 업무 | `private-work-code-verified`, `tested-component` | 비공개 회사 Git에서 본인 귀속 구성요소와 benchmark evidence 확인 |
| 회사 AI 작업 런타임 업무 | `private-work-code-verified`, `tested-component` | workspace, artifact, storage와 테스트의 본인 구현 범위 확인 |

상세 상태는 [`03_portfolio/evidence-index.md`](03_portfolio/evidence-index.md)를 기준으로 합니다.

## 직무와 직장 선택과 결합할 때

1. 이 저장소에서는 공개 기술 근거만 추출합니다.
2. 비공개 지원 전략이나 개인 조건은 이 공개 저장소에서 추론하지 않습니다.
3. 공개 회사 경력 claim은 `evidence/company-github/`에서 확인하고, 권한 있는 환경에서는 원본 코드, 테스트와 업무 기록과 교차 확인합니다.
4. 현재 공고와 회사 조건은 최신 공개 정보로 별도 조사합니다.
5. 결론에는 `공개 근거 / 권한 있는 비공개 근거 / 추론 / 미확인`을 분리합니다.
