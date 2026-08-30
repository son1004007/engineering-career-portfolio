# Portfolio Completion Plan

- 기준일: `2026-08-30`
- 목적: 공개 포트폴리오의 검증 상태를 실제 실행 증거와 동기화하고, 특정 언어나 framework가 아니라 capability gap을 기준으로 다음 공개 evidence를 선택한다.
- 원칙: 문서상 완료가 아니라 source review, 독립 구현, 최근 CI와 필요한 runtime/publication evidence를 근거로 상태를 올린다.
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

- portfolio runtime source: `f99686981da7efb8802635ae2bde5b0f781433ad`
- application image: `ghcr.io/son1004007/opsmate-local@sha256:61e267c05bf0ce0ea932ae62a3989194bd2a0065532d0ee4caee8b37c8f9d40b`
- model-tunnel image: `ghcr.io/son1004007/opsmate-model-tunnel@sha256:7fc133485a8ba60190e55b5eeb2da8b5eb02c1aa7e70e0cc0ce7b723746db1df`
- model: `gemma3:12b`
- 실제 모델 E2E: 9/9
- 관측 p95: `21,076ms`, 프로젝트 gate `<= 30,000ms`
- Synology internal bounded E2E: PASS
- public Internet bounded E2E: PASS
- 최종 runtime 상태: `CLOSED`

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

- [x] `2026-08-23` 실제 모델 E2E 기록
- [x] `2026-08-25` Synology internal deployment/network/security/lifecycle E2E 기록
- [x] `2026-08-29` public Internet deployment/network/lifecycle E2E 기록
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

첫 화면과 AI 해석 규칙을 다음 순서로 재구성했습니다.

```text
문제와 결과
-> 엔지니어링 역량
-> 검증된 evidence
-> 기술 세부
-> 사용하는 언어와 framework
```

갱신 대상:

- [x] `README.md`
- [x] `index.md`
- [x] `HOW_I_ENGINEER.md`
- [x] `AGENTS.md`
- [x] `AI_CONTEXT.md`
- [x] `llms.txt`
- [x] `_config.yml`
- [x] `01_profile/career-summary.md`
- [x] `01_profile/core-strengths.md`
- [x] `01_profile/career-direction.md`
- [x] `03_portfolio/portfolio-strategy.md`
- [x] `03_portfolio/portfolio-overview.md`
- [x] `WORKS.md`

목표:

- HR이 전문 용어 없이도 어떤 문제를 해결하는 엔지니어인지 이해할 수 있음
- 엔지니어는 바로 아래에서 architecture, framework, test와 evidence를 확인할 수 있음
- `Java 개발자`가 아니라 software/backend/platform capability를 먼저 인식함
- AI를 별도 유행 기술이 아니라 개발 방식과 서비스 기능 양쪽에서 사용하는 역량으로 보여줌

### P30. 사용자 로그인과 권한 통합 사례 - `published`

- 회사 코드와 독립된 Java 21 / Spring Boot 3.5.16 합성 인증 샘플
- DB/SSO 인증 수렴, local RBAC, session rotation, CSRF lifecycle, nonce replay, fail-closed 검증
- 자동 테스트 24개 성공
- main Pages publication evidence 존재

실제 외부 IdP, 운영 DB, 분산 session, 대규모 동시 접속과 SLA는 검증하지 않았습니다.

### P40. 복잡한 기간 조회 데이터 정합성 사례 - `published`

- Spring Boot/MyBatis/H2 독립 합성 샘플
- same/cross-year, tenant isolation, count/page, deterministic pagination, invalid input 검증
- 자동 테스트 12개 성공
- 실제 Oracle optimizer 선택과 운영 성능은 주장하지 않음

### P50. 환경이 달라도 배포하고 복구할 수 있는 구조 - `published`

- Spring Boot WAR 독립 샘플
- external-container bootstrap, non-root context path, 외부 config fail-closed
- backup -> replace -> health -> rollback rehearsal
- 자동 테스트 10개 성공
- 실제 외부 Tomcat zero-downtime, session drain, SLA는 주장하지 않음

### P60. 업무 규칙 일관성 사례 - `published`

- canonical session identity와 legacy fallback 경계
- latest-only / explicit-or-latest 업무 기준
- Service가 확정한 identity와 snapshot key만 data access에 전달
- 400/401/404 fail-closed boundary
- 자동 테스트 11개 성공
- 실제 회사 시스템 전체 정합성, 운영 DB, 조직 전체 설계 책임과 SLA는 주장하지 않음

### P70. 다음 공개 evidence 선택 - `pending`

현재 published backend case 4건을 늘리기 위해 새로운 Java 사례를 추가하지 않습니다.

다음 작업은 capability map의 **증거 공백**을 기준으로 선택합니다.

검토 영역:

```text
Data / AI Service Integration
AI evaluation
Platform / Operations
Security / Governance
AI-assisted engineering workflow
```

선택 기준:

1. 기존 공개 evidence와 다른 역량을 보여줄 것
2. 실제 경력 또는 독립 구현 근거가 있을 것
3. 비개발자도 해결한 문제를 이해할 수 있을 것
4. 회사 자산 없이 공개 재현이 가능할 것
5. 정상·실패·경계 검증이 가능할 것
6. 단순 기술 demo가 아니라 시스템 판단을 보여줄 것

현재 `CS-JAVA-11` 통계 분석 UI는 data/service evidence가 실제 지원 직무에서 필요할 때만 후보로 사용합니다. ID의 Java 표기는 기존 catalog 호환성을 위한 것이며 신규 선택 기준이 아닙니다.

### P80. HR-readable Case Study 개선 - `pending`

기존 published 사례의 기술 구현과 evidence는 유지하면서 제목과 첫 문단을 다음 형식으로 순차 점검합니다.

```text
문제
-> 왜 중요한가
-> 무엇을 바꿨는가
-> 확인된 결과
-> 기술적으로 어떻게 했는가
```

전문 용어를 제거하지 않고, 비개발자가 전문 용어를 알아야만 내용을 이해하는 구조만 제거합니다.

### P90. 포트폴리오 유지관리 - `pending`

- [x] Pages 링크와 공개 상태값 동기화
- [x] responsive/nav/code/table overflow baseline 자동 회귀
- [x] OpsMate 검증 범위를 최신 public E2E 사실과 동기화
- [ ] 현재 capability-first 변경의 Pages verify/build/deploy 결과 확인
- [ ] 실제 물리 단말 육안 spot-check는 선택적 유지관리
- [ ] 회사 GitHub evidence 월말 갱신은 반복 유지관리

## 현재 사용자에게 필요한 작업

현재 즉시 필요한 사용자 작업은 없습니다.

OpsMate workload는 `CLOSED` 상태를 유지합니다. 기존 4개 공개 case-study publication gate는 완료됐습니다. 신규 사례는 특정 language 수를 늘리기 위해 만들지 않습니다.

## 완료 판정

1. GitHub Pages 포트폴리오: `published`
2. Capability-first README/Pages/profile/AI interpretation: `verified`, latest Pages run 결과 확인 필요
3. OpsMate code/regression + real-model/internal/public bounded E2E: `verified`
4. OpsMate 24x7 운영/SLA/장기 부하: `not claimed`
5. 사용자 로그인·권한 통합 사례: `published`
6. 기간 조회 데이터 정합성 사례: `published`
7. 배포·복구 이식성 사례: `published`
8. 업무 규칙 일관성 사례: `published`
9. 다음 신규 사례: capability evidence gap이 생길 때만 선택
