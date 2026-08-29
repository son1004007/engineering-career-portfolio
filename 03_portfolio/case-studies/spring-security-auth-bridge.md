---
title: DB 로그인과 레거시 SSO를 하나의 Spring Security 권한 체계로 통합한 과정
description: DB 로그인과 가상 SSO를 로컬 RBAC·세션·CSRF 정책으로 통합한 독립 재현 사례
permalink: /cases/spring-security-auth-bridge/
status: published
---

# DB 로그인과 레거시 SSO를 하나의 Spring Security 권한 체계로 통합한 과정

공개 샘플과 실행 방법은 [`spring-security-auth-bridge`](../../02_projects/case-study-samples/spring-security-auth-bridge/README.md)에서 확인할 수 있습니다.

## 문제

엔터프라이즈 웹 서비스에서 DB 계정 로그인과 레거시 SSO 로그인이 함께 존재하면, 인증 성공 이후에도 서로 다른 사용자 표현과 권한 규칙이 남기 쉽습니다. 한 경로에서는 DB 역할을 사용하고 다른 경로에서는 외부 assertion의 값을 그대로 신뢰하면 같은 사용자라도 접근 결과가 달라지고 권한 상승 경계가 흐려집니다.

이 사례의 핵심 질문은 다음과 같습니다.

> 인증 수단은 두 개로 유지하면서도 세션, 사용자 상태와 RBAC 판단을 하나의 Spring Security 모델로 만들 수 있는가?

## 제약

- 기존 DB 계정 로그인을 한 번에 제거할 수 없음
- 레거시 SSO의 identity와 로컬 사용자 사이에 명시적 연결이 필요함
- 비활성 계정은 어떤 인증 경로에서도 접근하면 안 됨
- 외부 assertion이 애플리케이션 관리자 역할을 직접 부여하면 안 됨
- 기존 브라우저 기반 화면 때문에 서버 세션을 유지해야 함
- 로그인·로그아웃과 상태 변경 요청의 CSRF 실패를 구분해야 함

## 담당한 부분

비공개 업무 코드에서 본인이 담당한 Java/Spring Security 인증 흐름, DB 사용자 조회, 역할 기반 접근 제어와 배포·운영 설정을 확인했습니다. 공개 샘플에서는 두 인증 경로를 하나의 로컬 사용자·권한 모델로 수렴시키고 세션과 CSRF 수명주기를 함께 검증하도록 독립 구현했습니다.

조직 전체 인증 아키텍처나 SSO 제공자 구축, 공개 검증되지 않은 운영 성과는 이 사례의 범위에 포함하지 않았습니다.

## 검토한 대안

### 인증 경로별로 별도 SecurityFilterChain 유지

기존 변경을 줄일 수 있지만 사용자 상태, 실패 응답과 역할 규칙이 계속 분리됩니다. 경로별 회귀 테스트도 중복됩니다.

### SSO assertion의 역할을 그대로 권한으로 사용

구현은 단순하지만 IdP의 claim과 애플리케이션 역할 수명주기가 강하게 결합됩니다. 잘못된 claim mapping이 즉시 애플리케이션 권한 상승으로 이어질 수 있어 선택하지 않았습니다.

### 모든 인증을 stateless JWT로 전환

신규 API에는 적합할 수 있지만 기존 서버 렌더링 화면과 세션 기반 흐름까지 동시에 바꾸면 전환 범위가 커집니다. 이 사례에서는 인증 adapter만 분리하고 기존 세션 계약을 유지했습니다.

## 선택한 설계

두 인증 경로는 identity를 확인하는 방식만 다르고, 이후 흐름은 동일합니다.

```text
DB credentials -> AuthenticationManager --+
                                         +-> local VirtualUser -> local roles -> rotated session
signed SSO assertion -> SSO verifier -----+
```

설계 원칙은 다섯 가지입니다.

1. SSO assertion의 issuer·audience·keyId는 신뢰 출처, 대상 서비스와 active key를 한정하고, `subject`는 그 경계 안의 identity만 증명합니다.
2. 활성 상태와 역할은 로컬 사용자 저장소가 최종 결정합니다.
3. 인증 성공 시 기존 session id를 교체하고 SecurityContext를 명시적으로 저장합니다.
4. 로그인 성공 시 기존 CSRF token을 폐기하고 서버 세션·cookie에 연결된 새 token만 허용합니다.
5. 로그인까지 포함한 모든 `POST`는 CSRF token이 없으면 실행하지 않으며, 로그아웃은 인증된 세션도 요구합니다.

