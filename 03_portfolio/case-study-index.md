# Case Study Index

- 최종 갱신일: `2026-08-29`
- 목적: 기존 개인·회사 작업에서 게시물 후보를 추출하고, 원본 증거 확인과 공개용 재구현 상태를 분리해 관리
- 전략: [`portfolio-strategy.md`](portfolio-strategy.md)
- 회사 근거 규칙: [`../evidence/company-github/README.md`](../evidence/company-github/README.md)

## 현재 실행 대상

- `CS-JAVA-02` — MyBatis 기간 조회의 정합성과 인덱스 친화 조건을 함께 설계하기
- 권한 있는 비공개 원본에서 본인 귀속 SQL 변경 범위를 재확인했다.
- 회사 SQL·schema·식별자·데이터를 복사하지 않고 합성 Spring Boot/MyBatis/H2 샘플을 구현했다.
- `filter/count/page` 정합성, 결정적 정렬, index-friendly range predicate와 입력 실패 경계를 자동 테스트로 검증했다.
- MyBatis `BoundSql`에서 cross-year 구간이 상호 배타적 `UNION ALL`로 구성되고 indexed year/month column 변환과 기간 `OR`이 없는지 검증했다.
- 공개 샘플 Maven CI는 성공했다. 실제 Oracle 실행계획과 운영 성능 수치는 여전히 별도 미검증 boundary다.
- 다음 gate: 전체 PR regression과 GitHub Pages 게시·링크 확인 후 `published` 여부를 판정한다.

## 상태 정의

| 상태 | 의미 |
|---|---|
| `candidate` | 게시물 주제로만 선별됨 |
| `source-reviewed` | 원본 코드와 본인 귀속을 권한 있는 환경에서 확인 |
| `redaction-reviewed` | 공개 금지 정보와 재구현 경계를 검토 |
| `sample-implemented` | 회사 코드와 독립된 공개 샘플 코드 존재 |
| `sample-verified` | 공개 샘플의 최근 테스트 성공 기록 존재 |
| `published` | 게시물, 코드와 검증 근거가 서로 일치하고 공개 Pages에서 확인됨 |
| `hold` | 귀속, 보안 또는 구현 근거가 부족해 사용 보류 |

## Priority 1. Java/Spring 실무 깊이

