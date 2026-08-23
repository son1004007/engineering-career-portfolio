# Portfolio Work Ledger

- 기준일: `2026-08-24`
- 목적: 포트폴리오 작성부터 GitHub Pages 공개와 OpsMate 운영 검증까지 작업, 의존성과 검증 근거를 한곳에서 추적
- 원칙: 문서·코드·테스트·공개 안전성 검토가 함께 끝나기 전에는 완료로 표시하지 않음
- 공통 검수표: [`03_portfolio/review-checklist.md`](03_portfolio/review-checklist.md)
- 현재 완료 계획: [`PORTFOLIO_COMPLETION_PLAN.md`](PORTFOLIO_COMPLETION_PLAN.md)

## 상태

| 상태 | 의미 |
|---|---|
| `pending` | 선행 작업 전 또는 착수 전 |
| `in-progress` | 현재 작성·구현·검수 중 |
| `blocked` | 외부 입력이나 실행 환경이 없어 다음 단계로 갈 수 없음 |
| `verified` | 산출물과 최근 검증 결과가 존재 |
| `published` | 검증된 산출물이 공개 GitHub Pages에서 확인됨 |

## Work 현황

| Work | 목표 | 핵심 산출물 | 완료 조건 | 현재 상태 |
|---|---|---|---|---|
| `W00` | GitHub 게시 환경 준비 | 인증된 GitHub CLI, 작업 브랜치 | CLI·계정·remote·branch 확인 | `verified` |
| `W01` | 회사·개인 근거 정합화 | claim, 사례 인덱스, 추가 업무 계정 귀속 | 비식별 claim과 후보 상태 일치 | `verified` |
| `W02` | 검수 체계 고정 | 이 ledger와 공통 체크리스트 | 모든 Work의 증거·공개 게이트 정의 | `verified` |
| `W03` | 첫 Java/Spring 사례 공개 | 인증 통합 게시물, 독립 샘플, 테스트 | 정상·실패·경계 테스트 성공과 공개 검수 | `verified` |
| `W04` | 추가 업무 사례 정리 | Java/Spring·데이터·AI 사례 초안 | 각 글의 근거·한계·재현 계획 명시 | `verified` |
| `W05` | OpsMate Local 구현 | 설계, 수직 기능, 테스트 | RBAC·상태·멱등성·fail-closed 테스트 성공 | `verified` |
| `W06` | GitHub Pages 블로그 구현 | 홈, 탐색, 레이아웃, 배포 workflow | 반응형 규칙·링크·접근성·빌드 검수 | `verified` |
| `W07` | 통합 검수 | 테스트·링크·민감정보·렌더링 결과 | 공통 검수표의 필수 항목과 미검증 한계 기록 | `verified` |
| `W08` | GitHub 공개 | commit, push, PR, Pages URL | 원격 배포 성공과 공개 URL 응답 확인 | `published` |
| `W09` | OpsMate 공개 데모와 재현 가능한 운영 | 공개 session UI, model/DB 보호, 실제 모델 E2E, Docker/Caddy, open·close·reopen | 최신 regression, public URL·외부 정책과 양 호스트 rehearsal | `in-progress` |

## 의존성

```text
W00 -> W01 -> W02
             |-> W03 --|
             |-> W04 --|-> W07 -> W08
             |-> W05 --|
             `-> W06 --'
