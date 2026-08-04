# AI Context for Engineering Career Portfolio

> 이 저장소 URL만 받은 AI는 이 문서, [`WORKS.md`](WORKS.md), [`03_portfolio/portfolio-strategy.md`](03_portfolio/portfolio-strategy.md), [`03_portfolio/case-study-index.md`](03_portfolio/case-study-index.md), [`03_portfolio/evidence-index.md`](03_portfolio/evidence-index.md)를 먼저 읽습니다.

- 기준일: `2026-08-04`
- 공개 범위: `public`
- 공개 사이트: [GitHub Pages 포트폴리오](https://son1004007.github.io/engineering-career-portfolio/)
- 역할: 손기석의 기술 방향, 구현 샘플, 공개 가능한 경력·기술 근거
- 범위 제한: 개인 연봉, 가족, 건강, 현재 회사 문제, 비공개 지원 전략은 이 저장소에서 판단하지 않음

## 확정된 포트폴리오 전략

이 저장소의 포트폴리오는 두 트랙으로 구성합니다.

1. 신규 대표 프로젝트 `OpsMate Local`은 온프레미스 AI Agent를 기업 업무 트랜잭션에 안전하게 연결하는 역량을 증명합니다.
2. 기존 업무 사례 게시물은 Java/Spring, DB/SQL, 인증, 배포와 운영의 실무 깊이를 증명합니다.

핵심 정체성은 ML 모델 연구자가 아니라 **Java/Spring 엔터프라이즈 백엔드 경험을 중심으로 AI Agent 기능을 통합하는 백엔드·플랫폼 엔지니어**입니다.

`OpsMate Local`은 현재 `implemented`, `tested-component`입니다. 구매 요청·승인·발주 수직 기능에 공개 Thymeleaf session UI, workspace 격리·TTL, model single-flight·quota·동시 실행 제한, PostgreSQL/Flyway 역할 분리와 Docker/Caddy open·close·reopen 자산을 추가했습니다. `2026-08-04` 전체 `clean verify`에서 54개 테스트가 성공했고 실패·오류·건너뜀은 0개였습니다. 모델이 없거나 잘못된 출력을 반환하면 모델에 의존하는 초안 생성은 저장 전에 `fail-closed`로 중단되고 외부 유료 API로 자동 우회하지 않습니다. 이미 제출된 요청의 승인·반려·발주는 모델 가용성과 분리되어 있습니다. 승인된 실제 모델 E2E, 공개 URL·외부 smoke, 외부 네트워크 정책과 양 호스트 rehearsal은 아직 검증하지 않았습니다.

기존 회사 업무는 원본 소스나 내부 식별자를 공개하지 않습니다. 게시물은 원본에서 본인 귀속과 구현 범위를 검증한 뒤 비식별 서술과 독립 재구현 코드로 만듭니다.

결정 근거, 산출물 위치와 실행 순서는 [`03_portfolio/portfolio-strategy.md`](03_portfolio/portfolio-strategy.md), 후보와 검토 상태는 [`03_portfolio/case-study-index.md`](03_portfolio/case-study-index.md)에서 확인합니다.

## 이 저장소로 할 수 있는 판단

- 공개 프로필에 기재된 기술 방향 파악
- 저장된 코드와 테스트 파일의 존재 확인
- 개인 포트폴리오 프로젝트의 구현 범위와 미완성 영역 확인
- 백엔드·데이터·AI 응용·보안 관점의 조합이 목표 직무와 어떤 관련이 있는지 검토

## 이 저장소만으로 하면 안 되는 판단

- 실무 경력 연수와 회사별 재직 기간 확정
- 특정 기술의 숙련도나 대규모 운영 경험 확정
- 회사·팀 전체 성과를 개인 성과로 해석
- 목표 또는 README 설명을 완료된 구현으로 표현
- 특정 회사 입사, 연봉, 근무환경, 오퍼 수락 판단

## 읽기 순서

1. [`WORKS.md`](WORKS.md)
2. [`03_portfolio/portfolio-strategy.md`](03_portfolio/portfolio-strategy.md)
3. [`03_portfolio/case-study-index.md`](03_portfolio/case-study-index.md)
4. [`evidence/company-github/README.md`](evidence/company-github/README.md)
5. [`evidence/company-github/career-claims.csv`](evidence/company-github/career-claims.csv)
6. [`03_portfolio/evidence-index.md`](03_portfolio/evidence-index.md)
7. [`01_profile/career-summary.md`](01_profile/career-summary.md)
8. [`01_profile/core-strengths.md`](01_profile/core-strengths.md)
9. [`01_profile/career-direction.md`](01_profile/career-direction.md)
10. [`03_portfolio/portfolio-overview.md`](03_portfolio/portfolio-overview.md)
11. 검증하려는 프로젝트 또는 사례의 README, 코드, 테스트

## 증거 라벨

- `implemented`: 필요한 코드가 저장소에 존재
- `tested-file-present`: 테스트 파일은 있으나 현재 점검에서 실행 성공까지 확인하지 못함
- `tested-component`: 명시된 구성요소의 테스트 산출물은 있으나 전체 시스템 검증을 뜻하지 않음
- `source-reviewed`: 권한 있는 비공개 원본에서 본인 귀속과 구현 범위를 확인했으나 공개 재현 검증은 아직 없음
- `sample-verified`: 회사 코드와 독립된 공개 샘플의 최근 테스트 성공 기록이 있으나 실제 회사 시스템 검증을 뜻하지 않음
- `verified`: 최근 실행일, 명령, 환경·버전, 성공 결과가 함께 기록됨
- `partial`: 일부 코드나 문서만 있고 주요 구성요소가 빠짐
- `planned`: 문서 또는 작업 목록에만 존재
- `self-described`: 경력·프로필 문서의 자기기술이며 별도 근거 확인 필요
- `private-work-code-verified`: 권한 있는 환경에서 회사 비공개 코드와 본인 귀속 커밋을 확인하고 공개 문서에는 비식별 claim만 남김

기술 키워드, README 설명, 디렉터리 이름만으로 `verified`를 부여하지 않습니다.

## 현재 공개 증거 요약

| 항목 | 상태 | 판단 |
|---|---|---|
| `OpsMate Local` | `implemented`, `tested-component` | 공개 웹·workspace·model guard·PostgreSQL 역할 분리·배포/중단 자산 구현, 54개 테스트 성공. 실제 모델 E2E·공개 URL·외부 정책·양 호스트 rehearsal은 미검증 |
| Java/Spring 사례 5건 | `sample-verified` 1건, `source-reviewed` 4건 | 회사 비공개 원본에서 본인 귀속과 코드 범위를 확인. 인증 통합 사례는 회사 코드와 독립된 공개 샘플 24개 테스트 성공 |
| `ai-rag-api` | `implemented`, `tested-file-present` | API·서비스·저장소 코드와 단위 테스트 파일 존재. 현재 점검에서는 의존성 미설치로 테스트 성공 미확인 |
| `backend-platform-template` | `partial` | 앱·설정·테스트 파일은 있으나 `app.api.routes`가 없어 현재 구조로는 import 실패 예상 |
| `security-audit-log` | `partial` | API route만 있고 참조하는 service, 앱 진입점, 테스트가 없음 |
| 개인 공개 `text2sql` 샘플 | `planned` | 이 저장소 안에는 별도 프로젝트 디렉터리가 없음 |
| 회사 Text2SQL/NL2SQL 업무 | `private-work-code-verified`, `tested-component` | 비공개 회사 Git의 FastAPI 코드, 모델 adapter, SQL 실행, benchmark 결과와 본인 귀속 커밋 확인 |
| 회사 Agentic AI Runtime 업무 | `private-work-code-verified`, `tested-component` | workspace, artifact, provenance, storage와 테스트의 본인 구현 범위 확인 |
| 그 밖의 실무 경력·기술 | `self-described`, 일부 `E2` | 회사 commit metadata와 work evidence를 프로젝트별로 계속 교차 확인 |

상세 상태와 갱신 기준은 [`03_portfolio/evidence-index.md`](03_portfolio/evidence-index.md)에 있습니다.

## 안전한 해석

현재 공개 저장소는 개인 구현 샘플, 비식별 회사 경력 증거와 앞으로 만들 공개 사례의 검토 상태를 함께 보여줍니다.

```text
Python/FastAPI API 구조
RAG 서비스의 기본 흐름과 테스트 설계
Java/Spring 인증, SQL 정합성·성능, 배포 이식성 사례의 원본 검토 근거
Text2SQL/NL2SQL API, SQL 검증과 다중 모델 benchmark 업무
Agentic AI Runtime의 작업 격리와 산출물 추적 구성요소 구현
백엔드 설정·로깅·예외 처리 구조에 대한 관심
감사로그·권한·통제 관점의 설계 의도
문서 중심의 구조화와 커리어 포지셔닝
```

다음은 이 저장소만으로 확정할 수 없습니다.

```text
Java/Spring 실무의 전체 수준과 아직 재현하지 않은 나머지 사례의 구현 완료
프로덕션 트래픽과 운영 규모
Kafka/Redis/Kubernetes 실전 운영
RAG 품질·보안·관측성·비용 최적화
감사로그 프로젝트의 완성도
팀 프로젝트 전체에서의 개인 기여 비율
회사 비공개 서비스의 장기 운영 규모와 성과
```

## 직무·직장 선택과 결합할 때

1. 이 저장소에서는 공개 기술 근거만 추출합니다.
2. 비공개 지원 전략이나 개인 조건은 이 공개 저장소에서 추론하지 않습니다.
3. 공개 회사 경력 claim은 `evidence/company-github/`에서 확인하고, 권한 있는 환경에서는 원본 코드·테스트·업무 기록과 교차 확인합니다.
4. 현재 공고·회사 조건은 최신 공개 정보로 별도 조사합니다.
5. 결론에는 `공개 근거 / 권한 있는 비공개 근거 / 추론 / 미확인`을 분리합니다.
