# AI Context for Engineering Career Portfolio

> 이 저장소 URL만 받은 AI는 이 문서, [`WORKS.md`](WORKS.md), [`PORTFOLIO_COMPLETION_PLAN.md`](PORTFOLIO_COMPLETION_PLAN.md), [`03_portfolio/portfolio-strategy.md`](03_portfolio/portfolio-strategy.md), [`03_portfolio/case-study-index.md`](03_portfolio/case-study-index.md), [`03_portfolio/evidence-index.md`](03_portfolio/evidence-index.md)를 먼저 읽습니다.

- 기준일: `2026-08-30`
- 공개 범위: `public`
- 공개 사이트: [GitHub Pages 포트폴리오](https://son1004007.github.io/engineering-career-portfolio/)
- 역할: 손기석의 기술 방향, 구현 샘플, 공개 가능한 경력·기술 근거
- 범위 제한: 개인 연봉, 가족, 건강, 현재 회사 문제, 비공개 지원 전략은 이 저장소에서 판단하지 않음

## 확정된 포트폴리오 전략

이 저장소는 두 트랙으로 구성합니다.

1. `OpsMate Local`: 온프레미스 AI Agent를 기업 업무 트랜잭션에 안전하게 연결하는 대표 프로젝트
2. Java/Spring 실무 사례: 인증, DB/SQL, 배포, 업무 규칙과 운영의 실무 깊이를 회사 코드와 독립된 공개 샘플로 재현

핵심 정체성은 ML 모델 연구자가 아니라 **Java/Spring 엔터프라이즈 백엔드 경험을 중심으로 AI Agent 기능을 통합하는 백엔드·플랫폼 엔지니어**입니다.

## OpsMate Local 상태

`OpsMate Local`은 `implemented`, `tested-component`이며 다음 명시된 bounded boundary는 `verified`입니다.

- `2026-08-23`: Ollama `gemma3:12b` 실제 모델 E2E 9/9, 관측 p95 `21,076ms` (`<= 30,000ms` gate)
- `2026-08-25`: Synology exact immutable release 내부 stack/network/session/model/rate/log/lifecycle E2E
- `2026-08-29`: DSM TLS ingress를 통한 실제 Internet HTTPS persona flow, 외부 두 session isolation, URL session rewriting 부재, DB/model/loopback 비노출, app direct egress 차단, public `429`, log scan, normal close, same-digest reopen, emergency close, recovery normal close와 final `CLOSED`
- public evidence 반영 main Pages run `33250726427`: verify matrix, Jekyll build와 Pages deploy 성공

모델이 없거나 잘못된 출력을 반환하면 모델 의존 초안 생성은 저장 전에 `fail-closed`로 중단되고 외부 유료 API로 자동 우회하지 않습니다. 이미 제출된 요청의 승인·반려·발주는 모델 가용성과 분리되어 있습니다.

이 증거는 bounded deployment E2E이며 24x7 가용성, SLA, 장기 부하, 대규모 실제 사용자 운영을 증명하지 않습니다.

상세:

- [`02_projects/opsmate-local/docs/REAL_MODEL_E2E_EVIDENCE.md`](02_projects/opsmate-local/docs/REAL_MODEL_E2E_EVIDENCE.md)
- [`02_projects/opsmate-local/docs/NAS_INTERNAL_E2E_EVIDENCE.md`](02_projects/opsmate-local/docs/NAS_INTERNAL_E2E_EVIDENCE.md)
- [`02_projects/opsmate-local/docs/PUBLIC_DEPLOYMENT_E2E_EVIDENCE.md`](02_projects/opsmate-local/docs/PUBLIC_DEPLOYMENT_E2E_EVIDENCE.md)

## Java/Spring 공개 사례 상태

### CS-JAVA-01 — Spring Security 인증 브리지

- 상태: `published`
- 권한 있는 비공개 원본에서 본인 귀속 인증 통합 범위를 확인
- 회사 코드와 독립된 합성 Spring Security 샘플 구현
- 24개 자동 테스트 성공
- main publication evidence: commit `c74655a2e7aacfa0d05f41bc594598a0c0f73296`, Pages run `33276912458`의 Spring Security/public/Jekyll/build/deploy PASS
- 실제 IdP, 운영 DB, 분산 세션, 대규모 운영 부하는 검증/주장하지 않음

### CS-JAVA-02 — MyBatis 기간 조회 정합성

- 상태: `published`
- 권한 있는 비공개 원본에서 본인 귀속 SQL 개선 범위를 재확인
- 확인된 원칙: 여러 연도 기간의 복합 `OR`을 상호 배타적 구간으로 분해해 `UNION ALL`로 결합하고, indexed year/month column 숫자 변환 제거
- 공개 구현: [`02_projects/case-study-samples/mybatis-query-correctness/`](02_projects/case-study-samples/mybatis-query-correctness/README.md)
- Java 21 + Spring Boot 3.5.16 + MyBatis + H2 합성 데이터
- 12개 자동 테스트
- final PR regression run `33251272174`: PASS
- main Pages run `33251362190`: verify/build/deploy PASS
- 실제 Oracle optimizer index 선택과 운영 성능 수치는 검증/주장하지 않음

### CS-JAVA-03 — WAR 배포 이식성

- 상태: `published`
- 권한 있는 비공개 원본에서 본인 귀속 WAR deploy workflow/runbook, context-path 보정, profile/config 외부화 범위를 재확인
- 공개 구현: [`02_projects/case-study-samples/war-deployment-portability/`](02_projects/case-study-samples/war-deployment-portability/README.md)
- Java 21 + Spring Boot 3.5.16 WAR, provided Tomcat, `SpringBootServletInitializer`
- non-root context path, deploy profile fail-closed, backup/replace/health/rollback을 합성 환경에서 재현
- 10개 자동 테스트
- final PR regression run `33252086213`: 7개 job 전체 PASS
- main commit `63abaa49e05a366d6007902edd184a83df6bc7e9`
- main Pages run `33252148733`: 7개 verify job + build + deploy PASS
- CI context-path 관측: Java `21.0.12`, Spring Boot `3.5.16`, embedded Tomcat `10.1.55`
- 실제 외부 운영 Tomcat rolling deployment, session drain, zero-downtime, SLA는 검증/주장하지 않음

### CS-JAVA-06 — 업무 규칙 정합성

- 상태: `published`
- 권한 있는 비공개 원본에서 본인 author/committer 변경과 사용자 식별·최신 기준·조회 경로·null-safe 처리 범위를 재확인
- 공개 구현: [`02_projects/case-study-samples/business-rule-consistency/`](02_projects/case-study-samples/business-rule-consistency/README.md)
- Java 21 + Spring Boot 3.5.16 합성 `member snapshot` 도메인
- canonical session identity 우선, legacy fallback 제한, `LATEST_ONLY`/`EXPLICIT_OR_LATEST`, 명시적 Mapper 입력, 400/401/404 fail-closed 경계 재현
- 11개 MockMvc 자동 테스트
- 최초 PR run `33275860098`: 신규 sample job 및 전체 8개 portfolio job PASS
- final PR run `33276143715`: 상태 동기화 후 전체 8개 portfolio job PASS
- main merge commit `733db7c614af5613216773b3b1fc6b3567e0b84c`
- main Pages run `33276278894`: 8개 verify job + Pages build + deploy PASS
- 회사 클래스명, endpoint, field, SQL, schema, 테스트 계정, 실제 데이터와 내부 식별자는 공개하지 않음

기존 회사 업무는 원본 소스나 내부 식별자를 공개하지 않습니다. 게시물은 원본에서 본인 귀속과 구현 범위를 확인한 뒤 비식별 서술과 독립 재구현 코드로 만듭니다.

## 이 저장소로 할 수 있는 판단

- 공개 프로필에 기재된 기술 방향 파악
- 저장된 코드와 테스트 파일의 존재 및 최근 검증 상태 확인
- 개인 포트폴리오 프로젝트의 구현 범위와 검증된 boundary 확인
- Java/Spring 인증·SQL 정합성·WAR 배포·업무 규칙 정합성 사례가 독립 샘플에서 어떻게 재현됐는지 확인
- 백엔드·데이터·AI 응용·보안 관점의 조합 검토

## 이 저장소만으로 하면 안 되는 판단

- 실무 경력 연수와 회사별 재직 기간 확정
- 특정 기술의 숙련도나 대규모 운영 경험 확정
- 회사·팀 전체 성과를 개인 성과로 해석
- bounded E2E를 장기 production 운영 또는 SLA로 확대 해석
- Spring Security 합성 샘플을 실제 외부 IdP/운영 DB/분산 세션 운영 증거로 확대 해석
- H2 합성 샘플을 실제 Oracle 실행계획/성능 증거로 확대 해석
- embedded Tomcat CI를 실제 외부 운영 Tomcat 무중단 배포 증거로 확대 해석
- 업무 규칙 합성 샘플을 실제 회사 시스템 전체 정합성·운영 데이터 정확성 증거로 확대 해석
- 특정 회사 입사, 연봉, 근무환경, 오퍼 수락 판단

## 읽기 순서

1. [`WORKS.md`](WORKS.md)
2. [`PORTFOLIO_COMPLETION_PLAN.md`](PORTFOLIO_COMPLETION_PLAN.md)
3. [`03_portfolio/portfolio-strategy.md`](03_portfolio/portfolio-strategy.md)
4. [`03_portfolio/case-study-index.md`](03_portfolio/case-study-index.md)
5. [`evidence/company-github/README.md`](evidence/company-github/README.md)
6. [`evidence/company-github/career-claims.csv`](evidence/company-github/career-claims.csv)
7. [`03_portfolio/evidence-index.md`](03_portfolio/evidence-index.md)
8. [`01_profile/career-summary.md`](01_profile/career-summary.md)
9. [`01_profile/core-strengths.md`](01_profile/core-strengths.md)
10. [`01_profile/career-direction.md`](01_profile/career-direction.md)
11. [`03_portfolio/portfolio-overview.md`](03_portfolio/portfolio-overview.md)
12. 검증하려는 프로젝트 또는 사례의 README, 코드, 테스트

## 증거 라벨

- `implemented`: 필요한 코드가 저장소에 존재
- `tested-file-present`: 테스트 파일은 있으나 현재 점검에서 실행 성공까지 확인하지 못함
- `tested-component`: 명시된 구성요소의 테스트 산출물은 있으나 전체 시스템 검증을 뜻하지 않음
- `source-reviewed`: 권한 있는 비공개 원본에서 본인 귀속과 구현 범위를 확인했으나 공개 재현 검증은 아직 없음
- `sample-verified`: 회사 코드와 독립된 공개 샘플의 최근 테스트 성공 기록이 있으나 실제 회사 시스템 검증을 뜻하지 않음
- `published`: 독립 공개 샘플과 문서의 검증 후 main Pages build/deploy까지 성공
- `verified`: 최근 실행일, 명령/실행 경계, 환경·버전, 성공 결과가 함께 기록됨
- `partial`: 일부 코드나 문서만 있고 주요 구성요소가 빠짐
- `planned`: 문서 또는 작업 목록에만 존재
- `self-described`: 경력·프로필 문서의 자기기술이며 별도 근거 확인 필요
- `private-work-code-verified`: 권한 있는 환경에서 회사 비공개 코드와 본인 귀속 커밋을 확인하고 공개 문서에는 비식별 claim만 남김

기술 키워드, README 설명, 디렉터리 이름만으로 `verified`를 부여하지 않습니다.

## 현재 공개 증거 요약

| 항목 | 상태 | 판단 |
|---|---|---|
| `OpsMate Local` | `implemented`, `tested-component`; real-model, NAS internal, public Internet bounded boundary `verified` | 실제 모델, immutable Synology 배포, private DB/model, 외부 HTTPS/session/rate/non-exposure와 lifecycle 검증. 24x7 SLA/장기 부하는 미검증 |
| Spring Security 인증 사례 | `published` | 회사 코드와 독립된 합성 샘플 24개 테스트와 main Pages run `33276912458` 성공. 실제 IdP/DB/분산 세션은 미검증 |
| MyBatis 기간 조회 사례 | `published` | 합성 Spring Boot/MyBatis/H2 샘플 12개 테스트와 main Pages run `33251362190` 성공. Oracle 운영 성능은 미검증 |
| WAR 배포 이식성 사례 | `published` | 합성 Spring Boot WAR 샘플 10개 테스트, PR regression `33252086213`, main Pages `33252148733` 성공. 실제 외부 Tomcat 운영은 미검증 |
| 업무 규칙 정합성 사례 | `published` | 합성 Spring Boot 샘플 11개 테스트, final PR `33276143715`, main Pages `33276278894` 성공. 실제 회사 시스템 전체 정합성은 미검증 |
| `ai-rag-api` | `implemented`, `tested-file-present` | 코드·테스트 파일은 있으나 최근 성공 실행 미확인 |
| `backend-platform-template` | `partial` | 현재 구조에 누락 모듈이 있음 |
| `security-audit-log` | `partial` | API route만 있고 참조 service, 앱 진입점, 테스트가 없음 |
| 회사 Text2SQL/NL2SQL 업무 | `private-work-code-verified`, `tested-component` | 비공개 회사 Git에서 본인 귀속 구성요소와 benchmark evidence 확인 |
| 회사 Agentic AI Runtime 업무 | `private-work-code-verified`, `tested-component` | workspace, artifact, provenance, storage와 테스트의 본인 구현 범위 확인 |

상세 상태는 [`03_portfolio/evidence-index.md`](03_portfolio/evidence-index.md)를 기준으로 합니다.

## 안전한 해석

현재 공개 저장소는 다음을 보여주기에 적합합니다.

```text
Java/Spring 인증·인가·세션·CSRF 경계를 독립 샘플과 테스트로 검증하는 방식
MyBatis 기간 조회를 정합성·tenant isolation·count/page·pagination·SQL shape 관점에서 검증하는 방식
WAR 서비스의 external-container bootstrap, non-root context path, 외부 config fail-closed, health rollback 경계를 독립 샘플로 검증하는 방식
canonical session identity, 화면별 snapshot policy, Service-Mapper 책임을 독립 회귀 샘플로 검증하는 방식
AI 출력을 Spring 업무 규칙·승인·멱등성·fail-closed 경계에 넣는 설계와 구현
실제 open-weight model 구조화 출력을 서버 검증·저장까지 연결한 E2E
immutable artifact를 Synology에 배포하고 private DB/model network와 restricted SSH tunnel을 검증한 경험
Internet HTTPS 경로에서 실제 모델 업무 흐름, session isolation, rate/non-exposure 통제와 lifecycle을 bounded E2E로 검증한 경험
Text2SQL/NL2SQL API, SQL 검증과 다중 모델 benchmark 업무
Agentic AI Runtime의 작업 격리와 산출물 추적 구성요소 구현
비공개 업무 근거를 비식별 claim과 독립 공개 재현 상태로 분리하는 방식
```

다음은 이 저장소만으로 확정할 수 없습니다.

```text
Java/Spring 실무의 전체 수준과 아직 재현하지 않은 나머지 사례의 구현 완료
Spring Security 샘플의 실제 외부 IdP/운영 DB/분산 세션/운영 부하
MyBatis 샘플의 실제 Oracle 실행계획과 운영 성능
WAR 샘플의 실제 외부 Tomcat zero-downtime/session-drain/SLA
업무 규칙 샘플의 실제 회사 SSO/session E2E, Mapper SQL/운영 DB 결과, 운영 데이터 전체 정합성
프로덕션 트래픽과 운영 규모
Kafka/Redis/Kubernetes 실전 운영
RAG 품질·보안·관측성·비용 최적화
팀 프로젝트 전체에서의 개인 기여 비율
회사 비공개 서비스의 장기 운영 규모와 성과
OpsMate의 24x7 외부 운영 안정성, SLA, 장기/대규모 부하 성능
```

## 직무·직장 선택과 결합할 때

1. 이 저장소에서는 공개 기술 근거만 추출합니다.
2. 비공개 지원 전략이나 개인 조건은 이 공개 저장소에서 추론하지 않습니다.
3. 공개 회사 경력 claim은 `evidence/company-github/`에서 확인하고, 권한 있는 환경에서는 원본 코드·테스트·업무 기록과 교차 확인합니다.
4. 현재 공고·회사 조건은 최신 공개 정보로 별도 조사합니다.
5. 결론에는 `공개 근거 / 권한 있는 비공개 근거 / 추론 / 미확인`을 분리합니다.
