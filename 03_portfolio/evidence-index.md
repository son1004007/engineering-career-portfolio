# Portfolio Evidence Index

- 최종 점검일: `2026-07-31`
- 목적: 프로필 주장, 구현 코드, 테스트, 계획을 구분해 과장 없는 기술 검토를 가능하게 함

## 상태 정의

| 상태 | 의미 |
|---|---|
| `verified` | 최근 실행일, 명령, 환경·버전, 성공 결과가 함께 기록됨 |
| `implemented` | 핵심 코드가 저장소에 존재 |
| `tested-file-present` | 테스트 파일은 있으나 최근 성공 실행이 확인되지 않음 |
| `partial` | 일부 코드·문서만 있고 실행에 필요한 구성요소가 빠짐 |
| `planned` | 문서나 작업 목록에만 존재 |
| `self-described` | 프로필·경력 문서의 자기기술. 다른 근거로 확인 필요 |

## 프로젝트 상태

| 프로젝트 | 코드 | 테스트 | 문서 | 현재 상태 | 확인 사항 |
|---|---:|---:|---:|---|---|
| [`ai-rag-api`](../02_projects/ai-rag-api/README.md) | 있음 | 있음 | README·ARCHITECTURE·SETUP | `implemented`, `tested-file-present` | 현재 점검 환경에 `pytest`가 없어 성공 실행 미확인. 실제 LLM·벡터 저장소 품질과 운영성 별도 검증 필요 |
| [`backend-platform-template`](../02_projects/backend-platform-template/README.md) | 일부 | 있음 | README·ARCHITECTURE·SETUP | `partial` | `app/main.py`가 존재하지 않는 `app.api.routes`를 import. 인증·모니터링·PostgreSQL·Redis 구현도 현재 파일 목록에서 확인되지 않음 |
| [`security-audit-log`](../02_projects/security-audit-log/README.md) | route만 있음 | 없음 | README | `partial` | route가 존재하지 않는 `app.service.audit_service`를 import. 앱 진입점, 저장 모델, service, tests 보완 필요 |
| `text2sql` | 없음 | 없음 | 상위 문서의 계획만 있음 | `planned` | 프로젝트 디렉터리와 구현·테스트 필요 |
| `security-backend-platform` | 없음 | 없음 | 상위 문서의 예시만 있음 | `planned` | 별도 프로젝트로 완료되기 전 기술 근거로 사용 금지 |

## 공개 프로필 주장

| 주장 | 현재 분류 | 안전한 표현 | 추가 근거 |
|---|---|---|---|
| Java/Spring 백엔드 개발 경험 | `self-described` | Java/Spring 기반 업무·데이터 서비스 개발 경험을 경력 문서에 기재 | 회사·프로젝트 기간, 역할, 코드·산출물 색인 |
| Python/FastAPI API 구현 | `self-described` + 개인 코드 `implemented` | 실무 자기기술과 FastAPI 포트폴리오 코드가 각각 존재 | 실무 범위와 개인 샘플을 분리한 근거 |
| 데이터 분석 결과 서비스화 | `self-described` | 분석 결과를 웹/API로 연결한 경험을 경력 문서에 기재 | 프로젝트별 역할, 기간, 운영 반영, 성과 |
| Text2SQL/RAG/LLM PoC | `self-described`; RAG 개인 샘플 일부 구현 | AI 모델 연구보다 서비스/API 연계 경험으로 표현 | 실무 산출물, 본인 기여, 테스트·평가 결과 |
| Linux/Docker/Jenkins 운영 반영 | `self-described` | 배포·환경 구성·장애 분석 경험을 경력 문서에 기재 | 운영 범위, 책임, 자동화, 장애·개선 결과 |
| 보안 경력과 자격 | `self-described` | 보안·통제 관점을 가진 백엔드 전환형 프로필 | 재직 연표, 자격 유효 상태, 실제 담당 업무 |

## 현재 판단

이 저장소는 다음을 보여주기에 적합합니다.

- 목표하는 엔지니어 정체성과 기술 조합
- 작은 FastAPI/RAG 구현과 테스트 설계 방식
- 백엔드 구조, 운영, 보안·감사 관점을 함께 보려는 방향
- 문서화와 구조화 능력

다음에는 아직 충분하지 않습니다.

- 프로덕션 수준 백엔드·플랫폼 숙련도 증명
- 대규모 트래픽·분산 시스템·클라우드 네이티브 운영 증명
- 프로젝트별 정량 성과와 본인 기여 범위 증명
- 모든 README에 적힌 기능의 실제 구현 증명

## 갱신 규칙

프로젝트 상태를 올릴 때는 다음 근거를 함께 추가합니다.

```text
partial -> implemented:
실행에 필요한 핵심 모듈과 진입점 추가

implemented -> tested-file-present:
핵심 경로 테스트 파일 추가

tested-file-present -> verified:
최근 테스트 명령, 성공 결과, 환경·버전 기록
```

경력 주장에는 가능한 경우 다음을 연결합니다.

```text
기간
역할과 개인 기여
문제와 구현
운영 반영 여부
검증된 성과
공개 가능한 산출물 또는 비식별 근거 색인
```
