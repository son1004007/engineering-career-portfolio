# Projects

이 폴더는 손기석의 실무형 엔지니어링 역량을 보여주는 프로젝트 문서를 관리하는 공간입니다.

이 저장소의 프로젝트는 단순한 학습 기록보다 아래 목적을 우선합니다.
- 면접관이 빠르게 이해할 수 있는 포트폴리오
- 실제 서비스/운영 구조를 설명할 수 있는 산출물
- 이력서와 연결 가능한 프로젝트 설명
- 나중에 확장 가능한 실행 문서

---

## 프로젝트 구성 원칙
### 1. 설명만 두지 않는다
가능하면 아래 항목을 함께 둡니다.
- 프로젝트 개요
- 문제 정의
- 해결 방식
- 기술 스택
- 구조 설명
- 실행 방법
- 결과 또는 기대 효과

### 2. 커리어 방향 3축과 연결한다
프로젝트는 아래 3축 중 하나 이상에 연결되어야 합니다.
- 백엔드 + AI 응용 확장형
- 백엔드 + 데이터/플랫폼 확장형
- 보안 + 백엔드 + 플랫폼 특화형

### 3. 면접 답변으로 연결 가능해야 한다
각 프로젝트는 아래 질문에 답할 수 있어야 합니다.
- 왜 만들었는가
- 무엇을 구현했는가
- 어떤 기술을 썼는가
- 어떤 문제를 해결했는가

---

## 대표 프로젝트와 공개 샘플
### 1. opsmate-local
- 구매 요청, 고정 정책 검색, 승인과 발주 생성의 수직 흐름
- Java/Spring 기반 업무 트랜잭션과 로컬 오픈웨이트 LLM 통합
- 모델 장애 시 쓰기 경로를 차단하는 온프레미스 Agent 대표 프로젝트
- 공개 Thymeleaf UI, workspace 격리·TTL, model guard, PostgreSQL 역할 분리와 Docker/Caddy open/close 자산 구현
- 현재 `implemented`, `tested-component`; `2026-08-04` 전체 `clean verify` 54개 성공, 실패·오류·건너뜀 0개
- 승인된 실제 모델 E2E, 공개 URL·외부 smoke·외부 네트워크 정책과 양 호스트 rehearsal은 미검증
- [코드와 실행 절차](opsmate-local/README.md)

### 2. case-study-samples
- 기존 회사 코드를 복사하지 않은 독립 재현 샘플
- Spring Security 인증, MyBatis·SQL 정합성/성능, WAR 배포 이식성 순으로 검토
- 합성 데이터와 테스트 필수
- Spring Security 인증 브리지: `sample-verified`, 24개 테스트 성공
- [인증 브리지 코드와 실행 절차](case-study-samples/spring-security-auth-bridge/README.md)

### 3. 기존 지원 프로젝트
- `ai-rag-api`: 기존 RAG 구현 샘플, 대표 프로젝트 아님
- `backend-platform-template`: 현재 `partial`
- `security-audit-log`: 현재 `partial`

---

## 한 줄 정리
이 폴더의 프로젝트들은 손기석이 **백엔드, AI 응용, 운영, 보안**을 함께 이해하는 엔지니어라는 점을 보여주기 위한 실전형 포트폴리오 산출물입니다.
