# Portfolio Completion Plan

- 기준일: `2026-08-30`
- 목적: 공개 포트폴리오의 검증 상태를 실제 실행 증거와 동기화하고, 특정 언어나 framework가 아니라 capability gap을 기준으로 다음 공개 evidence를 선택한다.
- 원칙: 문서상 완료가 아니라 독립 구현, 최근 CI와 필요한 runtime/publication evidence를 근거로 상태를 올린다.
- 전역 기준: `son1004007/ai-agent-workflow-playbook/CONTROL.md`
- 실행 ledger: [`WORKS.md`](WORKS.md)
- 장기 backlog: [`TASKS.md`](TASKS.md)
- 포트폴리오 전략: [`03_portfolio/portfolio-strategy.md`](03_portfolio/portfolio-strategy.md)
- 개발 방식: [`HOW_I_ENGINEER.md`](HOW_I_ENGINEER.md)

## 상태

| 상태 | 의미 |
|---|---|
| `pending` | 아직 시작하지 않음 |
| `in-progress` | 현재 수행 중 |
| `blocked-user` | 사용자만 가능한 입력/승인 대기 |
| `blocked-external` | 외부 서비스/네트워크/runtime 조건 대기 |
| `verified` | 요구된 검증 증거까지 확보 |
| `published` | 공개 Pages 배포까지 검증됨 |

## 현재 완료된 핵심 evidence

### Controlled AI Integration - OpsMate Local

- 실제 `gemma3:12b` 모델을 사용한 9개 핵심 합성 업무 시나리오 E2E 전건 성공
- Synology internal bounded E2E: PASS
- public Internet bounded E2E: PASS
- 최종 runtime 상태: `CLOSED`
- 세부 응답시간과 gate는 프로젝트 evidence 문서에서 확인

이 evidence는 bounded E2E이며 24x7 SLA, 장기 부하 또는 대규모 production traffic을 의미하지 않습니다.

### Engineering Problem Case Studies

| ID | 문제 중심 표현 | 공개 검증 | 상태 |
|---|---|---:|---|
| `CS-JAVA-01` | 사용자 로그인과 권한을 안전하게 통합 | 24 tests | `published` |
| `CS-JAVA-02` | 복잡한 기간 조회에서 데이터 정합성 유지 | 12 tests | `published` |
| `CS-JAVA-03` | 환경이 달라도 배포하고 복구할 수 있는 구조 | 10 tests | `published` |
| `CS-JAVA-06` | 사용자 식별과 업무 기준을 여러 계층에서 일관되게 유지 | 11 tests | `published` |

ID는 기존 evidence traceability를 위해 유지하지만, 공개 제목과 포지셔닝은 framework보다 문제를 먼저 보여줍니다.

## 실행 순서

### P00. OpsMate 상태와 공개 evidence 동기화 - `verified`

- [x] 실제 모델 E2E 기록
- [x] Synology internal deployment/network/security/lifecycle E2E 기록
- [x] public Internet deployment/network/lifecycle E2E 기록
- [x] AI context, work ledger, evidence index와 public evidence 동기화
- [x] public Pages evidence 반영

사용자 작업: 없음.

### P10. Repository regression - `verified`

- [x] OpsMate Maven regression
- [x] 공개 case-study sample regression
- [x] 공개 링크·credential·상태 정합성 검사
- [x] Jekyll build
- [x] shell/Compose/Nginx/container/runbook 검증
- [x] non-root image build 및 migration rehearsal

사용자 작업: 없음.

### P20. Capability-first 포지셔닝 - `verified`

첫 화면과 AI 해석 규칙을 다음 순서로 유지합니다.

```text
문제와 결과
-> 엔지니어링 역량
-> 검증된 evidence
-> 기술 세부
-> 사용하는 언어와 framework
```

독립 Codex/Gemini 검토 결과를 반영해 다음 원칙을 추가했습니다.

- `Backend Engineer`를 주 역할로 고정
- Java/Spring과 Python/FastAPI 모두 실제 사용 기술이지만 공개 재현 evidence 강도는 구분
- `RAG/Agent patterns`, `observability`처럼 공개 검증보다 앞선 상위 용어는 첫 화면에서 제거하거나 근거가 있는 문맥에서만 사용
- 첫 화면에서 p95 같은 세부 성능 수치보다 실제 시나리오와 실패 차단 경계를 우선

### P30. 사용자 로그인과 권한 통합 사례 - `published`

- 회사 코드와 독립된 Java 21 / Spring Boot 합성 인증 샘플
- 인증 수렴, RBAC, session/CSRF, replay, fail-closed 검증
- 자동 테스트 24개 성공
- Pages publication evidence 존재

### P40. 복잡한 기간 조회 데이터 정합성 사례 - `published`

