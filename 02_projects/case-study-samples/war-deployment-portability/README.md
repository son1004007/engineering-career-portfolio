# WAR Deployment Portability

회사 원본 코드·배포 경로·호스트·인증정보를 복사하지 않고, Spring Boot WAR 서비스를 별도 Servlet Container에 배포할 때 필요한 경계를 합성 샘플로 재현합니다.

## 재현하는 문제

- 실행 가능한 JAR 전제와 외부 Tomcat WAR 전제를 분리합니다.
- 루트(`/`)가 아닌 context path에서도 링크와 health endpoint가 올바르게 동작해야 합니다.
- deploy profile의 런타임 값은 저장소에 넣지 않고 외부에서 주입하며, 필수 값이 없으면 fail-closed 합니다.
- 새 WAR 교체 뒤 health gate가 실패하면 이전 artifact로 복구합니다.

## 구현

- Java 21 / Spring Boot 3.5.16
- `<packaging>war</packaging>` + `spring-boot-starter-tomcat` `provided`
- `SpringBootServletInitializer` 기반 외부 Servlet Container bootstrap
- request의 실제 context path를 사용하는 `/entry` 링크
- `/healthz` bounded readiness marker
- `deploy` profile에서 외부 `PORTFOLIO_RUNTIME_TOKEN` 미주입 시 기동 거부
- `deploy/release-war.sh`: previous WAR backup -> staged replace -> health check -> 실패 시 rollback

## 검증

```bash
chmod +x mvnw
./mvnw clean verify
```

`2026-08-29` GitHub Actions `Verify Portfolio` run `33252018737`의 `WAR deployment portability` job이 성공했습니다. 총 10개 자동 테스트가 다음 경계를 검증합니다.

- `/demo` non-root context-path entry/health와 root hardcoding 부재
- deploy profile 외부 runtime 값 fail-closed / synthetic 값 주입 성공
- WAR packaging, provided Tomcat, external-container initializer
- 정상 health 시 candidate WAR 유지와 previous WAR backup
- health 실패 시 previous WAR rollback
- unsafe app-name 거부

CI 관측 환경의 context-path 통합 테스트는 Java `21.0.12`, Spring Boot `3.5.16`, embedded Tomcat `10.1.55`에서 실행됐습니다. 이는 외부 운영 Tomcat 검증과 구분합니다.

## 공개 경계

이 샘플은 실제 회사 WAR, JSP, 설정 파일, 서버 주소, 인증정보, 배포 workflow를 포함하지 않습니다. 실제 외부 Tomcat 인스턴스의 무중단 교체, 운영 트래픽, SLA, 특정 WAS 설정 호환성을 검증했다는 의미도 아닙니다.

관련 문서:

- [ARCHITECTURE.md](ARCHITECTURE.md)
- [SETUP.md](SETUP.md)
- [VERIFICATION.md](VERIFICATION.md)
