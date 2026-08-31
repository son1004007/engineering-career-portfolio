# Portfolio Evidence Index

- 최종 점검일: `2026-08-31`
- 목적: 프로필 주장, 구현 코드, 테스트, 계획을 구분해 과장 없는 기술 검토를 가능하게 함

## 상태 정의

| 상태 | 의미 |
|---|---|
| `verified` | 최근 실행일, 명령, 환경·버전, 성공 결과가 함께 기록됨 |
| `implemented` | 핵심 코드가 저장소에 존재 |
| `tested-file-present` | 테스트 파일은 있으나 최근 성공 실행이 확인되지 않음 |
| `tested-component` | 명시된 구성요소의 테스트 산출물은 확인했으나 전체 시스템 검증은 아님 |
| `source-reviewed` | 권한 있는 비공개 원본에서 본인 귀속과 구현 범위를 확인했으나 공개 재현 검증은 아직 없음 |
| `sample-verified` | 회사 코드와 독립된 공개 샘플의 최근 테스트 성공을 기록했으나 실제 회사 시스템 검증은 아님 |
| `published` | 독립 샘플·문서 검증 후 main GitHub Pages build/deploy까지 성공 |
| `partial` | 일부 코드·문서만 있고 실행에 필요한 구성요소가 빠짐 |
| `planned` | 문서나 작업 목록에만 존재 |
| `self-described` | 프로필·경력 문서의 자기기술. 다른 근거로 확인 필요 |
| `private-work-code-verified` | 권한 있는 환경에서 회사 비공개 코드와 본인 귀속 커밋을 확인. 공개 문서에는 비식별 claim만 존재 |

## 공개 사이트 검증

