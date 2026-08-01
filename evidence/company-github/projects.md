# Sanitized Company Project Evidence

이 문서는 회사 비공개 GitHub에서 확인한 본인 기여를 채용용으로 비식별화한 요약입니다. 원본 저장소명과 고객 식별자는 공개하지 않습니다.

## WORK-AI-01. 정형데이터 Text2SQL/NL2SQL 서비스

- 확인된 활동 기간: `2026-03`부터 `2026-04`
- 역할: AI 응용서비스 백엔드 구현과 모델 비교 검증
- 기술: Python, FastAPI, PostgreSQL, Hugging Face, Ollama, OpenAI-compatible API, SQL validation
- 구현: 질문과 schema를 이용한 SQL 생성, SELECT 전용 검증, 선택적 DB 실행, 논리명 기반 응답 데이터, 결과 저장과 benchmark report
- 검증: 현업 질문 13개와 로컬 모델 4개의 52회 비교, 29개 질문 validation set과 결과 보고서 기록
- 상태: `E3`, `tested-component`
- 제한: 일반 unit test보다 benchmark suite와 실제 SQL 실행 결과가 주요 검증 근거입니다. 실서비스 장기 운영, 모델 학습과 fine-tuning은 확인된 범위가 아닙니다.

## WORK-AI-02. 데이터 분석 Agentic AI Runtime

- 확인된 활동 기간: `2026-06`부터 `2026-07`
- 역할: 분석 job의 실행 및 산출물 추적을 위한 Runtime backend 구성요소 구현
- 기술: Python, FastAPI/Pydantic 계열 schema, pytest, multi-agent runtime contracts
- 구현: 안전한 저장 경로, 작업별 격리 workspace, artifact와 provenance schema/store, manifest, 공통 job storage manager
- 검증: workspace, artifact, provenance, 안전하지 않은 경로 차단 관련 테스트 커밋 확인
- 상태: `E3`, `tested-component`
- 제한: OpenClaw, Azure 모델, 로컬 모델의 전체 실호출과 end-to-end 운영 완료는 별도 확인이 필요합니다.

## WORK-AI-03. LLM 기반 분석 업무 workflow

- 확인된 활동 기간: `2026-05`
- 역할: 장애 진단, 분석 지원, RAG 확장을 위한 workflow와 human review 기준 설계
- 산출물: 역할, 단계, 검증, 산출물 관리, 로드맵 문서
- 근거 범위: 비공개 local Git의 author metadata와 문서. 기본 ref 도달 여부는 미확인
- 상태: `E2`, `designed`
- 제한: 문서 중심 기여이므로 구현 완료로 사용하지 않습니다. 아래 303건의 인증 계정 집계에는 포함하지 않습니다.

## WORK-AI-04. 다중 모델 LLM gateway pilot

- 확인된 활동 기간: `2026-04`
- 역할: 여러 로컬 모델의 호출 경로와 개발도구 연계를 위한 gateway pilot 구현
- 기술: FastAPI, Docker Compose, vLLM, shell scripts
- 구현: gateway app, container 구성, 두 개 모델 실행 스크립트, gateway 실행 절차와 환경 예시
- 상태: `E2`, `implemented`
- 제한: 자동 테스트와 실행 결과가 확인되지 않았습니다. 프로덕션 LLM 플랫폼, 고가용성 또는 대규모 serving 경험으로 표현하지 않습니다.

## WORK-PLATFORM-01. 엔터프라이즈 데이터 서비스

- 확인된 활동 기간: `2024-05`부터 `2026-07`
- 역할: Java/Python 기반 웹, API, DB 연계, 데이터 결과 서비스화, 운영 개선
- 근거: 여러 회사 비공개 저장소에서 본인 author identity에 귀속되는 지속적인 커밋 활동 확인
- 상태: `E2`, `implemented`
- 제한: 커밋 수는 성과가 아닙니다. 프로젝트별 본인 역할, 배포 여부와 결과는 이력서 작성 전에 원본과 다시 대조합니다.

