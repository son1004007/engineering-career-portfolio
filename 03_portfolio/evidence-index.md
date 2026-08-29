# Portfolio Evidence Index

- 최종 점검일: `2026-08-29`
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
- OpsMate public deployment E2E 문서 반영 main Pages run `33250726427`: verify matrix, Jekyll build와 Pages deploy 모두 성공 (`2026-08-29`)
- 물리 모바일 최종 UX 검수는 유지관리 gate로 남음

## 프로젝트 상태

| 프로젝트 | 코드 | 테스트 | 문서 | 현재 상태 | 확인 사항 |
|---|---:|---:|---:|---|---|
| [OpsMate Local](../02_projects/opsmate-local/README.md) | 수직 기능, 공개 웹, workspace/model guard, PostgreSQL, restricted tunnel, 배포/lifecycle 자산 있음 | 실제 `gemma3:12b` 9/9 E2E + Synology internal E2E + public Internet network/security/lifecycle bounded E2E 성공 | README·ARCHITECTURE·SETUP·PUBLIC_DEMO·THREAT_MODEL·SERVICE_RUNBOOK·REAL_MODEL_E2E_EVIDENCE·NAS_INTERNAL_E2E_EVIDENCE·PUBLIC_DEPLOYMENT_E2E_EVIDENCE | `implemented`, `tested-component`; real-model adapter, NAS internal boundary, public deployment/network/security/lifecycle boundary `verified` | 24x7 SLA, 장기 부하, 대규모 실제 사용자 운영은 검증/주장하지 않음. 검증 종료 후 workload `CLOSED` |
| [Spring Security 인증 브리지](../02_projects/case-study-samples/spring-security-auth-bridge/README.md) | 독립 공개 샘플 있음 | 24개 성공 | [사례 게시물](case-studies/spring-security-auth-bridge.md)·README·ARCHITECTURE·SETUP·VERIFICATION | `sample-verified` | 합성 사용자·issuer·audience 바인딩 SSO 샘플. 실제 회사 시스템 검증을 뜻하지 않음 |
| [MyBatis 기간 조회 정합성](../02_projects/case-study-samples/mybatis-query-correctness/README.md) | 독립 Spring Boot/MyBatis/H2 샘플 있음 | 12개 성공; PR run `33251026033`의 `MyBatis query correctness` job 성공 | [사례 게시물](case-studies/mybatis-query-correctness.md)·README·ARCHITECTURE·SETUP·VERIFICATION | `sample-verified` | 권한 있는 비공개 원본에서 본인 귀속 SQL 개선 범위를 재확인한 뒤 설계 원리만 합성 재구현. Oracle 실행계획·운영 성능 수치는 미검증 |
| [Java/Spring 사례 후보](case-study-index.md) | 회사 원본에서 확인 | 공개 독립 샘플 2건 검증 | 후보 인덱스 있음 | `sample-verified` 2건, `source-reviewed` 다수 | 원본 코드를 공개하지 않으며 독립 재현·최근 테스트가 없는 사례를 완료로 표현하지 않음 |
| [`ai-rag-api`](../02_projects/ai-rag-api/README.md) | 있음 | 있음 | README·ARCHITECTURE·SETUP | `implemented`, `tested-file-present` | 현재 점검 환경에서 최근 성공 실행 미확인. 실제 LLM·벡터 저장소 품질과 운영성 별도 검증 필요 |
| [`backend-platform-template`](../02_projects/backend-platform-template/README.md) | 일부 | 있음 | README·ARCHITECTURE·SETUP | `partial` | `app/main.py`가 존재하지 않는 `app.api.routes`를 import. 인증·모니터링·PostgreSQL·Redis 구현도 현재 파일 목록에서 확인되지 않음 |
| [`security-audit-log`](../02_projects/security-audit-log/README.md) | route만 있음 | 없음 | README | `partial` | route가 존재하지 않는 `app.service.audit_service`를 import. 앱 진입점, 저장 모델, service, tests 보완 필요 |
| 개인 공개 `text2sql` 샘플 | 없음 | 없음 | 상위 문서의 계획만 있음 | `planned` | 회사 실무 Text2SQL과 혼동 금지 |
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