- Spring Boot/MyBatis/H2 독립 합성 샘플
- 기간 경계, tenant isolation, count/page, deterministic pagination 검증
- 자동 테스트 12개 성공

### P50. 환경이 달라도 배포하고 복구할 수 있는 구조 - `published`

- Spring Boot WAR 독립 샘플
- 외부 설정, health check, rollback rehearsal
- 자동 테스트 10개 성공

### P60. 업무 규칙 일관성 사례 - `published`

- session identity와 fallback 경계
- latest-only / explicit-or-latest 정책
- service/data-access 입력 소유권과 400/401/404 fail-closed 검증
- 자동 테스트 11개 성공

### P70. 다음 공개 evidence 선택 - `verified`

추가 Java/Spring 사례는 현재 core portfolio에 필요하지 않습니다.

독립 Codex/Gemini 검토와 현재 공개 evidence를 대조한 결과, 다음 capability gap은 **Python/FastAPI 기반 Data / AI Service Integration의 독립 공개 재현성**으로 확정했습니다.

선택한 방향:

> 하나의 독립 실행 가능한 Python/FastAPI 백엔드 샘플로 자연어 질의, SQL 검증·실행과 AI 평가 경계를 보여준다.

선택 이유:

1. 현재 공개 완료 샘플은 Java/Spring 쪽이 더 강함
2. Python/FastAPI와 Text2SQL/NL2SQL은 실제 경험을 설명하지만 같은 수준의 독립 공개 재현 evidence가 부족함
3. 새 Java 사례보다 현재 포지셔닝의 실제 증거 균형을 개선함
4. generic CRUD/RAG 데모가 아니라 data/AI backend의 실패 경계와 검증 방식을 보여줄 수 있음

### P75. Python/FastAPI Data / AI Service Integration sample - `pending`

최소 범위:

```text
FastAPI request boundary
-> natural-language question
-> SQL candidate
-> read-only policy / syntax validation
-> bounded execution
-> result
-> answer/evaluation status
```

검증해야 할 경계:

- generation success와 answer correctness를 구분
- unsafe/write SQL 차단
- row/timeout limit
- invalid/unsupported query fail-closed
- deterministic fixture/provider를 이용한 자동 테스트
- 외부 유료 모델 없이 core regression 재현 가능
- real-model adapter는 가치가 있을 때만 별도 bounded gate로 추가

완료 gate:

```text
문제/결정 문서화
-> 독립 Python 구현
-> 정상·실패·경계 테스트 PASS
-> 공개 안전성 검수 PASS
-> portfolio regression PASS
-> Pages publication PASS
```

회사 코드, 실제 query/schema/data, 내부 식별자는 사용하지 않습니다.

### P80. HR-readable Case Study 개선 - `pending`

기존 published 사례의 기술 구현과 evidence는 유지하면서 첫 화면은 다음 순서를 유지합니다.

```text
문제
-> 왜 중요한가
-> 무엇을 바꿨는가
-> 확인된 결과
-> 기술적으로 어떻게 했는가
```

### P90. 포트폴리오 유지관리 - `pending`

- [x] Pages 링크와 공개 상태값 동기화
- [x] responsive/nav/code/table overflow baseline 자동 회귀
- [x] OpsMate 검증 범위를 최신 public E2E 사실과 동기화
- [ ] 현재 독립 리뷰 P0 문구 정렬의 PR regression 확인
- [ ] main Pages verify/build/deploy 확인
- [ ] 변경 후 Codex/Gemini self-hosted 재검토
- [ ] 실제 물리 단말 육안 spot-check는 선택적 유지관리
- [ ] 회사 GitHub evidence 월말 갱신은 반복 유지관리

## 현재 사용자에게 필요한 작업

현재 즉시 필요한 사용자 작업은 없습니다.

기존 4개 Java/Spring 공개 case-study publication gate는 완료됐습니다. 다음 구현은 기술 개수를 늘리기 위한 Python 데모가 아니라, 실제로 부족한 Python/FastAPI Data / AI Service Integration evidence 한 건으로 제한합니다.

## 완료 판정

1. GitHub Pages 포트폴리오: `published`
2. Capability-first positioning: `verified`, 독립 리뷰 P0 정렬 publication 재검증 진행 중
3. OpsMate real-model/internal/public bounded E2E: `verified`
4. OpsMate 24x7 운영/SLA/장기 부하: `not claimed`
5. 사용자 로그인·권한 통합 사례: `published`
6. 기간 조회 데이터 정합성 사례: `published`
7. 배포·복구 이식성 사례: `published`
8. 업무 규칙 일관성 사례: `published`
9. 다음 신규 evidence: Python/FastAPI Data / AI Service Integration 선택 완료, 구현 `pending`
