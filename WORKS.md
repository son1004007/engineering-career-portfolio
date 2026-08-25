# Portfolio Work Ledger

- 기준일: `2026-08-25`
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
| `W02` | 검수 체계 고정 | 이 ledger와 공통 체크리스트 | 모든 Work의 증거·공개 gate 정의 | `verified` |
| `W03` | 첫 Java/Spring 사례 공개 | 인증 통합 게시물, 독립 샘플, 테스트 | 정상·실패·경계 테스트 성공과 공개 검수 | `verified` |
| `W04` | 추가 업무 사례 정리 | Java/Spring·데이터·AI 사례 초안 | 각 글의 근거·한계·재현 계획 명시 | `verified` |
| `W05` | OpsMate Local 구현 | 설계, 수직 기능, 테스트 | RBAC·상태·멱등성·fail-closed 테스트 성공 | `verified` |
| `W06` | GitHub Pages 블로그 구현 | 홈, 탐색, 레이아웃, 배포 workflow | 반응형 규칙·링크·접근성·빌드 검수 | `verified` |
| `W07` | 통합 검수 | 테스트·링크·민감정보·렌더링 결과 | 공통 검수표의 필수 항목과 미검증 한계 기록 | `verified` |
| `W08` | GitHub 공개 | commit, push, PR, Pages URL | 원격 배포 성공과 공개 URL 응답 확인 | `published` |
| `W09` | OpsMate 공개 데모와 재현 가능한 운영 | 공개 session UI, private DB/model boundary, immutable images, internal E2E, public HTTPS ingress | 내부 E2E + public URL 외부 gate + public lifecycle | `in-progress` |

## 의존성

```text
W00 -> W01 -> W02
             |-> W03 --|
             |-> W04 --|-> W07 -> W08
             |-> W05 --|
             `-> W06 --'
W08 -> W09
```

## 핵심 검증 기록

### W00-W08

- 첫 공개 포트폴리오와 Java/Spring 인증 통합 샘플, Jekyll Pages, 공개 링크/민감정보 검수는 완료된 상태를 유지합니다.
- Spring Security 인증 브리지는 독립 공개 샘플의 최근 테스트 성공 근거를 유지합니다.
- 회사 업무는 원본 비공개 코드를 복사하지 않고 비식별 claim과 독립 재현 상태를 분리합니다.

### W09 — OpsMate Local

#### 구현/CI

- Spring Boot 기반 구매 초안·제출·승인·반려·발주·감사 수직 기능
- 공개 Thymeleaf/HttpSession UI, CSRF, 서버 생성 persona/workspace
- workspace 격리·TTL, model single-flight/quota/concurrency guard
- PostgreSQL/Flyway admin·migration·runtime role 분리
- private Docker network, non-root/read-only image, loopback Nginx edge
- restricted SSH model tunnel과 fail-closed model boundary
- normal close, emergency close, strict CLOSED verifier, same-digest reopen 자산
- latest reviewed source: `f99686981da7efb8802635ae2bde5b0f781433ad`
- `Verify Portfolio` run `32848946968`: 모든 job 성공 (`2026-08-25`)

#### 실제 모델 단독 E2E

- Ollama `0.13.5`, `gemma3:12b`
- 합성 요청 9/9 성공
- 요청·감사 이벤트 각각 9건
- 관측 p95 `21,076ms`, gate `<= 30,000ms`
- 상세: [`02_projects/opsmate-local/docs/REAL_MODEL_E2E_EVIDENCE.md`](02_projects/opsmate-local/docs/REAL_MODEL_E2E_EVIDENCE.md)

#### immutable release

- app: `ghcr.io/son1004007/opsmate-local@sha256:61e267c05bf0ce0ea932ae62a3989194bd2a0065532d0ee4caee8b37c8f9d40b`
- tunnel: `ghcr.io/son1004007/opsmate-model-tunnel@sha256:7fc133485a8ba60190e55b5eeb2da8b5eb02c1aa7e70e0cc0ce7b723746db1df`
- publish/pull verification run `32848946995`: PASS
- Synology runtime preparation run `32849378114`: exact digest pull/stage, runtime input permission `600`, PostgreSQL volume 보존, running container `0`, final `CLOSED`: PASS

#### Synology internal deployment/lifecycle E2E

`2026-08-25` bounded runtime run `32849533407`에서 다음을 모두 통과했습니다.

- stack-start / port-policy / network-policy / edge-security
- loopback edge `127.0.0.1:18083`
- app/DB/model-tunnel host port 없음
- app/edge direct egress 차단
- Secure XSRF/JSESSIONID session
- restricted tunnel을 통한 실제 모델 path
- persona flow와 durable draft
- cross-workspace isolation
- Nginx `429` rate-limit
- credential/log scan
- normal close + synthetic workspace purge
- strict CLOSED verification
- same-digest reopen
- emergency close
- final normal close
- `runtime_policy_flags=YES_YES`
- final `CLOSED`

공개 가능한 상세 증거: [`02_projects/opsmate-local/docs/NAS_INTERNAL_E2E_EVIDENCE.md`](02_projects/opsmate-local/docs/NAS_INTERNAL_E2E_EVIDENCE.md)

#### 현재 미검증 boundary

내부 runtime gate는 완료됐습니다. 다음 항목은 아직 별도 external gate입니다.

- DSM Reverse Proxy/TLS public ingress
- router forwarding / public origin
- Internet/LTE 외부 persona smoke
- 외부 DB/model 비노출 확인
- public-origin session isolation/rate-limit 확인
- public origin을 포함한 close/reopen smoke

따라서 W09 전체는 아직 `in-progress`이며, **GHCR package visibility는 더 이상 blocker가 아닙니다.**

## 다음 실행 순서

상세 체크리스트는 [`PORTFOLIO_COMPLETION_PLAN.md`](PORTFOLIO_COMPLETION_PLAN.md)를 Source of Truth로 사용합니다.

1. DSM Reverse Proxy/TLS와 router public ingress를 구성합니다.
2. exact reviewed release를 열어 Internet/LTE public smoke, DB/model 비노출, session/rate/security gate를 검증합니다.
3. public origin을 포함한 normal close -> same-digest reopen -> emergency close -> final normal close를 검증하고 최종 `CLOSED`로 둡니다.
4. W09 상태 문서와 공개 증거를 최종 동기화합니다.
5. `03_portfolio/case-study-index.md`의 다음 Java/Spring 사례를 독립 재구현하고 검수합니다.
6. `evidence/company-github/monthly/`를 월말에 갱신하고 공개 링크와 Pages workflow 상태를 정기 확인합니다.