- DSM Reverse Proxy/TLS + router ingress를 통한 public HTTPS root/live marker: PASS
- public `/api/**` denial, `/actuator/**` 차단: PASS
- 실제 모델 기반 draft -> submit -> approve -> order -> audit -> cleanup: PASS
- 외부 두 session cross-workspace isolation: PASS
- URL `;jsessionid` rewriting: absent
- app direct Internet egress: blocked
- 외부 PostgreSQL/model/loopback edge 직접 TCP 노출: closed
- bounded rate burst 60건: allowed `24`, HTTP `429` `36`, transport failure `0`
- credential/private-key/Bearer marker log scan: PASS
- normal close 후 public live marker absent, running container `0`, PostgreSQL volume preserved, strict `CLOSED`
- same immutable digest reopen 후 public HTTPS + 실제 모델 smoke 재통과
- emergency close 후 public live marker absent, strict `CLOSED`
- recovery normal close/purge 후 final `runtime_policy_flags=YES_YES`, final `CLOSED`

상세: [`../02_projects/opsmate-local/docs/PUBLIC_DEPLOYMENT_E2E_EVIDENCE.md`](../02_projects/opsmate-local/docs/PUBLIC_DEPLOYMENT_E2E_EVIDENCE.md)

이 증거는 bounded deployment/network/security/lifecycle 검증입니다. 24x7 SLA, 장기 부하, DDoS/WAF 전문 방어, production traffic 규모를 의미하지 않습니다.

## 두 번째 Java/Spring 공개 재현 증거

`CS-JAVA-02`는 권한 있는 비공개 원본에서 다음 범위의 본인 귀속 변경을 재확인한 뒤 독립 구현했습니다.

- 여러 연도 기간 조건의 복합 `OR`을 서로 겹치지 않는 시작/중간/종료 구간으로 분해
- 구간을 `UNION ALL`로 결합
- 검색·정렬 year/month column의 숫자 변환 제거

공개 구현은 회사 SQL·schema·식별자·데이터를 사용하지 않습니다.

검증된 공개 샘플 경계:

- Java 21 + Spring Boot 3.5.16 + MyBatis Spring Boot Starter 3.0.5
- H2 in-memory 합성 schema/data
- 같은 연도 및 여러 연도 시작·종료 경계
- 누락·중복 부재와 tenant isolation
- count/page 공통 filter semantics
- deterministic pagination
- invalid range/month/page/size/tenant validation
- `(tenant_id, snapshot_year, snapshot_month)` 합성 복합 인덱스 존재
- MyBatis `BoundSql`에서 same-year/cross-year SQL shape 확인
- indexed year/month column 변환 함수 부재와 기간 `OR` 부재
- `./mvnw -q clean verify` 성공, 12개 테스트

검증하지 않은 것:

- 실제 Oracle optimizer의 index 선택
- 회사 운영 실행계획 원문
- elapsed time, CPU, logical read 개선률
- production traffic 성능

상세: [`../02_projects/case-study-samples/mybatis-query-correctness/VERIFICATION.md`](../02_projects/case-study-samples/mybatis-query-correctness/VERIFICATION.md)

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
| Java/Spring 백엔드 개발 경험 | `self-described` + 독립 샘플 `sample-verified` | Java/Spring 기반 업무·데이터 서비스 개발 경험과 공개 검증 샘플을 분리해 제시 | 회사·프로젝트 기간, 역할, 코드·산출물 색인 |
| Python/FastAPI API 구현 | `self-described` + 개인 코드 `implemented` | 실무 자기기술과 FastAPI 포트폴리오 코드가 각각 존재 | 실무 범위와 개인 샘플 분리 |
| 데이터 분석 결과 서비스화 | `self-described` | 분석 결과를 웹/API로 연결한 경험을 경력 문서에 기재 | 프로젝트별 역할, 기간, 운영 반영, 성과 |
| Text2SQL/NL2SQL/LLM PoC | `private-work-code-verified`, `E3`; RAG 개인 샘플 `implemented` | FastAPI 기반 Text2SQL/NL2SQL 구현과 다중 모델 benchmark 경험 | 운영 반영 범위와 장기 성과는 별도 확인 |
| Agentic AI Runtime | `private-work-code-verified`, `E3` for components | 작업 격리와 artifact/provenance 추적 구성요소 및 테스트 구현 | 전체 플랫폼 완성 또는 LLMOps lead로 표현 금지 |
| Linux/Docker/Jenkins 운영 반영 | `self-described` | 배포·환경 구성·장애 분석 경험을 경력 문서에 기재 | 운영 범위, 책임, 자동화, 장애·개선 결과 |
| 보안 경력과 자격 | `self-described` | 보안·통제 관점을 가진 백엔드 전환형 프로필 | 재직 연표, 자격 유효 상태, 실제 담당 업무 |