## WORK-OPS-01. 개발환경 및 배포 운영 개선

- 확인된 활동 기간: `2026-03`부터 `2026-07`
- 역할: Linux 원격 개발환경, 배포 자동화, Git 및 개발 규칙, 운영 가이드 정리
- 상태: `E2`, `implemented`
- 제한: 조직 전체 운영 책임이나 공식 표준 제정으로 확대하지 않습니다.

## WORK-DATA-01. 운영 데이터 서비스 및 ModelOps 개선

- 확인된 활동 기간: `2026-01`부터 `2026-07`
- 역할: 운영 데이터 화면과 DB 연계, Python 배치와 결과 재적재, 분석 및 ModelOps 기능 개선
- 기술: Java, JSP, MyBatis, Python, SQL, shell scripts
- 상태: `E2`, `implemented`
- 제한: 자동 테스트와 성능 개선 수치는 확인되지 않았습니다. 프로젝트 전체 성과나 단독 구현으로 표현하지 않습니다.

## WORK-DATA-02. 재현 가능한 데이터 분석 pipeline

- 확인된 활동 기간: `2026-06`부터 `2026-07`
- 역할: 읽기 전용 데이터 분석, CLI와 SQL runner, 재현 manifest와 HTML 보고서 생성 흐름 구현
- 검증: pipeline 테스트 파일과 내부 검증 기록 확인
- 상태: `E3`, `tested-component`
- 제한: 이 공개 점검에서는 독립 재실행과 CI 성공을 확인하지 않았습니다.

## WORK-EDU-01. 데이터 플랫폼 및 배포 자동화

- 확인된 활동 기간: `2026-01`부터 `2026-07`
- 역할: DB 인증, 분석 화면과 필터 기능, 제한형 배포, 검증, health와 rollback 기반 개선
- 상태: `E2`, `implemented`
- 제한: 자동화 코드는 있으나 test와 운영 성과는 별도 확인이 필요합니다.

## WORK-INTEGRATION-01. 문서 및 애플리케이션 데이터 연계

- 확인된 활동 기간: `2026-02`부터 `2026-07`
- 역할: 문서 파싱 보완, dry-run, DB 확인과 CSV export 도구 구현
- 상태: `E2`, `implemented`
- 제한: 전체 운영 pipeline 완료나 처리 성능 수치로 확대하지 않습니다.

## 권장 한 줄 포지셔닝

> 엔터프라이즈 데이터 시스템 경험을 바탕으로 Python/FastAPI로 Text2SQL, RAG, Agentic AI 기능을 서비스화하는 AI/LLM 응용서비스 백엔드 엔지니어

## 2026년 계정 귀속 활동 요약

인증된 GitHub 검색 기준 2026년 1월부터 7월까지 현재 회사 조직의 26개 비공개 저장소에서 본인 계정에 귀속된 author commit 검색 결과 1,241건을 확인했습니다. 저장소 간 동일 SHA의 중복 여부는 독립 확인하지 않았으므로 고유 커밋 수로 표현하지 않습니다. 이 중 LLM 구현 세 작업축은 `WORK-AI-01` 89건, `WORK-AI-04` 47건, `WORK-AI-02` 167건으로 합계 303건이며 3월부터 7월까지 이어졌습니다. `WORK-AI-03` 설계 문서 작업은 이 303건에 포함하지 않습니다.

이 수치는 업무 탐색 범위와 지속성을 보여주는 메타데이터입니다. micro-commit, 문서 커밋과 브랜치 상태의 영향을 받으므로 생산성, 투입시간 또는 성과 점수로 사용하지 않습니다. 상세 비식별 집계는 [`snapshots/authenticated-account-summary-2026-08-01.json`](snapshots/authenticated-account-summary-2026-08-01.json)에 있습니다.
