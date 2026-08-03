# Engineering Career Portfolio

[공개 포트폴리오 보기](https://son1004007.github.io/engineering-career-portfolio/)

Java/Spring으로 업무 시스템을 개발해 왔습니다. Python/FastAPI 기반 Text2SQL/NL2SQL API와 로컬 모델 비교를 경험했고, 현재는 Spring Boot에서 LLM 초안을 권한·승인 흐름 안에 연결하는 `OpsMate Local`을 구현하고 있습니다. 인증, 데이터, 트랜잭션과 운영을 포함한 백엔드 구현에 집중합니다.

## 대표 작업

### OpsMate Local

구매 요청부터 승인·발주까지의 흐름에 AI 초안 생성을 결합한 Spring Boot 프로젝트입니다. 정책 조회, 권한, 상태 전이, 중복 방지와 발주는 서버가 통제합니다.

- Java 21, Spring Boot, Spring Security, Spring Data JPA
- 구매 요청·승인·반려·발주·감사 이벤트 구현
- 권한 위반, 모델 오류, 중복 요청과 트랜잭션 롤백을 포함한 자동화 테스트 19개 통과
- 실제 오픈웨이트 모델 E2E와 운영 배포는 아직 검증 전

[프로젝트 설명과 코드](02_projects/opsmate-local/README.md)

### Spring Security 인증 통합 사례

DB 로그인과 레거시 SSO의 사용자·권한 처리를 하나의 Spring Security 흐름으로 통합한 독립 재현 사례입니다. 회사 코드를 사용하지 않고 합성 사용자와 일반화된 SSO 형식으로 새로 구현했습니다.

- 로컬 사용자 상태와 역할을 최종 권한 기준으로 사용
- 세션 회전, CSRF 수명주기, assertion 검증과 replay 차단 구현
- 정상·실패·권한·시간 경계 시나리오 자동화 테스트 24개 통과

[문제 해결 과정](03_portfolio/case-studies/spring-security-auth-bridge.md) · [공개 샘플 코드](02_projects/case-study-samples/spring-security-auth-bridge/README.md)

## 실무 경험 하이라이트

- Java/Spring 기반 웹 서비스와 API, Oracle/MyBatis 조회, 인증·권한과 배포 환경 개선
- Python/FastAPI 기반 Text2SQL/NL2SQL API, SQL 검증과 로컬 모델 비교
- Agent Runtime의 작업 공간 격리와 artifact·provenance 추적 구성요소
- Linux, Tomcat, Nginx, Docker와 Jenkins 기반 배포·운영
- 정보보호 업무 경험을 바탕으로 한 권한, 감사와 실패 처리 관점

비공개 업무 사례는 회사 자산을 공개하지 않는 범위에서 [기술 사례](03_portfolio/case-studies/README.md)로 정리했습니다.

## 기술

| 영역 | 사용 경험 |
|---|---|
| Backend | Java, Spring Boot, Spring MVC, Spring Security, JPA, MyBatis, Python, FastAPI |
| Data | Oracle, PostgreSQL, SQL, CSV·Excel 처리 |
| AI integration | Text2SQL/NL2SQL, RAG, 로컬 LLM adapter, 구조화 출력 검증 |
| Operations | Linux, Tomcat, Nginx, Docker, Jenkins |
| Engineering | 인증·인가, 상태 전이, 멱등성, 트랜잭션, 감사 이벤트 |

## 테스트 실행

두 대표 구현은 각 프로젝트의 Maven Wrapper로 재현할 수 있습니다.

```powershell
cd 02_projects\opsmate-local
.\mvnw.cmd -q clean verify

cd ..\case-study-samples\spring-security-auth-bridge
.\mvnw.cmd -q clean verify
```

저장소 전체 링크, 공개 문구와 상태 정합성 검사는 다음 명령으로 확인합니다.

```powershell
python -B -m unittest discover -s tests -p "test_*.py" -v
```

## 공개 범위

이 저장소에는 회사 코드, 고객 데이터, 내부 URL과 실제 업무 규칙을 포함하지 않습니다. 실무 사례는 본인이 담당한 문제를 일반화해 설명하고, 공개 코드는 합성 데이터로 별도 구현합니다. 실제 모델 E2E나 운영 성능처럼 확인하지 않은 항목은 프로젝트 문서에 따로 명시합니다.