## 현재 active 공개 재현 작업

`CS-JAVA-02`는 독립 샘플 구현과 Maven 검증까지 완료해 `sample-verified`입니다.

- 검증: 12개 테스트, PR run `33251026033`의 `MyBatis query correctness` job 성공
- 남은 gate: 전체 PR regression + main merge + GitHub Pages 게시·링크 확인
- 금지: 회사 SQL·스키마·식별자·데이터 복사, 확인되지 않은 Oracle 운영 성능 수치 사용
- Pages 게시 후 다음 `source-reviewed` Java/Spring 사례를 선택한다.

## 현재 판단

이 저장소는 다음을 보여주기에 적합합니다.

- Java/Spring Security 인증·인가·세션·CSRF 경계를 독립 샘플과 테스트로 검증하는 방식
- MyBatis 기간 조회를 결과 정합성, tenant isolation, count/page 일치, deterministic pagination과 index-friendly SQL shape로 검증하는 방식
- AI 출력을 Spring 업무 규칙·승인·멱등성·fail-closed 경계 안에 두고 workspace·모델 호출량·DB 권한·서비스 수명주기까지 통제하는 설계와 구현
- 실제 오픈웨이트 모델 구조화 출력을 Spring 서버 검증·저장 경계까지 연결한 E2E
- immutable deployment artifact, private DB/model network, restricted tunnel, public HTTPS ingress, rate/session/egress boundary와 normal/emergency lifecycle을 실제 Synology target에서 bounded E2E로 검증한 증거
- 목표하는 엔지니어 정체성과 Java/Python/AI 응용 기술 조합
- 비공개 업무 근거를 비식별 claim과 공개 재현 상태로 나누는 검수 방식
- 문서화와 구조화 능력

다음에는 아직 충분하지 않습니다.

- 프로덕션 수준 백엔드·플랫폼 숙련도 전체 증명
- 대규모 트래픽·분산 시스템·클라우드 네이티브 운영 증명
- OpsMate의 24x7 Internet 운영, SLA, 장기 부하와 production traffic 규모 증명
- MyBatis 샘플의 실제 Oracle 실행계획 또는 운영 성능 증명
- 프로젝트별 정량 성과와 본인 기여 범위 전체 증명
- 모든 README에 적힌 기능의 실제 구현 증명

## 갱신 규칙

프로젝트 상태를 올릴 때는 다음 근거를 함께 추가합니다.

```text
partial -> implemented:
실행에 필요한 핵심 모듈과 진입점 추가

implemented -> tested-file-present:
핵심 경로 테스트 파일 추가

tested-file-present -> verified:
최근 테스트 명령, 성공 결과, 환경·버전 기록
```

전체 서비스가 여러 외부 gate를 가지면 컴포넌트별 `verified`와 전체 상태를 분리합니다. bounded public deployment E2E가 성공해도 24x7 SLA나 장기 부하까지 자동으로 확장하지 않습니다.

경력 주장에는 가능한 경우 다음을 연결합니다.

```text
기간
역할과 개인 기여
문제와 구현
운영 반영 여부
검증된 성과
공개 가능한 산출물 또는 비식별 근거 색인
```
