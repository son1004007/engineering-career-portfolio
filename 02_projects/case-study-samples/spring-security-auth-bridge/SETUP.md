# Setup

## 요구사항

- JDK 21
- 저장소에 포함된 Maven Wrapper 3.9.9
- PowerShell 7 예시는 선택 사항

## 자동 테스트

환경변수 없이 테스트할 수 있습니다. 테스트 전용 합성 값은 test context에만 주입됩니다.

```powershell
.\mvnw.cmd test
```

macOS 또는 Linux에서는 `./mvnw test`를 사용합니다.

## 애플리케이션 실행

공개 샘플에 실행 비밀을 넣지 않기 위해 세 사용자 비밀번호와 SSO 공유 비밀을 환경변수로 요구합니다.

```powershell
$env:APP_DEMO_ANALYST_PASSWORD = 'replace-with-a-local-password'
$env:APP_DEMO_ADMIN_PASSWORD = 'replace-with-another-local-password'
$env:APP_DEMO_USER_PASSWORD = 'replace-with-a-user-local-password'
$env:APP_DEMO_DISABLED_PASSWORD = 'replace-with-a-third-local-password'
$env:APP_SSO_SHARED_SECRET = 'replace-with-at-least-32-random-bytes'
$env:APP_SSO_EXPECTED_ISSUER = 'portfolio-demo-idp'
$env:APP_SSO_EXPECTED_AUDIENCE = 'spring-security-auth-bridge'
$env:APP_SSO_ACTIVE_KEY_ID = 'demo-key-v1'
.\mvnw.cmd spring-boot:run
```

비밀번호는 UTF-8 기준 각 12~72바이트, SSO 비밀은 32바이트 이상이어야 합니다. 이 값들은 로컬 데모용이며 커밋하지 않습니다.

## DB 로그인 확인

먼저 CSRF token과 세션을 같은 `WebRequestSession`에 준비합니다.

```powershell
$webSession = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$csrf = Invoke-RestMethod -Uri 'http://localhost:8080/auth/csrf' -WebSession $webSession
$headers = @{ 'X-XSRF-TOKEN' = $csrf.token }
$body = @{
  username = 'analyst'
  password = $env:APP_DEMO_ANALYST_PASSWORD
} | ConvertTo-Json

Invoke-RestMethod -Method Post `
  -Uri 'http://localhost:8080/auth/db' `
  -WebSession $webSession `
  -Headers $headers `
  -ContentType 'application/json' `
  -Body $body

Invoke-RestMethod -Uri 'http://localhost:8080/api/reports/monthly' -WebSession $webSession
```

인증 성공 시 기존 CSRF token과 `XSRF-TOKEN` cookie는 폐기됩니다. 로그인 이후 `POST` 요청을 보낼 때는 같은 `WebRequestSession`으로 `/auth/csrf`를 다시 호출해 새 token과 cookie를 사용합니다. 이전 token을 다시 보내면 `403 INVALID_CSRF`입니다.

`analyst` 세션으로 `/api/admin/reindex`를 호출하면 CSRF token이 맞더라도 `403 ACCESS_DENIED`가 반환됩니다.

## SSO 수동 확인

SSO assertion은 신뢰된 IdP가 서명한다고 가정합니다. 샘플은 서버 내부에 서명용 HTTP endpoint를 만들지 않습니다. canonical text와 HMAC 형식은 [`ARCHITECTURE.md`](ARCHITECTURE.md)에 문서화되어 있고, 생성·검증 예시는 `AuthenticationFlowTest`와 `HmacSsoAssertionVerifierTest`에서 확인할 수 있습니다.

운영 설계에서는 이 HMAC adapter 대신 OIDC 또는 SAML 검증기를 사용하고, 비밀은 secret manager에서 공급해야 합니다. 동일한 secret을 여러 relying party가 공유하면 안 됩니다. 샘플은 active key 하나만 지원하므로 실제 key 교체에는 이전·신규 key 중첩 검증과 폐기 기한 관리가 추가로 필요합니다.

## 설정 실패 확인

`APP_SSO_SHARED_SECRET`을 제거한 뒤 애플리케이션을 다시 실행하면 DB 로그인은 유지되지만 `/auth/sso`는 `503 SSO_ADAPTER_UNAVAILABLE`을 반환합니다. 약한 내장 비밀이나 DB 로그인으로 자동 우회하지 않습니다.
