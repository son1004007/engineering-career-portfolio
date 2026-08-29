# Architecture

## Runtime boundary

```text
external servlet container
        |
        | context path: container/runtime choice
        v
Spring Boot WAR
  |- ServletInitializer
  |- /entry
  |- /healthz
  `- deploy profile guard
```

애플리케이션은 특정 WAR 파일명이나 `/` context path를 전제로 URL을 만들지 않습니다. `DeploymentInfoController`는 현재 request의 context path를 사용해 링크를 구성합니다.

## Configuration boundary

```text
repository configuration
  application.properties
  application-deploy.properties
        |
        | PORTFOLIO_RUNTIME_TOKEN
        v
DeployRuntimeGuard
```

`deploy` profile의 필수 런타임 값은 환경에서 주입합니다. 값이 비어 있으면 application context 생성 단계에서 실패합니다. 샘플 endpoint는 해당 값 자체를 반환하거나 기록하지 않습니다.

## Release boundary

```text
candidate WAR
   |
   v
stage -> previous backup -> active WAR
                         |
                         v
                    health check
                    /          \
                  PASS          FAIL
                   |             |
                retain        rollback
```

`release-war.sh`는 파일 교체와 health/rollback 경계만 재현합니다. 실제 Tomcat reload 방식, session drain, reverse proxy, cluster rolling deployment는 환경별 책임으로 남겨 둡니다.

## 설계 이유

- WAR artifact와 런타임 설정을 분리해 빌드 산출물이 환경별 비밀값을 포함하지 않게 합니다.
- context path를 runtime concern으로 취급해 개발·검증·운영 경로 차이로 인한 하드코딩 결함을 줄입니다.
- artifact 교체 성공이 아니라 health gate 통과를 배포 성공 조건으로 둡니다.
- health 실패 시 이전 artifact를 복구해 실패 상태를 그대로 확정하지 않습니다.