| ID | 게시물 후보 | 근거 | 현재 상태 | 공개 코드 방식 | 다음 확인 |
|---|---|---|---|---|---|
| `CS-JAVA-01` | [레거시 SSO와 DB 계정 인증을 Spring Security로 통합한 과정](case-studies/spring-security-auth-bridge.md) | `WORK-EDU-01`, `E2`, 회사 비공개 코드와 본인 귀속 확인 + 공개 샘플 24개 테스트 성공 | `sample-verified` | [가상 사용자 저장소, issuer·audience 바인딩 SSO adapter와 RBAC 독립 샘플](../02_projects/case-study-samples/spring-security-auth-bridge/README.md) | 공개 블로그 배포 후 링크·렌더링 확인 |
| `CS-JAVA-02` | [MyBatis 기간 조회의 정합성과 인덱스 친화 조건을 함께 설계하기](case-studies/mybatis-query-correctness.md) | `WORK-EDU-01`, `E2`, 권한 있는 비공개 원본에서 본인 귀속 SQL 개선 범위 재확인 + 공개 샘플 12개 테스트 성공 | `sample-verified`, **active** | [합성 Spring Boot/MyBatis/H2 샘플](../02_projects/case-study-samples/mybatis-query-correctness/README.md): 기간 구간 분해, count/page 공통 filter, 결정적 pagination, SQL shape 검증 | 전체 PR regression과 Pages 게시 확인; Oracle 실행계획·운영 성능 수치는 별도 증거 전 사용 금지 |
| `CS-JAVA-03` | [WAR 기반 Spring MVC/JSP 서비스를 환경 독립적으로 배포한 과정](case-studies/war-deployment-portability.md) | `WORK-EDU-01`, `E2`, 회사 비공개 코드와 본인 귀속 확인 | `source-reviewed` | Tomcat, context path, profile, health와 rollback을 포함한 독립 샘플 | 내부 인프라 제거 후 배포·경로 회귀 시나리오 작성 |
| `CS-JAVA-04` | Java 업무 화면과 Python 분석 결과 재적재를 연결한 데이터 서비스 | `WORK-DATA-01`, `E2`, `implemented` | `candidate` | 가상 분석 결과와 DB를 사용하는 Java/Python 연계 샘플 | 개인 기여 코드, 트랜잭션 경계와 운영 반영 범위 재확인 |
| `CS-JAVA-11` | [Java/Spring 통계 품질 분석 화면에서 CSV·Excel 데이터와 Xbar-R 시각화를 연결한 과정](case-studies/statistical-analysis-ui.md) | `WORK-DATA-03`, `E2`, 추가 업무 계정 소유와 비공개 코드 일부 기여 확인 | `source-reviewed` | 합성 측정값과 공개 수식으로 업로드·집계·차트 흐름을 독립 구현 | 직접 기여 범위를 화면·데이터 처리 단위로 제한하고 통계 정확도 테스트 설계 |
| `CS-JAVA-05` | 엔터프라이즈 웹·API·DB 연동의 장애와 운영 개선 사례 | `WORK-PLATFORM-01`, `E2`, `implemented` | `candidate` | 도메인을 일반화한 API 연동·재시도·추적 샘플 | 하나의 구체적 문제로 범위 축소 및 본인 기여 재확인 |
| `CS-JAVA-06` | [여러 화면에 흩어진 기준값과 사용자 식별 규칙을 한 흐름으로 정합화한 과정](case-studies/business-rule-consistency.md) | `WORK-EDU-01`, `E2`, 회사 비공개 코드와 본인 귀속 확인 | `source-reviewed` | 주문·회원 같은 합성 도메인으로 Controller-Service-Mapper 규칙과 회귀 테스트 재현 | 기존 사례와 중복되지 않는 도메인 규칙·회귀 결함 범위 확정 |

### 공개 전 별도 보안 검토가 필요한 개인 코드 후보

아래 후보는 비공개 개인 업무 코드에서 본인 귀속과 구현을 확인했습니다. 원본 저장소는 공개 안전성 검토가 끝나지 않았으므로 링크하거나 원문을 사용하지 않습니다.

| ID | 게시물 후보 | 강점 | 현재 상태 | 공개용 보완 |
|---|---|---|---|---|
| `CS-JAVA-07` | 관리자·사용자 Spring 서비스를 단일 코드베이스와 Profile로 통합한 과정 | Gradle, Spring Boot, `@Profile`, 환경 설정, PID 기반 운영 스크립트 | `hold` | 합성 서비스로 재구현하고 종료·stale PID·회귀 테스트 추가 |
| `CS-JAVA-08` | 클라이언트 인증 상태를 서버 세션 검증과 요청 제한으로 옮긴 과정 | Spring MVC, HttpSession, 요청 제한, Spring Security | `hold` | 만료, 검증 시도 제한, 로그 마스킹과 자동 테스트를 포함해 재설계 |
| `CS-JAVA-09` | 확장자와 파일 시그니처를 함께 검사하는 업로드 검증 | Multipart 처리, allowlist, magic number, 설정 외부화 | `hold` | 크기·단축 입력·MIME 불일치·악성 fixture 테스트 추가 |
| `CS-JAVA-10` | DB 버전 차이로 깨진 목록 페이징을 호환 구조로 복구한 과정 | MyBatis, pagination, 레거시 DB 호환성 | `hold` | Testcontainers 기반 버전별 재현과 실행계획 비교 |

`CS-JAVA-01`과 `CS-JAVA-02`는 회사 코드와 독립된 공개 샘플의 최근 테스트가 성공해 `sample-verified`입니다. 나머지 `source-reviewed` 사례는 공개 재현 코드와 테스트가 생기기 전 완료 상태로 올리지 않습니다. GitHub Pages에서 글·코드·검증 링크를 확인하기 전에는 `published`로 표시하지 않습니다.

## Priority 2. AI 응용과 검증

