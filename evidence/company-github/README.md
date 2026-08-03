# Company GitHub Career Evidence

- 기준일: `2026-08-01`
- 공개 범위: 비식별 메타데이터와 경력 주장만 공개
- 원본 범위: 회사 소유 비공개 GitHub 저장소
- 목적: 퇴사 후에도 본인 작성 커밋과 검증된 구현 범위를 과장 없이 설명

## 먼저 알아야 할 점

일반 Git 기록으로 확인되는 것은 commit author와 committer입니다. 누가 GitHub에 push 이벤트를 실행했는지는 조직 audit log가 있어야 확인할 수 있습니다. 따라서 이 저장소는 `내가 push한 커밋`이 아니라 `내 GitHub 계정과 확인된 author identity에 귀속되는 작성 커밋`으로 기록합니다.

커밋 수는 활동 범위를 찾는 인덱스일 뿐 생산성, 난이도, 영향도를 뜻하지 않습니다. 경력 주장은 코드, 테스트, 실행 결과, 업무 기록과 함께 확인된 범위만 사용합니다.

## 공개하지 않는 정보

- 회사 소스 코드, diff, patch
- 원본 커밋 메시지와 내부 파일 경로
- 고객명, 비공개 저장소명과 URL
- 서버 주소, IP, 계정, 토큰, DB 접속 정보
- 고객 데이터, 프롬프트, 로그 원문
- 개인 이메일과 회사 이메일

공개 snapshot은 비식별 project alias와 집계값만 남깁니다. 선택형 verification digest와 source fingerprint는 비공개 검증용이며, 활성화한 결과를 이 공개 저장소에 커밋하지 않습니다. 권한 있는 검토자는 필요한 claim을 회사 GitHub 원본, 코드·테스트와 업무 기록에서 다시 확인해야 합니다.

## 증거 등급

| 등급 | 의미 |
|---|---|
| `E3` | 인증된 GitHub 또는 원본 저장소에서 본인 귀속 커밋과 구현 또는 테스트 근거를 함께 확인 |
| `E2` | 본인 귀속 커밋은 확인했지만 병합, 실행, 운영 또는 기여 범위 일부가 미확인 |
| `E1` | 업무일지나 경력 설명은 있으나 독립된 Git 근거가 부족 |
| `E0` | 예정 또는 계획 |

구현 상태는 `designed`, `implemented`, `tested-component`, `integrated`, `operated`를 구분합니다. 앞 단계가 확인됐다고 뒤 단계까지 자동으로 올리지 않습니다.

## 읽기 순서

1. [`career-claims.csv`](career-claims.csv)
2. [`projects.md`](projects.md)
3. 최신 월별 snapshot: [`monthly/2026-08.md`](monthly/2026-08.md)
4. 비식별 로컬 집계 [`snapshots/local-author-metadata-2026-08-01.json`](snapshots/local-author-metadata-2026-08-01.json)
5. 인증된 계정 집계 [`snapshots/authenticated-account-summary-2026-08-01.json`](snapshots/authenticated-account-summary-2026-08-01.json)
6. [`../../03_portfolio/evidence-index.md`](../../03_portfolio/evidence-index.md)

## 갱신 주기

- 매월 말: 인증된 원격 상태를 확인하고 월별 snapshot 갱신
- 프로젝트 milestone: 새 구현, 테스트, 통합 또는 운영 근거가 생길 때 claim 갱신
- 이력서 작성 전: 사용하려는 claim의 `last_verified_at` 재확인
- 퇴사 직전: 접근 가능한 모든 원격, PR, 기본 브랜치 포함 여부와 테스트 근거를 최종 확인하고 `departure-snapshot-YYYY-MM-DD.md` 생성

집계 도구는 [`../../tools/collect_company_commit_evidence.py`](../../tools/collect_company_commit_evidence.py)입니다. 실제 저장소 경로와 author identity는 커밋하지 않는 local config에만 둡니다.

로컬 snapshot은 생성 시점의 local refs를 집계합니다. 원격과 동기화됐다는 뜻은 아닙니다. 이력서에 사용할 핵심 claim은 인증된 GitHub 원격, 코드와 테스트 또는 실행 결과를 추가로 대조합니다.

인증된 계정 snapshot은 GitHub login에 직접 연결된 author commit만 집계하고, 로컬 snapshot은 승인된 이름과 이메일 identity를 함께 매칭합니다. 두 집계는 범위가 다르고 서로 겹치므로 합산하지 않습니다.

인증된 계정의 commit 수는 저장소별 GitHub 검색 결과 합계입니다. 저장소 간 동일 SHA 중복 여부를 독립 확인하기 전에는 고유 commit 수로 표현하지 않습니다. 로컬 snapshot도 clone이나 fork가 함께 설정되면 source별 집계가 겹칠 수 있습니다.