- 공개 URL: [GitHub Pages 포트폴리오](https://son1004007.github.io/engineering-career-portfolio/)
- 최초 전체 공개본 검증일: `2026-08-03`
- 당시 Pages Actions run `30782896966`: Jekyll build, Java 프로젝트와 공개 저장소 검사·deploy 성공
- OpsMate reviewed runtime source `f99686981da7efb8802635ae2bde5b0f781433ad` 기준 repository regression run `32848946968`: 성공 (`2026-08-25`)
- 내부 E2E 문서 반영 main Pages run `32851086949`: 성공 (`2026-08-25`)
- OpsMate public deployment E2E 문서 반영 main Pages run `33250726427`: verify/build/deploy 성공 (`2026-08-29`)
- MyBatis `CS-JAVA-02` main Pages run `33251362190`: verify/build/deploy 성공 (`2026-08-29`)
- WAR `CS-JAVA-03` main Pages run `33252148733`: 7개 verify job + build + deploy 성공 (`2026-08-29`)
- Java publication-state sync main Pages run `33252607907`: 7개 verify job + build + deploy 성공
- 업무 규칙 정합성 `CS-JAVA-06` final PR regression run `33276143715`: 8개 job 전체 성공
- `CS-JAVA-06` main commit `733db7c614af5613216773b3b1fc6b3567e0b84c`
- `CS-JAVA-06` main Pages run `33276278894`: 8개 verify + build + deploy 성공 (`2026-08-30`)
- Spring Security `CS-JAVA-01` publication evidence: main commit `c74655a2e7aacfa0d05f41bc594598a0c0f73296`, Pages run `33276912458`의 Spring Security regression, public/Jekyll 검사, Pages build/deploy 성공 (`2026-08-30`)
- `CS-AI-01`은 독립 프로젝트 검증은 완료했지만 이 포트폴리오의 Pages publication은 아직 진행 중이므로 현재 `sample-verified`로 유지
- 모바일 baseline은 viewport, 52rem/32rem responsive breakpoints, nav/code/table horizontal-overflow guard를 public portfolio test로 고정하며 물리 단말 육안 검수는 선택적 유지관리 항목으로 둠

## 프로젝트 상태

| 프로젝트 | 코드 | 테스트 | 문서 | 현재 상태 | 확인 사항 |
|---|---:|---:|---:|---|---|
| [OpsMate Local](../02_projects/opsmate-local/README.md) | 수직 기능, 공개 웹, workspace/model guard, PostgreSQL, restricted tunnel, 배포/lifecycle 자산 있음 | 실제 `gemma3:12b` 9/9 E2E + Synology internal E2E + public Internet bounded E2E 성공 | README·ARCHITECTURE·SETUP·PUBLIC_DEMO·THREAT_MODEL·SERVICE_RUNBOOK·3종 E2E evidence | `implemented`, `tested-component`; 명시된 real-model/NAS/public boundary `verified` | 24x7 SLA, 장기 부하, 대규모 실제 사용자 운영은 검증/주장하지 않음. workload `CLOSED` |
| [Spring Security 인증 브리지](../02_projects/case-study-samples/spring-security-auth-bridge/README.md) | 독립 공개 샘플 있음 | 24개 성공; main Pages run `33276912458` Spring Security regression PASS | [사례 게시물](case-studies/spring-security-auth-bridge.md)·README·ARCHITECTURE·SETUP·VERIFICATION | `published` | 합성 사용자·issuer·audience 바인딩 SSO 샘플. 실제 IdP/DB/분산 세션/운영 부하는 미검증 |
| [MyBatis 기간 조회 정합성](../02_projects/case-study-samples/mybatis-query-correctness/README.md) | 독립 Spring Boot/MyBatis/H2 샘플 있음 | 12개 성공; final PR regression `33251272174` | [사례 게시물](case-studies/mybatis-query-correctness.md)·README·ARCHITECTURE·SETUP·VERIFICATION | `published` | main Pages `33251362190` 성공. Oracle 실행계획·운영 성능 수치는 미검증 |
| [WAR 배포 이식성](../02_projects/case-study-samples/war-deployment-portability/README.md) | 독립 Spring Boot WAR 샘플 있음 | 10개 성공; final PR regression `33252086213` | [사례 게시물](case-studies/war-deployment-portability.md)·README·ARCHITECTURE·SETUP·VERIFICATION | `published` | main Pages `33252148733` 성공. 실제 외부 Tomcat rolling/zero-downtime/SLA는 미검증 |
| [업무 규칙 정합성](../02_projects/case-study-samples/business-rule-consistency/README.md) | 독립 Spring Boot 합성 샘플 있음 | 11개 성공; final PR regression `33276143715` 전체 8개 job 성공 | [사례 게시물](case-studies/business-rule-consistency.md)·README·ARCHITECTURE·SETUP·VERIFICATION | `published` | main Pages `33276278894` 성공. canonical identity/latest-only/Mapper ownership/fail-closed 경계를 검증했으며 실제 회사 시스템 전체 정합성은 미검증 |
| [Text2SQL Workspace](https://github.com/son1004007/text2sql-workspace) | 독립 Python/FastAPI 멀티사용자 Text2SQL 서비스, SQLGlot policy, SQLite/PostgreSQL query executor, Docker runtime | public project main CI에서 Python test + Docker/PostgreSQL E2E PASS (`2026-08-31`) | README·ARCHITECTURE·SETUP·CURRENT_STATE·runtime/security evidence + [사례 게시물](case-studies/text2sql-validation.md) | `sample-verified` | reader SELECT 성공/INSERT 실패, workspace 격리, unsafe SQL 사전 차단, bounded execution/evaluation 검증. external real LLM·production auth/load/SLA는 미검증; portfolio Pages publication pending |
| [Java/Spring 사례 후보](case-study-index.md) | 권한 있는 원본에서 일부 확인 | 공개 독립 샘플 4건 검증, 4건 publication gate 완료 | 후보 인덱스 있음 | `published` 4건, `source-reviewed` 다수 | 원본 코드를 공개하지 않으며 독립 재현·최근 테스트가 없는 사례를 완료로 표현하지 않음 |
| [`ai-rag-api`](../02_projects/ai-rag-api/README.md) | 있음 | 있음 | README·ARCHITECTURE·SETUP | `implemented`, `tested-file-present` | 현재 점검 환경에서 최근 성공 실행 미확인. 실제 LLM·벡터 저장소 품질과 운영성 별도 검증 필요 |
| [`backend-platform-template`](../02_projects/backend-platform-template/README.md) | 일부 | 있음 | README·ARCHITECTURE·SETUP | `partial` | `app/main.py`가 존재하지 않는 `app.api.routes`를 import. 주요 구성요소 누락 |
| [`security-audit-log`](../02_projects/security-audit-log/README.md) | route만 있음 | 없음 | README | `partial` | 참조 service, 앱 진입점, 저장 모델, tests 보완 필요 |
| `security-backend-platform` | 없음 | 없음 | 상위 문서의 예시만 있음 | `planned` | 별도 프로젝트로 완료되기 전 기술 근거로 사용 금지 |

## OpsMate 실제 모델 E2E 증거

`2026-08-23` 사설 GPU runtime에서 source commit `ff67df0990cbed3a41cf5051a5e2701a7b2a7b50`의 실제 모델 gate를 실행했습니다.

- Ollama `0.13.5`
- model `gemma3:12b`
- 합성 구매 요청 9건 중 9건 성공
- 실제 `/api/chat` 구조화 출력, JSON 역직렬화와 서버 측 category·policy 검증 통과
- 요청·감사 이벤트 저장 건수 각각 9건
- 관측 p95 `21,076ms`, gate `p95 <= 30,000ms`
- Maven exit code `0`

상세: [`../02_projects/opsmate-local/docs/REAL_MODEL_E2E_EVIDENCE.md`](../02_projects/opsmate-local/docs/REAL_MODEL_E2E_EVIDENCE.md)

## OpsMate NAS internal deployment/lifecycle E2E 증거

`2026-08-25` exact release source `f99686981da7efb8802635ae2bde5b0f781433ad`와 immutable application/model-tunnel image를 Synology runtime에서 bounded E2E로 검증했습니다.

- exact immutable image pull/stage
- runtime input permission `600`, PostgreSQL persistent volume 보존
- restricted SSH tunnel -> 실제 `gemma3:12b` model path
- stack/host-port/Docker-network/Nginx edge security
- app/edge direct egress blocked
- app/DB/model-tunnel host port 없음
- Secure XSRF/JSESSIONID, COOKIE-only tracking
- persona flow, durable draft, cross-workspace isolation
- edge rate-limit `429`, credential/log scan
- normal close, strict CLOSED, same-digest reopen, emergency close, recovery normal close
- final `runtime_policy_flags=YES_YES`, final `CLOSED`

상세: [`../02_projects/opsmate-local/docs/NAS_INTERNAL_E2E_EVIDENCE.md`](../02_projects/opsmate-local/docs/NAS_INTERNAL_E2E_EVIDENCE.md)

## OpsMate public Internet deployment/lifecycle E2E 증거

`2026-08-29` private `device-control` bounded runtime run `33241004788`에서 실제 Internet HTTPS origin을 포함한 최종 외부 gate를 수행했습니다.

- DSM Reverse Proxy/TLS + router ingress public HTTPS: PASS
- public `/api/**` denial, `/actuator/**` 차단: PASS
- 실제 모델 draft -> submit -> approve -> order -> audit -> cleanup: PASS
- 외부 두 session cross-workspace isolation: PASS
- URL `;jsessionid` rewriting: absent
- app direct Internet egress: blocked
- 외부 PostgreSQL/model/loopback edge 직접 TCP 노출: closed
- bounded rate burst 60건: allowed `24`, HTTP `429` `36`, transport failure `0`
- credential/private-key/Bearer marker log scan: PASS
- normal close, same immutable digest reopen, emergency close, recovery normal close: PASS
- final `runtime_policy_flags=YES_YES`, running workload container `0`, PostgreSQL volume preserved, final `CLOSED`

상세: [`../02_projects/opsmate-local/docs/PUBLIC_DEPLOYMENT_E2E_EVIDENCE.md`](../02_projects/opsmate-local/docs/PUBLIC_DEPLOYMENT_E2E_EVIDENCE.md)

이 증거는 bounded deployment/network/security/lifecycle 검증입니다. 24x7 SLA, 장기 부하, DDoS/WAF 전문 방어, production traffic 규모를 의미하지 않습니다.

## `CS-JAVA-01` Spring Security 인증 브리지 공개 재현 증거

권한 있는 비공개 원본에서 본인 귀속 인증 통합 범위를 확인한 뒤 회사 코드·계정·역할명·설정·내부 식별자를 사용하지 않는 독립 Spring Security 샘플로 재현했습니다.

검증된 공개 샘플 경계:

- Java 21 + Spring Boot 3.5.16
- DB credential과 signed SSO assertion을 하나의 local user/RBAC 모델로 수렴
- issuer, audience, active keyId와 HMAC signature 검증
- local active status와 role을 최종 권한 source로 사용
- session id rotation과 SecurityContext 저장
- session/cookie/header CSRF 수명주기와 이전 token 재사용 거부
- nonce replay 차단과 시간 경계 검증
- 비밀 누락·길이 부족 시 SSO fail-closed
- 24개 자동 테스트, 실패·오류·건너뜀 0 (`2026-08-03` 기록)
- main commit `c74655a2e7aacfa0d05f41bc594598a0c0f73296`
- main Pages run `33276912458`: Spring Security regression + public portfolio + Jekyll + Pages build/deploy PASS

검증하지 않은 것: 실제 외부 IdP, 운영 DB, 분산 세션, Redis nonce store, 로그인 rate limit, 감사 이벤트, 대규모 동시 접속, 실제 운영 전환과 SLA.

상세: [`../02_projects/case-study-samples/spring-security-auth-bridge/VERIFICATION.md`](../02_projects/case-study-samples/spring-security-auth-bridge/VERIFICATION.md)

## `CS-JAVA-02` MyBatis 공개 재현 증거

권한 있는 비공개 원본에서 본인 귀속 SQL 개선 범위를 재확인한 뒤 회사 SQL·schema·식별자·데이터를 사용하지 않는 독립 샘플로 재현했습니다.

검증된 공개 샘플 경계:

- Java 21 + Spring Boot 3.5.16 + MyBatis Spring Boot Starter 3.0.5 + H2
- same-year/cross-year 시작·종료 경계
- 누락·중복 부재와 tenant isolation
- count/page 공통 filter semantics와 deterministic pagination
- invalid input validation
- `(tenant_id, snapshot_year, snapshot_month)` 합성 복합 인덱스
- MyBatis `BoundSql` same/cross-year SQL shape
- indexed year/month column 변환 함수 및 기간 `OR` 부재
- 12개 자동 테스트
- final PR regression run `33251272174`: PASS
- main Pages run `33251362190`: verify/build/deploy PASS

검증하지 않은 것: 실제 Oracle optimizer index 선택, 회사 실행계획 원문, 운영 elapsed/CPU/logical read 개선률, production traffic 성능.

상세: [`../02_projects/case-study-samples/mybatis-query-correctness/VERIFICATION.md`](../02_projects/case-study-samples/mybatis-query-correctness/VERIFICATION.md)

## `CS-JAVA-03` WAR 배포 이식성 공개 재현 증거

권한 있는 비공개 원본에서 본인 귀속 WAR deploy workflow/runbook, context-path 보정, profile/config 외부화 범위를 재확인한 뒤 회사 WAR/JSP/workflow/호스트/배포 경로/인증정보를 사용하지 않는 독립 샘플로 재현했습니다.

검증된 공개 샘플 경계:

- Java 21 + Spring Boot 3.5.16
- Maven `war` packaging + provided Tomcat
- `SpringBootServletInitializer` 기반 external-container bootstrap
- non-root `/demo` context path entry/health와 root hardcoding 부재
- deploy profile 필수 외부 값 누락 시 fail-closed
- candidate WAR backup -> staged replace -> health -> 실패 시 rollback
- unsafe application name 거부
- 10개 자동 테스트
- final PR regression run `33252086213`: 7개 job 전체 PASS
- main commit `63abaa49e05a366d6007902edd184a83df6bc7e9`
- main Pages run `33252148733`: 7개 verify + build + deploy PASS
- context-path CI 관측: Java `21.0.12`, Spring Boot `3.5.16`, embedded Tomcat `10.1.55`

검증하지 않은 것: 실제 외부 Tomcat 버전별 호환성 전체, rolling deployment, session drain, zero-downtime, 실제 TLS/SSO 통합, production traffic/SLA.

상세: [`../02_projects/case-study-samples/war-deployment-portability/VERIFICATION.md`](../02_projects/case-study-samples/war-deployment-portability/VERIFICATION.md)

## `CS-JAVA-06` 업무 규칙 정합성 공개 재현 증거

권한 있는 비공개 원본에서 본인 author/committer 변경과 사용자 식별, 화면별 최신 기준, 사용자 전용 조회 경로, null-safe 최신 기준 처리 범위를 재확인한 뒤 회사 원본과 독립된 합성 샘플로 재현했습니다.

검증된 공개 샘플 경계:

- Java 21 + Spring Boot 3.5.16
- session canonical `subjectId` 우선과 canonical 부재 시 제한적 legacy fallback
- 인증 주체 부재 `401` fail-closed
- `LATEST_ONLY`에서 요청 year/month가 결과 기준을 바꾸지 않음
- `EXPLICIT_OR_LATEST`에서 완전한 year/month만 명시 snapshot으로 허용
- Service가 확정한 `subjectId + SnapshotKey`만 Mapper에 전달
- 잘못된/불완전한 기간 `400`, snapshot 부재 `404`
- request parameter로 session identity를 덮어쓸 수 없음
- 11개 MockMvc 자동 테스트
- sample/regression run `33275860098`: `Business-rule consistency` job과 전체 8개 portfolio job PASS
- final PR regression run `33276143715`: public/Jekyll/Spring Security/MyBatis/WAR/Business-rule/OpsMate/container-runbook 8개 job 전체 PASS
- main commit `733db7c614af5613216773b3b1fc6b3567e0b84c`
- main Pages run `33276278894`: 8개 verify + build + deploy PASS

검증하지 않은 것: 실제 회사 SSO/session E2E, 회사 Mapper SQL/운영 DB 결과, 운영 데이터 전체 정합성, 조직 전체 업무 규칙 설계 책임, 운영 트래픽/SLA.

상세: [`../02_projects/case-study-samples/business-rule-consistency/VERIFICATION.md`](../02_projects/case-study-samples/business-rule-consistency/VERIFICATION.md)

## `CS-AI-01` Text2SQL Workspace 공개 재현 증거

비공개 업무 Text2SQL/NL2SQL 구현에서 확인된 문제 범위를 회사 코드와 분리하고, 별도 공개 프로젝트 [`Text2SQL Workspace`](https://github.com/son1004007/text2sql-workspace)로 독립 구현했습니다.

검증된 공개 샘플 경계:

- Python 3.13 + FastAPI
- synthetic signed bearer identity와 사용자별 workspace/query ownership
- cross-user workspace/query 접근 거부
- replaceable `Text2SqlModel` interface와 deterministic fixture model
- natural-language question -> SQL candidate -> SQLGlot validation -> read-only execution -> result
- exactly one statement, SELECT/query-only, explicit analytics table allowlist
- unsafe fixture SQL은 query executor 호출 전에 차단
- query/attempt history와 retry 관계 보존
- generation / validation / execution / correctness 상태와 평가 지표 분리
- SQL string equality가 아닌 columns/rows result semantics 기반 correctness
- SQLite read-only deterministic adapter + PostgreSQL read-only runtime adapter
- PostgreSQL 17 Docker runtime의 dedicated analytics reader
- 동일 reader direct `SELECT` 성공, `INSERT` 실패
- explicit read-only PostgreSQL transaction, bounded rows, statement timeout
- application metadata state와 analytics query authority 분리
- non-root FastAPI container
- host에는 FastAPI loopback binding만 publish하고 PostgreSQL port는 publish하지 않음
- clean-volume Docker E2E에서 API health, 두 사용자 격리, safe query, unsafe rejection, evaluation, network exposure와 DB privilege를 검증
- public project security/disclosure review PASS
- public project main CI에서 Python test와 Docker/PostgreSQL E2E 모두 PASS (`2026-08-31`)

runtime gate에서 SQLite-only 경로가 드러내지 못한 PostgreSQL numeric aggregation/type 차이를 실제로 발견해 synthetic money 타입과 cross-engine result comparison을 수정했습니다.

현재 deterministic evaluation fixture는 2개의 작은 합성 case가 generation/validation/execution/correctness pipeline을 통과하는지 검증합니다. 이것을 외부 LLM의 100% Text2SQL accuracy로 표현하지 않습니다.

검증하지 않은 것:

- production authentication 또는 external IdP
- external/real LLM E2E와 statistically meaningful model-quality metrics
- arbitrary production database connector
- production concurrency, load, SLA, large-user operation
- 실제 회사 schema/query/data와 운영 정확도

상세: [Text2SQL Workspace README](https://github.com/son1004007/text2sql-workspace) | [포트폴리오 사례](case-studies/text2sql-validation.md)

## 회사 GitHub 실무 근거

원본은 회사 소유 비공개 저장소이며 공개 저장소에는 코드와 내부 식별자를 복사하지 않습니다. 상세 공개 범위와 claim은 [`../evidence/company-github/README.md`](../evidence/company-github/README.md)에서 확인합니다.

| project ID | 확인된 범위 | 상태 | 제한 |
|---|---|---|---|
| `WORK-AI-01` | FastAPI Text2SQL/NL2SQL, 모델 adapter, SQL 검증·실행, benchmark와 결과 기록 | `private-work-code-verified`, `tested-component`, `E3` | PoC 범위. 모델 연구 및 장기 운영으로 확대 금지 |
| `WORK-AI-02` | Agentic AI Runtime의 workspace, artifact, provenance, storage와 테스트 | `private-work-code-verified`, `tested-component`, `E3` | 전체 플랫폼 및 end-to-end 모델 연동 완료로 확대 금지 |
| `WORK-AI-04` | FastAPI, Docker Compose, vLLM 기반 다중 모델 gateway pilot | `implemented`, `E2` | 테스트 및 운영 검증 완료로 확대 금지 |
| `WORK-AI-03` | LLM 분석 workflow와 RAG 확장 로드맵 | `designed`, `E2` | 구현 완료로 사용 금지 |
| `WORK-PLATFORM-01` | 데이터 서비스 웹/API/DB 연계와 운영 개선의 지속적 작성 커밋 | `implemented`, `E2` | 프로젝트별 역할과 outcome은 추가 대조 필요 |
| `WORK-DATA-01` | 운영 데이터 서비스와 분석 배치 및 ModelOps 개선 | `implemented`, `E2` | 자동 테스트와 성능 개선 수치로 확대 금지 |
| `WORK-DATA-02` | 재현 가능한 데이터 분석 pipeline, CLI, SQL runner와 검증 테스트 | `tested-component`, `E3` | 독립 재실행 및 CI 성공은 미확인 |
| `WORK-DATA-03` | Java/Spring 통계 품질 분석 화면의 데이터 처리와 시각화 일부 | `implemented`, `E2` | 직접 기여 일부만 사용; 전체 통계 시스템·정확도·운영 성과로 확대 금지 |
| `WORK-EDU-01` | 데이터 플랫폼 인증, 분석 화면/필터, 제한형 배포·검증·health/rollback 기반 | `implemented`, `E2` | 전체 플랫폼 단독 구현 또는 자동화 운영 성과로 확대 금지 |
| `WORK-INTEGRATION-01` | 문서 파싱, dry-run, DB 확인과 CSV export 도구 | `implemented`, `E2` | 전체 운영 pipeline 완료로 확대 금지 |
| `WORK-OPS-01` | Linux 원격 개발환경, 배포 자동화, Git 운영 규칙과 실행 가이드 | `implemented`, `E2` | 조직 전체 표준 수립 또는 운영 책임자로 확대 금지 |

## 공개 프로필 주장

| 주장 | 현재 분류 | 안전한 표현 | 추가 근거 |
|---|---|---|---|
| Java/Spring 백엔드 개발 경험 | `self-described` + 독립 공개 사례 | Java/Spring 기반 업무 경험과 독립 샘플 검증을 분리해 제시 | 회사·프로젝트 기간, 역할, 코드·산출물 색인 |
| Python/FastAPI API 구현 | `self-described` + `sample-verified` 공개 Text2SQL | 실무 자기기술과 독립 FastAPI API/runtime evidence를 분리해 제시 | production auth/load 범위는 별도 근거 전 주장 금지 |
| 데이터 분석 결과 서비스화 | `self-described` | 분석 결과를 웹/API로 연결한 경험을 경력 문서에 기재 | 프로젝트별 역할, 기간, 운영 반영, 성과 |
| Text2SQL/NL2SQL/LLM PoC | `private-work-code-verified`, `E3` + 독립 공개 `sample-verified` | 실무 Text2SQL 구현 경험과 공개 synthetic Text2SQL service/runtime 검증을 함께 제시하되 서로 다른 evidence로 구분 | external real-model accuracy와 운영 반영 범위는 별도 확인 |
| Agentic AI Runtime | `private-work-code-verified`, `E3` for components | 작업 격리와 artifact/provenance 추적 구성요소 및 테스트 구현 | 전체 플랫폼 완성 또는 LLMOps lead로 표현 금지 |
| Linux/Docker/Jenkins 운영 반영 | `self-described` | 배포·환경 구성·장애 분석 경험을 경력 문서에 기재 | 운영 범위, 책임, 자동화, 장애·개선 결과 |
| 보안 경력과 자격 | `self-described` | 보안·통제 관점을 가진 백엔드 전환형 프로필 | 재직 연표, 자격 유효 상태, 실제 담당 업무 |

## 최근 완료된 공개 재현 작업

현재 독립 공개 재현 evidence는 Java/Spring 사례 4건의 publication gate와 Python/FastAPI Text2SQL 샘플의 runtime verification을 포함합니다.

- `CS-JAVA-01`: Spring Security 인증·인가·세션·CSRF, 24개 테스트 + main Pages `33276912458`
- `CS-JAVA-02`: MyBatis 기간 정합성, 12개 테스트 + main Pages `33251362190`
- `CS-JAVA-03`: WAR 배포 이식성, 10개 테스트 + main Pages `33252148733`
- `CS-JAVA-06`: 업무 규칙 정합성, 11개 테스트 + main Pages `33276278894`
- `CS-AI-01`: Python/FastAPI Text2SQL, public project main Python test + Docker/PostgreSQL E2E PASS (`2026-08-31`), portfolio Pages publication pending
- 공통 금지: 회사 코드·테이블·식별자·데이터 복사, 팀 전체 결과나 실제 운영 성과로의 확대 해석

## 현재 판단

이 저장소는 다음을 보여주기에 적합합니다.

- Java/Spring Security 인증·인가·세션·CSRF 경계를 독립 샘플과 테스트로 검증하는 방식
- MyBatis 기간 조회를 결과 정합성, tenant isolation, count/page 일치, deterministic pagination과 index-friendly SQL shape로 검증하는 방식
- WAR 서비스의 external-container bootstrap, non-root context path, 외부 config fail-closed와 health rollback을 검증하는 방식
- canonical session identity, 화면별 snapshot policy, Service-Mapper responsibility와 fail-closed를 회귀 테스트로 고정하는 방식
- Python/FastAPI 멀티사용자 Text2SQL API에서 사용자 ownership, model output validation, PostgreSQL read-only privilege와 result-based evaluation을 분리해 검증하는 방식
- SQLite-only 검증에 머물지 않고 Docker/PostgreSQL runtime에서 엔진 호환성, 네트워크 노출과 database privilege를 확인하는 방식
- AI 출력을 Spring 업무 규칙·승인·멱등성·fail-closed 경계 안에 두고 workspace·모델 호출량·DB 권한·서비스 수명주기까지 통제하는 설계와 구현
- 실제 오픈웨이트 모델 구조화 출력을 Spring 서버 검증·저장 경계까지 연결한 E2E
- immutable deployment artifact, private DB/model network, restricted tunnel, public HTTPS ingress, rate/session/egress boundary와 normal/emergency lifecycle bounded E2E
- 목표하는 엔지니어 정체성과 Java/Python/AI 응용 기술 조합
- 비공개 업무 근거를 비식별 claim과 공개 재현 상태로 나누는 검수 방식

다음에는 아직 충분하지 않습니다.

- 프로덕션 수준 백엔드·플랫폼 숙련도 전체 증명
- 대규모 트래픽·분산 시스템·클라우드 네이티브 운영 증명
- OpsMate의 24x7 Internet 운영, SLA, 장기 부하와 production traffic 규모 증명
- Spring Security 샘플의 실제 외부 IdP/운영 DB/분산 세션/운영 부하 증명
- MyBatis 샘플의 실제 Oracle 실행계획 또는 운영 성능 증명
- WAR 샘플의 실제 외부 Tomcat zero-downtime/session-drain/SLA 증명
- 업무 규칙 샘플의 실제 회사 SSO/session, Mapper SQL/운영 DB, 운영 데이터 전체 정합성 증명
- Text2SQL 샘플의 external real LLM 정확도, production IdP, arbitrary DB, concurrency/load/SLA 증명
- 프로젝트별 정량 성과와 본인 기여 범위 전체 증명

## 갱신 규칙

프로젝트 상태를 올릴 때는 해당 boundary의 최근 명령/환경/성공 결과를 함께 기록합니다. bounded component 또는 deployment 검증을 더 넓은 production 운영·SLA 주장으로 자동 확장하지 않습니다.

경력 주장에는 가능한 경우 다음을 연결합니다.

```text
기간
역할과 개인 기여
문제와 구현
운영 반영 여부
검증된 성과
공개 가능한 산출물 또는 비식별 근거 색인
```
