# Spring Security Auth Bridge

> 상태: `sample-verified`. 최근 실행 결과는 [`VERIFICATION.md`](VERIFICATION.md)에 기록합니다.

DB 계정 로그인과 레거시 SSO 로그인을 하나의 Spring Security 세션과 RBAC 모델로 수렴시키는 독립 재현 샘플입니다. 실제 회사 코드, 도메인, 계정 체계, 설정 또는 식별자는 사용하지 않았습니다.

- 런타임: Java 21
- 프레임워크: Spring Boot 3.5.16, Spring Security 6.5.x
- 빌드: Maven Wrapper 3.9.9

## 보여주는 역량

- `AuthenticationManager`를 사용하는 DB 계정 인증
- issuer·audience·keyId·HMAC 서명·발급 시각·nonce를 검증하는 일반화된 SSO adapter
- 인증 경로와 무관하게 로컬 계정의 역할만 권한으로 사용하는 RBAC
- 인증 성공 시 세션 고정 공격을 막는 session id rotation
- 로그인, 로그아웃, 상태 변경 요청에 적용되는 CSRF 방어
- 로그인 성공 시 기존 CSRF token을 폐기하는 세션 연계 token rotation
- JSON 기반 `401`, `403`, `503` 실패 계약
- 정상·실패·시간 경계·replay 시나리오 자동 테스트

## 합성 사용자

| 사용자 | SSO subject | 역할 | 상태 |
|---|---|---|---|
| `analyst` | `sso-analyst-001` | `USER`, `ANALYST` | 활성 |
| `admin` | `sso-admin-001` | `USER`, `ADMIN` | 활성 |
| `user` | `sso-user-001` | `USER` | 활성 |
| `disabled` | `sso-disabled-001` | `USER` | 비활성 |

비밀번호와 SSO 공유 비밀은 코드에 기본값을 두지 않습니다. 실행자가 환경변수로 제공해야 하며, SSO 비밀이 없거나 32바이트 미만이면 SSO 경로만 `503 SSO_ADAPTER_UNAVAILABLE`로 닫힙니다.

SSO assertion은 합성 `portfolio-demo-idp` issuer, `spring-security-auth-bridge` audience와 `demo-key-v1` keyId에 고정됩니다. 서명이 맞아도 세 값 중 하나가 다르면 다른 서비스·환경 또는 이전 key의 assertion으로 보고 거부합니다. 공유 비밀은 relying party별로 고유해야 합니다.

## API 요약

| Method | Path | 권한 | 설명 |
|---|---|---|---|
| `GET` | `/auth/csrf` | 공개 | CSRF token과 세션 준비 |
| `POST` | `/auth/db` | 공개 + CSRF | DB 계정 로그인 |
| `POST` | `/auth/sso` | 공개 + CSRF | 서명된 SSO assertion 로그인 |
| `POST` | `/auth/logout` | 로그인 + CSRF | 세션 종료 |
| `GET` | `/api/me` | 로그인 | 현재 사용자와 역할 |
| `GET` | `/api/reports/monthly` | `ANALYST` 또는 `ADMIN` | 합성 보고서 조회 |
| `POST` | `/api/admin/reindex` | `ADMIN` + CSRF | 합성 관리자 작업 |

## 빠른 검증

Java 21에서 저장소에 포함된 Maven Wrapper로 실행합니다.

```powershell
.\mvnw.cmd test
```

애플리케이션 실행과 수동 호출은 [`SETUP.md`](SETUP.md), 설계와 신뢰 경계는 [`ARCHITECTURE.md`](ARCHITECTURE.md), 사례 설명은 [`../../../03_portfolio/case-studies/spring-security-auth-bridge.md`](../../../03_portfolio/case-studies/spring-security-auth-bridge.md)에서 확인할 수 있습니다.

## 의도적인 제한

- 외부 IdP, LDAP, 실제 DB 대신 합성 in-memory adapter를 사용합니다.
- HMAC assertion은 신뢰 경계와 실패 정책을 보여주기 위한 최소 형식이며 표준 OIDC/SAML을 대체하지 않습니다.
- 한 번에 하나의 `active-key-id`와 secret만 지원하므로 무중단 key 교체의 이전·신규 key 중첩 기간은 구현하지 않았습니다.
- 다중 인스턴스 환경에서 nonce 저장소와 세션은 Redis 같은 공유 저장소로 교체해야 합니다.
- 로그인 시도 제한, 계정별 lockout과 IP rate limit은 구현하지 않았습니다.
- 인증 성공·실패, 권한 거부와 로그아웃 감사 이벤트 저장은 구현하지 않았습니다.
- 성능, 실제 운영 트래픽, 회사 시스템의 운영 성과를 주장하지 않습니다.
