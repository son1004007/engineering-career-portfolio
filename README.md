# Engineering Career Portfolio

손기석님의 엔지니어 커리어 포트폴리오 저장소입니다.

이 저장소는 단순한 이력서 보관용이 아니라, 아래 목적을 함께 만족하도록 설계합니다.

- 면접관과 채용 담당자가 빠르게 이해할 수 있는 포트폴리오
- 채용공고 분석과 지원 우선순위를 누적 관리하는 개인 운영 저장소
- 회사 내부 공유 문서와 개인 포트폴리오를 분리하되 연결 가능한 구조
- 추후 다른 ChatGPT 대화나 Codex가 이어받아도 바로 이해할 수 있는 작업 기준 저장소

---

## 이 저장소를 보는 추천 순서

### 면접관 / 채용 담당자
1. `01_profile/career-summary.md`
2. `05_portfolio/portfolio-overview.md`
3. `04_projects/`
4. `02_resume/`

### 손기석님 본인
1. `00_meta/project-context.md`
2. `00_meta/current-status.md`
3. `03_job-postings/`
4. `02_resume/`
5. `04_projects/`

### 회사 내부 공유 대상
1. `06_company-sharing/`
2. `01_profile/career-direction.md`

---

## 폴더 구조

```text
engineering-career-portfolio/
├─ README.md
├─ 00_meta/
│  ├─ project-context.md
│  ├─ working-rules.md
│  ├─ current-status.md
│  └─ handoff-guide.md
├─ 01_profile/
│  ├─ career-summary.md
│  ├─ core-strengths.md
│  └─ career-direction.md
├─ 02_resume/
│  ├─ resume-backend.md
│  ├─ resume-backend-ai.md
│  ├─ resume-security-backend.md
│  └─ step-based-execution-roadmap.md
├─ 03_job-postings/
│  ├─ overall-comparison-summary.md
│  ├─ scoring-criteria.md
│  └─ postings/
├─ 04_projects/
│  ├─ README.md
│  ├─ ai-rag-api/
│  ├─ text2sql/
│  ├─ backend-platform-template/
│  └─ security-audit-log/
├─ 05_portfolio/
│  ├─ portfolio-overview.md
│  ├─ backend-ai-engineer.md
│  └─ security-backend-platform.md
├─ 06_company-sharing/
│  ├─ business-types-and-dev-roles.md
│  └─ common-deliverables-strategy.md
└─ 07_interview/
   ├─ interview-qa.md
   └─ company-specific-notes.md
```

---

## 운영 원칙
- 문서는 Markdown 기준으로 관리합니다.
- 같은 내용이라도 목적이 다르면 문서를 분리합니다.
  - 예: 이력서용 / 회사공유용 / 면접용
- 새 채용공고가 들어오면 `03_job-postings/postings/` 아래에 개별 문서를 먼저 만듭니다.
- 모든 비교와 우선순위 판단은 `03_job-postings/overall-comparison-summary.md` 에 누적합니다.
- 프로젝트는 설명만 쓰지 않고, 가능한 한 실행 구조와 README를 같이 둡니다.
- 이 저장소는 다른 대화/도구가 이어받을 수 있게 항상 `00_meta/current-status.md` 를 최신 상태로 유지합니다.

---

## 현재 커리어 핵심 방향
- 메인 전략: 백엔드 + AI 응용 확장형
- 기반 전략: 백엔드 + 데이터/플랫폼 확장형
- 차별화 전략: 보안 + 백엔드 + 플랫폼 특화형

한 줄로 정리하면:

**백엔드를 중심으로 AI 응용을 확장하되, 데이터/플랫폼 역량을 기반으로 쌓고, 보안 도메인을 차별화 무기로 활용하는 엔지니어**
