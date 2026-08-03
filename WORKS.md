# Portfolio Work Ledger

- 기준일: `2026-08-03`
- 목적: 포트폴리오 작성부터 GitHub Pages 공개까지 작업, 의존성과 검증 근거를 한곳에서 추적
- 원칙: 문서·코드·테스트·공개 안전성 검토가 함께 끝나기 전에는 완료로 표시하지 않음
- 공통 검수표: [`03_portfolio/review-checklist.md`](03_portfolio/review-checklist.md)

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
| `W06` | GitHub Pages 블로그 구현 | 홈, 탐색, 레이아웃, 배포 workflow | 모바일·링크·접근성·빌드 검수 | `in-progress` |
| `W07` | 통합 검수 | 테스트·링크·민감정보·렌더링 결과 | 공통 검수표의 필수 항목 모두 통과 | `pending` |
| `W08` | GitHub 공개 | commit, push, PR, Pages URL | 원격 배포 성공과 공개 URL 응답 확인 | `pending` |

## 의존성

```text
W00 -> W01 -> W02
             |-> W03 --|
             |-> W04 --|-> W07 -> W08
             |-> W05 --|
             `-> W06 --'
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
- 구매 초안·제출·승인·반려·발주·감사 수직 기능과 고정 `policy.search` typed tool 구현
- `mvnw.cmd -q clean verify`: 19개 성공, 실패·오류·건너뜀 0개 (`2026-08-03`)
- 공개 상태: `implemented`, `tested-component`; 실제 오픈웨이트 모델 서버 E2E와 운영 배포는 미검증

## 다음 실행 순서

1. `W06`의 GitHub Pages 빌드와 실화면을 원격 환경에서 확인합니다.
2. `W07`에서 이미 성공한 프로젝트·저장소 테스트와 공개 안전성 검사에 원격 렌더링 결과를 합쳐 최종 판정합니다.
3. `W08`에서 검수된 변경만 PR로 병합하고 공개 URL의 홈·사례·프로젝트 링크를 확인합니다.
4. 공개 후에는 실제 허가된 오픈웨이트 모델 서버 E2E와 다음 Java/Spring 독립 재현 사례를 별도 Work로 진행합니다.