| ID | 게시물 후보 | 근거 | 현재 상태 | 공개 코드 방식 | 다음 확인 |
|---|---|---|---|---|---|
| `CS-AI-01` | [Text2SQL/NL2SQL을 안전한 API 기능으로 만든 과정](case-studies/text2sql-validation.md) | `WORK-AI-01`, `E3`, `tested-component` | `source-reviewed` | SELECT 제한, schema validation과 합성 DB를 사용하는 독립 샘플 | benchmark와 정확도 표현을 분리하고 공개 데이터로 재검증 |
| `CS-AI-02` | [Agentic Runtime의 작업 격리와 artifact/provenance 추적](case-studies/agent-runtime-artifact-provenance.md) | `WORK-AI-02`, `E3`, `tested-component` | `source-reviewed` | workspace 탈출 차단과 manifest 추적을 일반화한 샘플 | 전체 플랫폼이 아닌 본인 구현 구성요소 경계 명시 |
| `CS-AI-03` | HWP 문서 구조를 보존하는 검색·적재 파이프라인 | [개인 공개 저장소](https://github.com/son1004007/hwp-ingest-rag-pipeline), 코드 존재 | `candidate` | 기존 공개 코드를 정리하고 테스트 추가 | README-구현 불일치, 고정 경로와 기본 DB 설정 제거 |

## Priority 3. 데이터·운영·보안 보조 사례

| ID | 게시물 후보 | 근거 | 현재 상태 | 공개 코드 방식 | 다음 확인 |
|---|---|---|---|---|---|
| `CS-DATA-01` | 재현 가능한 데이터 분석 pipeline과 결과 보고서 생성 | `WORK-DATA-02`, `E3`, `tested-component` | `source-reviewed` | 합성 데이터, CLI, SQL runner, manifest와 HTML 보고서 샘플 | 독립 재실행과 CI 성공 기록 추가 |
| `CS-OPS-01` | 문서 파싱과 DB 적재 전 dry-run·검증·CSV export | `WORK-INTEGRATION-01`, `E2`, `implemented` | `candidate` | 가상 문서와 DB를 사용하는 ingestion 검증 샘플 | 원본 역할, 실패·복구 시나리오와 테스트 확인 |
| `CS-SEC-01` | Linux 보안 점검 결과를 상태와 권고로 구조화한 도구 | [개인 공개 저장소](https://github.com/son1004007/kisa-infra-check-linux), 일부 코드 존재 | `hold` | 구현된 점검 항목만 남기고 fixture 기반 테스트 추가 | README의 전체 항목 구현 주장과 실제 코드 범위 정합화 |

추가 업무 계정은 `2026-08-03` 소유자 확인을 거쳐 본인 계정으로 분류했습니다. 해당 계정의 커밋도 원본에서 직접 기여가 확인된 범위만 사용하며, 회사 코드와 데이터에는 동일한 비식별·독립 재구현 규칙을 적용합니다.

## 게시물 완료 조건

각 사례는 다음 항목이 모두 있어야 `published`로 표시합니다.

- 비식별 문제 정의와 제약조건
- 개인 담당 범위와 팀 결과의 구분
- 대안 비교와 설계 결정
- 회사 코드와 독립된 최소 재현 코드
- 합성 데이터 또는 공개 데이터
- 정상, 실패와 경계 조건 테스트
- 실행 명령과 최근 성공 결과
- 공개 안전성 검토
- 확인된 결과와 확인하지 못한 한계

## AI 작업 지침

다음 작업을 시작하는 AI는 이 순서를 따릅니다.

1. 후보 ID와 연결된 공개 evidence ID를 확인합니다.
2. 권한이 있으면 회사 원본에서 본인 귀속과 코드 범위를 다시 확인합니다.
3. 원본 코드를 복사하지 않고 공개용 요구사항과 테스트부터 새로 작성합니다.
4. `02_projects/case-study-samples/<slug>/`에 독립 샘플을 구현합니다.
5. 검증이 끝난 뒤 `03_portfolio/case-studies/<slug>.md`를 작성합니다.
6. 이 인덱스와 [`evidence-index.md`](evidence-index.md)의 상태를 함께 갱신합니다.