W08 -> W09
```

## Work별 검증 기록

### W00

- GitHub CLI `2.97.0`
- 인증 계정: 공개 저장소 소유 계정
- 작업 브랜치: `agent/portfolio-blog`
- 검증일: `2026-08-03`

### W01

- 추가 업무 계정의 소유자 확인일: `2026-08-03`
- 공개 반영: 계정명 대신 비식별 귀속 사실과 확인된 직접 기여 범위만 기록
- 추가 claim: `WORK-DATA-03`, `CLM-DATA-003`, `CS-JAVA-11`

### W02

- 완료 조건은 [`03_portfolio/review-checklist.md`](03_portfolio/review-checklist.md)로 고정
- 상태 변경 시 이 문서, [`03_portfolio/case-study-index.md`](03_portfolio/case-study-index.md), [`03_portfolio/evidence-index.md`](03_portfolio/evidence-index.md)를 함께 갱신

### W04

- Java/Spring 기술 노트 4건과 AI 응용 기술 노트 2건 작성
- 모든 글은 `source-reviewed` 또는 `tested-component` 한계를 유지하고 공개 재현 전 완료 표현을 사용하지 않음
- 회사·계정·저장소 식별자와 credential 패턴 스캔 통과: `2026-08-03`

### W03

- Spring Boot `3.5.16`, Java `21`, Maven Wrapper `3.9.9`
- DB 인증, HMAC SSO adapter, 로컬 RBAC, 세션 회전, CSRF와 fail-closed 독립 구현
- `mvnw.cmd -q clean verify`: 24개 성공, 실패·오류·건너뜀 0개 (`2026-08-03`)
- 공개 상태: `sample-verified`; 실제 회사 시스템과 운영 환경 검증을 뜻하지 않음

### W05

- Spring Boot `3.5.16`, Java `21`, Maven Wrapper `3.9.11`
- 구매 초안·제출·승인·반려·발주·감사 수직 기능과 서버 주도 `policy.search` 조회 포트 구현; 조회 결과만 모델에 전달하고 모델에는 도구 실행 권한을 부여하지 않음
- 과거 baseline `mvnw.cmd -q clean verify`: 19개 성공, 실패·오류·건너뜀 0개 (`2026-08-03`)
- 이후 W09에서 공개 웹·PostgreSQL·model guard·운영 자산까지 확장

### W06

- Jekyll 레이아웃, 홈, 공통 탐색, 상태 배지, 사례·문서 레이아웃과 반응형 CSS 구현
- GitHub Pages workflow의 Jekyll 검증, build와 deploy 성공: [Actions run 30782896966](https://github.com/son1004007/engineering-career-portfolio/actions/runs/30782896966)
- 기본 실제 뷰포트 `1280x720`에서 홈과 공개 문서의 CSS 적용, heading, 탐색과 가로 넘침 없음 확인
- `52rem`, `32rem` 반응형 규칙, 키보드 focus, skip link, reduced-motion 규칙을 정적으로 검토
- 검수 한계: 물리 모바일 기기의 최종 시각 검수는 별도 UX 점검 항목으로 남김

### W07

- 원격 CI: Spring Security 인증 브리지 24개, OpsMate Local 19개, 저장소 공개 검수 12개 테스트 성공
- Jekyll PR build, Markdown 상대 링크, Pages pretty URL, 공개 텍스트 credential·로컬 경로 검사 성공
- 공개 홈과 `WORKS`, evidence index, 전략, 체크리스트, AI context, OpsMate, 인증 사례, 회사 근거 경로를 직접 열어 404 없음 확인
- 전략의 `track-a-flagship` 앵커와 `llms.txt`의 공개 URL을 확인하고 오래된 `.html` 문서 링크가 없음을 확인
- 이 기록은 `2026-08-03` 당시 공개본의 통합 검수이며 최신 W09 regression을 대체하지 않음

### W08

- 구현·배포 PR [#3](https://github.com/son1004007/engineering-career-portfolio/pull/3), 공개 링크 보정 PR [#4](https://github.com/son1004007/engineering-career-portfolio/pull/4), Pages URL 보정 PR [#5](https://github.com/son1004007/engineering-career-portfolio/pull/5) 병합
- 공개 URL: [son1004007.github.io/engineering-career-portfolio](https://son1004007.github.io/engineering-career-portfolio/)
- GitHub Pages `workflow` build 방식과 원격 deploy 성공, 홈·사례·프로젝트·근거 문서 응답 확인
- 공개 판정일: `2026-08-03`

### W09

- 구현일: `2026-08-04`
- 공개 웹: Thymeleaf/HttpSession, 실제 CSRF cookie, 서버 `DemoPrincipal`, `/api/**` 거부와 방문자별 workspace 격리·TTL·정리
- 자원 보호: 동일 요청 single-flight, workspace·전체 quota, 제한 queue·follower와 전체 모델 동시 실행 기본값 `1`
- 데이터 경계: PostgreSQL/Flyway, DB admin·one-shot migration·runtime 역할 분리, runtime DDL·Flyway history 접근 거부 검증 경로
- 배포 경계: full-digest app/base/service image, `restart: no`, 분리 network, read-only·최소 권한 container, bounded log와 Caddy access log 미활성
- 운영 자산: 앱·모델 호스트별 open, normal close, 환경 파일 독립 emergency close, closed verifier와 same-digest reopen 계약
- 설명 기준: [`03_portfolio/code-explanation-standard.md`](03_portfolio/code-explanation-standard.md)와 핵심 업무 경계의 한국어 Javadoc·주석
- 전체 `mvnw.cmd -q clean verify`: 54개 성공, 실패·오류·건너뜀 0개 (`2026-08-04`)
- 실제 모델 E2E: 사설 GPU 호스트 Ollama `0.13.5`, `gemma3:12b`, 9/9 성공, 요청·감사 이벤트 각각 9건, 관측 p95 21,076ms, gate `<= 30,000ms`, Maven exit code 0 (`2026-08-23`)
- 실제 모델 검증 source commit: `ff67df0990cbed3a41cf5051a5e2701a7b2a7b50`; 상세 증거는 [`02_projects/opsmate-local/docs/REAL_MODEL_E2E_EVIDENCE.md`](02_projects/opsmate-local/docs/REAL_MODEL_E2E_EVIDENCE.md)
- Office 원격 read-only 검증 경로: disposable detached Git worktree + Bubblewrap로 repository snapshot E2E 성공 (`2026-08-23`); runtime bridge의 상세 증거는 private `device-control`이 Source of Truth
- baseline regression: PR [#10](https://github.com/son1004007/engineering-career-portfolio/pull/10), `Verify Portfolio` run [32638023909](https://github.com/son1004007/engineering-career-portfolio/actions/runs/32638023909)에서 OpsMate `clean verify`, Spring Security 샘플, public portfolio 검사, Jekyll build, shell/Compose/Caddy 검증, non-root image build와 one-shot migration rehearsal 모두 성공 (`2026-08-23`)
- production tunnel host-key pin: PR [#12](https://github.com/son1004007/engineering-career-portfolio/pull/12), source merge `54ceafce06a2d8b23a832c0681654ac1687c407e`; `Verify Portfolio` run `32659673514` 전체 성공 (`2026-08-24`)
- runtime tunnel bootstrap: Synology 전용 key/Office ED25519 known_hosts와 Office restricted authorized-key(`restrict`, `port-forwarding`, loopback Ollama `permitopen`, forced command denial)를 실제 target에서 확인 (`2026-08-24`)
- NAS production-container preflight: Container Manager Docker daemon 접근까지 성공했으나 run `32660707257`에서 GHCR anonymous image pull이 `denied`; container는 시작되지 않았고 package access/visibility가 현재 P20 blocker (`2026-08-24`)
- 미검증 gate: production tunnel container `/api/version`, immutable GHCR digest의 NAS pull, NAS runtime env/closed preflight, public application URL·외부 smoke, host egress allowlist, edge/WAF rate limit, DB/model 외부 비노출, normal/emergency close와 same-digest reopen rehearsal
- 공개 상태: `implemented`, `tested-component`; 실제 모델 adapter E2E와 최신 CI regression은 `verified`. 위 외부 운영 gate가 끝날 때까지 전체 서비스 `verified` 또는 운영 완료로 표시하지 않음

## 다음 실행 순서

상세 체크리스트는 [`PORTFOLIO_COMPLETION_PLAN.md`](PORTFOLIO_COMPLETION_PLAN.md)를 Source of Truth로 사용합니다.

1. GHCR package anonymous pull gate를 해결하고 exact source SHA image의 NAS RepoDigest와 production tunnel container E2E를 검증합니다.
2. NAS-local runtime env/secret과 closed preflight를 준비한 뒤 DSM Reverse Proxy/TLS와 router ingress를 구성합니다.
3. host egress allowlist와 edge/WAF rate limit을 적용하고 public URL 외부 smoke 및 DB/model 비노출을 검증합니다.
4. normal/emergency close와 same-digest reopen을 rehearsal하고 최종 상태를 `CLOSED`로 둡니다.
5. `03_portfolio/case-study-index.md`의 다음 Java/Spring 사례를 독립 재구현하고 검수합니다.
6. `evidence/company-github/monthly/`를 월말에 갱신하고 공개 링크와 Pages workflow 상태를 정기 확인합니다.