샘플 SSO adapter는 issuer, audience, keyId, HMAC-SHA256 서명, 발급 시각과 nonce를 함께 검증합니다. 과거 2분과 미래 10초의 시간 경계를 두고, 한 번 성공한 nonce는 다시 사용할 수 없습니다. 다른 서비스·환경 또는 이전 key의 assertion은 서명이 유효해도 거부합니다. 공유 비밀이 없거나 너무 짧으면 취약한 기본값을 사용하지 않고 SSO 경로를 `503`으로 닫습니다.

세부 구성과 교체 가능한 경계는 샘플의 [`ARCHITECTURE.md`](../../02_projects/case-study-samples/spring-security-auth-bridge/ARCHITECTURE.md)에 정리했습니다.

## 핵심 구현

- DB 인증: `AuthenticationManager`와 `DaoAuthenticationProvider`
- 사용자 경계: `VirtualUserRepository`와 in-memory adapter
- SSO 검증: `HmacSsoAssertionVerifier`
- 역할 수렴: `SsoAuthenticationService`
- 세션 저장: `SessionLoginService`
- CSRF 수명주기: `SessionCookieCsrfTokenRepository`
- HTTP 보안 계약: `SecurityConfiguration`, 인증 필수 logout controller, JSON entry point와 access denied handler

코드는 합성 계정 네 개와 합성 API만 사용합니다. 실제 업무 도메인, DB schema와 사내 역할명은 포함하지 않습니다.

## 테스트

테스트는 정상, 실패와 경계 조건을 분리합니다.

| 구분 | 검증 시나리오 |
|---|---|
| 정상 | DB 로그인, SSO 로그인, 실제 CSRF token·cookie 로그인, 세션 저장, 분석가·관리자 조회, 관리자 상태 변경 |
| 실패 | 잘못된 비밀번호·서명, issuer·audience·keyId 불일치, 비활성·미등록 계정, 세션 없음, 이전·누락 CSRF, 익명 로그아웃, 역할 부족, nonce replay |
| 경계 | `USER`와 `ADMIN`의 reports 권한, SSO 최대 과거 시각과 미래 skew의 포함 여부, 경계를 1초 벗어난 assertion, canonical delimiter 입력 |
| 설정 | SSO 비밀 누락·길이 부족 시 fail-closed |

`2026-08-03`에 Java 21, Spring Boot 3.5.16과 Maven Wrapper 3.9.9로 `24`개 테스트를 실행해 실패·오류·건너뜀 `0`을 확인했습니다. 이후 GitHub Actions의 `Spring Security auth bridge` job에서 동일 샘플의 `./mvnw -q clean verify`를 반복 회귀 검증했고, main Pages run `33276912458`에서 Spring Security job, 공개 텍스트·링크 검사, Jekyll build와 Pages deploy가 모두 성공했습니다. 상세 환경과 한계는 샘플의 [`VERIFICATION.md`](../../02_projects/case-study-samples/spring-security-auth-bridge/VERIFICATION.md)에 기록했습니다.

## 확인한 결과

이 독립 샘플에서는 단순한 로그인 endpoint를 넘어 다음 설계와 동작을 확인할 수 있습니다.

- 서로 다른 identity proof를 하나의 사용자·권한 모델로 수렴시키는 경계 설계
- 인증 실패, 인가 실패와 CSRF 실패를 구분한 API 계약
- 세션 고정, replay와 외부 역할 주입을 테스트 가능한 요구사항으로 바꾸는 방식
- 외부 인증 adapter가 실패해도 약한 기본값으로 우회하지 않는 정책

## 한계와 다음 단계

- **인증 연동:** HMAC assertion은 교육용 최소 protocol이며 active key 하나만 지원합니다. 운영 환경에서는 검증된 OIDC/SAML 라이브러리와 key 교체 절차가 필요합니다.
- **운영 구조:** 사용자와 nonce 저장소가 in-memory 방식이라 단일 프로세스만 지원합니다. rate limit, 계정 잠금, 구조화된 감사 이벤트와 세션 clustering도 구현 범위에 포함하지 않았습니다.
- **검증 범위:** 계정 연결 해제와 IdP 장애 복구, 성능, 대규모 동시 접속과 실제 운영 전환은 검증하지 않았습니다.

다음 확장 우선순위는 OIDC adapter 교체, Redis 기반 replay 방지와 Testcontainers 기반 사용자 저장소 검증입니다.

## 현재 공개 범위

회사명, 고객명, 내부 경로와 원본 코드·설정·데이터는 포함하지 않았습니다. 공개 코드는 합성 사용자와 일반화한 인증 요구사항으로 별도 구현했으며, 24개 테스트 결과와 Pages publication evidence는 이 독립 샘플과 공개 문서의 상태만 확인합니다. 실제 회사 시스템의 규모와 운영 성과를 검증한 결과로 해석하지 않습니다.
