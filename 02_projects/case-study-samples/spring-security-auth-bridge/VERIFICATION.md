# Verification

## 독립 샘플 검증

- 실행일: `2026-08-03` (`Asia/Seoul`)
- 운영체제: Windows 10, amd64
- Java: Microsoft OpenJDK `21.0.5`
- Spring Boot: `3.5.16`
- Maven Wrapper: Apache Maven `3.9.9`
- 명령: `.\mvnw.cmd -q clean verify`
- 결과: `BUILD SUCCESS`
- 테스트: `24` 실행, 실패 `0`, 오류 `0`, 건너뜀 `0`

## 현재 CI·게시 증거

- GitHub Actions Java matrix에 `Spring Security auth bridge`가 포함됨
- CI 명령: `./mvnw -q clean verify` on Temurin Java 21
- main Pages run `33276912458`: `verify / Spring Security auth bridge` PASS
- 같은 run의 public portfolio 검사와 Jekyll site build PASS
- 같은 run의 Pages artifact build와 deploy PASS
- publication 기준 main commit: `c74655a2e7aacfa0d05f41bc594598a0c0f73296`

이 publication evidence는 공개 샘플·문서·Pages 배포의 정합성을 확인합니다. 실제 회사 인증 시스템 또는 외부 IdP 운영 검증을 의미하지 않습니다.

## 검증된 범위

- DB 계정 정상·오류·비활성 로그인
- SSO 정상 로그인과 로컬 역할 수렴
- issuer, audience와 active keyId 불일치 assertion 거부
- 잘못된 서명, 만료, 미등록·비활성 사용자
- 알 수 없는 역할 필드 거부와 nonce replay 방지
- 인증 전 session id rotation
- `/auth/csrf`의 실제 session·cookie·header token을 사용한 로그인
- 로그인 전 token 폐기, 새 token 발급·사용과 이전 token 재사용 거부
- 활성 `USER`의 reports `403`, `ADMIN`의 reports `200`
- 세션 없음, 역할 부족, 로그인·로그아웃·관리자 쓰기의 CSRF 실패와 로그아웃 세션 폐기
- 유효한 CSRF token이 있어도 익명 로그아웃 `401`
- SSO 시간 범위 양 끝값과 1초 밖 경계
- 미래 skew assertion의 마지막 유효 초까지 replay 차단
- canonical delimiter 입력 거부
- SSO 공유 비밀 누락·길이 부족 시 fail-closed

## 해석 제한

이 결과는 단일 JVM의 합성 in-memory adapter와 MockMvc 경계를 검증합니다. 외부 IdP, 실제 DB, 분산 세션, Redis nonce 저장소, 로그인 rate limit, 감사 이벤트, 부하와 운영 가용성은 검증하지 않았습니다. HMAC adapter는 active key 하나만 지원해 key 교체 중 이전·신규 key 중첩 검증도 포함하지 않습니다.

JDK 21 테스트 종료 시 Mockito/Byte Buddy의 동적 agent loading 관련 향후 호환성 경고가 출력됐습니다. 테스트 실패는 아니며, 향후 JDK에서 동적 agent loading 기본 정책이 바뀌면 테스트 JVM agent 설정을 명시적으로 갱신해야 합니다.
