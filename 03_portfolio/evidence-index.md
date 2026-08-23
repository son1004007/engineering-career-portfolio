# Portfolio Evidence Index

- 최종 점검일: `2026-08-23`
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
- 마지막 전체 공개본 검증일: `2026-08-03`
- 원격 결과: [Pages Actions run 30782896966](https://github.com/son1004007/engineering-career-portfolio/actions/runs/30782896966)의 Jekyll build, Java 2개 프로젝트, 공개 저장소 검사와 deploy 성공
- 테스트: 당시 Spring Security 인증 브리지 24개, OpsMate Local 19개, 저장소 공개 검수 12개 성공
- 실화면: 홈, Work ledger, 근거 인덱스, 전략과 앵커, 체크리스트, AI context, 대표 프로젝트·사례 경로의 응답과 가로 넘침 없음 확인
- 이후 증거: OpsMate는 `2026-08-04` 전체 `clean verify` 54개 성공, `2026-08-23` 실제 `gemma3:12b` E2E 9/9와 p95 gate 성공
- 한계: 최신 commit 기준 통합 regression, 물리 모바일 최종 검수와 OpsMate public application 외부 배포 gate는 별도 수행 필요

## 프로젝트 상태

| 프로젝트 | 코드 | 테스트 | 문서 | 현재 상태 | 확인 사항 |
|---|---:|---:|---:|---|---|
| [OpsMate Local](../02_projects/opsmate-local/README.md) | 수직 기능, 공개 웹, workspace/model guard, PostgreSQL·배포 자산 있음 | 2026-08-04 `clean verify` 54개 성공. 2026-08-23 실제 `gemma3:12b` E2E 9/9, p95 21,076ms <= 30,000ms | README·ARCHITECTURE·SETUP·PUBLIC_DEMO·THREAT_MODEL·SERVICE_RUNBOOK·REAL_MODEL_E2E_EVIDENCE | `implemented`, `tested-component`; real-model boundary `verified` | public application URL·외부 smoke, host egress/edge rate limit, DB/model 비노출과 양 호스트 close/reopen rehearsal은 미검증 |
| [Spring Security 인증 브리지](../02_projects/case-study-samples/spring-security-auth-bridge/README.md) | 독립 공개 샘플 있음 | 24개 성공 | [사례 게시물](case-studies/spring-security-auth-bridge.md)·README·ARCHITECTURE·SETUP·VERIFICATION | `sample-verified` | 회사 원본이 아닌 합성 사용자·issuer·audience 바인딩 SSO 샘플. 운영 시스템 검증을 뜻하지 않음 |
| [Java/Spring 사례 후보](case-study-index.md) | 회사 원본에서 확인 | 공개 샘플 1건만 있음 | 후보 인덱스 있음 | `sample-verified` 1건, `source-reviewed` 4건, 나머지 `candidate` 또는 `hold` | 원본 코드를 공개할 수 없으며 각 독립 재현 코드와 테스트가 생기기 전 `published` 표현 금지 |
| [`ai-rag-api`](../02_projects/ai-rag-api/README.md) | 있음 | 있음 | README·ARCHITECTURE·SETUP | `implemented`, `tested-file-present` | 현재 점검 환경에서 최근 성공 실행을 확인하지 않음. 실제 LLM·벡터 저장소 품질과 운영성 별도 검증 필요 |
| [`backend-platform-template`](../02_projects/backend-platform-template/README.md) | 일부 | 있음 | README·ARCHITECTURE·SETUP | `partial` | `app/main.py`가 존재하지 않는 `app.api.routes`를 import. 인증·모니터링·PostgreSQL·Redis 구현도 현재 파일 목록에서 확인되지 않음 |
| [`security-audit-log`](../02_projects/security-audit-log/README.md) | route만 있음 | 없음 | README | `partial` | route가 존재하지 않는 `app.service.audit_service`를 import. 앱 진입점, 저장 모델, service, tests 보완 필요 |
| 개인 공개 `text2sql` 샘플 | 없음 | 없음 | 상위 문서의 계획만 있음 | `planned` | 회사 실무 Text2SQL과 혼동 금지. 공개 샘플 프로젝트 디렉터리는 아직 없음 |
| `security-backend-platform` | 없음 | 없음 | 상위 문서의 예시만 있음 | `planned` | 별도 프로젝트로 완료되기 전 기술 근거로 사용 금지 |

## OpsMate 실제 모델 E2E 증거

`2026-08-23` 사설 GPU 호스트에서 source commit `ff67df0990cbed3a41cf5051a5e2701a7b2a7b50`의 실제 모델 gate를 실행했습니다.

- Ollama `0.13.5`
- model `gemma3:12b`
- 합성 구매 요청 9건 중 9건 성공
- 실제 `/api/chat` 구조화 출력, JSON 역직렬화와 서버 측 category·policy 검증 통과
- 요청·감사 이벤트 저장 건수 각각 9건
- 관측 p95 `21,076ms`
- gate `p95 <= 30,000ms`
- Maven exit code `0`
- 실행 직후 모델은 GPU 100% 사용으로 관측됨

상세 환경·명령·한계는 [`../02_projects/opsmate-local/docs/REAL_MODEL_E2E_EVIDENCE.md`](../02_projects/opsmate-local/docs/REAL_MODEL_E2E_EVIDENCE.md)를 기준으로 합니다. 이 결과는 실제 모델 adapter 경계의 검증이며 public network/deployment/lifecycle 전체 검증을 의미하지 않습니다.

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
| `WORK-EDU-01` | 데이터 플랫폼 기능 개선과 제한형 배포 자동화 | `implemented`, `E2` | tests와 운영 성과는 별도 확인 |
| `WORK-INTEGRATION-01` | 문서 파싱, dry-run, DB 확인과 CSV export 도구 | `implemented`, `E2` | 전체 운영 pipeline 완료로 확대 금지 |

## 공개 프로필 주장

| 주장 | 현재 분류 | 안전한 표현 | 추가 근거 |
|---|---|---|---|
| Java/Spring 백엔드 개발 경험 | `self-described` | Java/Spring 기반 업무·데이터 서비스 개발 경험을 경력 문서에 기재 | 회사·프로젝트 기간, 역할, 코드·산출물 색인 |
| Python/FastAPI API 구현 | `self-described` + 개인 코드 `implemented` | 실무 자기기술과 FastAPI 포트폴리오 코드가 각각 존재 | 실무 범위와 개인 샘플을 분리한 근거 |
| 데이터 분석 결과 서비스화 | `self-described` | 분석 결과를 웹/API로 연결한 경험을 경력 문서에 기재 | 프로젝트별 역할, 기간, 운영 반영, 성과 |
| Text2SQL/NL2SQL/LLM PoC | `private-work-code-verified`, `E3`; RAG 개인 샘플 `implemented` | FastAPI 기반 Text2SQL/NL2SQL 구현과 다중 모델 benchmark 경험. RAG 개인 샘플은 별도 표시 | 운영 반영 범위와 장기 성과는 별도 확인 |
| Agentic AI Runtime | `private-work-code-verified`, `E3` for components | 작업 격리와 artifact/provenance 추적 구성요소 및 테스트 구현 | 전체 플랫폼 완성 또는 LLMOps lead로 표현 금지 |
| Linux/Docker/Jenkins 운영 반영 | `self-described` | 배포·환경 구성·장애 분석 경험을 경력 문서에 기재 | 운영 범위, 책임, 자동화, 장애·개선 결과 |
| 보안 경력과 자격 | `self-described` | 보안·통제 관점을 가진 백엔드 전환형 프로필 | 재직 연표, 자격 유효 상태, 실제 담당 업무 |

## 현재 판단

이 저장소는 다음을 보여주기에 적합합니다.

- Java/Spring Security 인증·인가·세션·CSRF 경계를 독립 샘플과 테스트로 검증하는 방식
- AI 출력을 Spring 업무 규칙·승인·멱등성·fail-closed 경계 안에 두고 workspace·GPU 호출량·DB 권한·서비스 수명주기까지 통제하는 설계와 구현
- 실제 오픈웨이트 모델의 구조화 출력을 Spring 서버 검증과 저장 경계까지 연결한 제한된 E2E 증거
- 목표하는 엔지니어 정체성과 Java/Python/AI 응용 기술 조합
- 비공개 업무 근거를 비식별 claim과 공개 재현 상태로 나누는 검수 방식
- 문서화와 구조화 능력

다음에는 아직 충분하지 않습니다.

- 프로덕션 수준 백엔드·플랫폼 숙련도 증명
- 대규모 트래픽·분산 시스템·클라우드 네이티브 운영 증명
- OpsMate public application의 실제 인터넷 배포와 장기 안정성 증명
- public URL, 외부 네트워크 정책과 앱·모델 양쪽 호스트 close/reopen 증명
- 프로젝트별 정량 성과와 본인 기여 범위 증명
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

전체 서비스가 여러 외부 gate를 가지면 컴포넌트별 `verified`와 전체 상태를 분리합니다. 예를 들어 실제 모델 E2E가 성공해도 public network와 lifecycle rehearsal이 남아 있으면 프로젝트 전체는 `tested-component`를 유지합니다.

경력 주장에는 가능한 경우 다음을 연결합니다.

```text
기간
역할과 개인 기여
문제와 구현
운영 반영 여부
검증된 성과
공개 가능한 산출물 또는 비식별 근거 색인
```
