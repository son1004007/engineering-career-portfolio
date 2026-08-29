---
title: WAR 기반 Spring 서비스의 배포 이식성
 description: context path와 환경 차이로 발생하는 배포 결함을 profile, health와 rollback 관점에서 정리한 비식별 사례
permalink: /cases/war-deployment-portability/
status: sample-implemented
---

# WAR 기반 Spring 서비스의 배포 이식성

## 문제

개발 환경의 루트 경로에서 동작하던 Spring 화면이 외부 Tomcat의 context path 아래에서는 redirect, 정적 자원 또는 health 확인 때문에 실패할 수 있습니다. 환경값이 WAR 안에 고정되면 동일 산출물을 여러 환경에 배포하기도 어렵고, 파일 교체 자체를 배포 성공으로 간주하면 기동 실패를 늦게 발견하게 됩니다.

이 사례의 핵심은 **WAR 파일을 만드는 것 자체가 아니라 애플리케이션과 Servlet Container, 환경 설정, 배포 검증의 책임 경계를 명확히 하는 것**입니다.

## 담당 범위와 공개 경계

권한 있는 비공개 Spring 업무 원본에서 본인 귀속 범위를 다시 확인했습니다. 공개 가능한 수준에서 확인된 작업은 다음과 같습니다.

- WAR deploy workflow와 배포 runbook 보완
- 여러 업무 화면의 context-path 하드코딩 보정
- profile 기본 활성화 제거와 환경별 설정 경계 정리
- DB 비밀정보를 패키지 설정에서 외부 주입 경계로 이동
- 배포 후 검증·복구 절차 문서화

공개 저장소에는 회사 WAR, JSP 원문, workflow, 서버 주소, 인증서, 배포 경로와 인증정보를 복사하지 않았습니다. 또한 기존 서비스의 최초 WAR 패키징이나 전체 인프라를 본인이 단독 설계했다는 주장도 하지 않습니다.

같은 문제를 [독립 합성 샘플](../../02_projects/case-study-samples/war-deployment-portability/README.md)로 새로 구현했습니다.

## 설계 1. 외부 Servlet Container 경계를 코드로 명시한다

공개 샘플은 Java 21 / Spring Boot 3.5.16을 사용하고 Maven artifact를 WAR로 고정합니다.

```text
<packaging>war</packaging>
spring-boot-starter-tomcat -> provided
ServletInitializer -> SpringBootServletInitializer
```

내장 서버 실행만 가능한 구조가 아니라 외부 Servlet Container가 application source를 bootstrap할 수 있는 entry point를 둡니다.

## 설계 2. context path를 문자열 상수가 아닌 runtime 값으로 취급한다

`/demo` 같은 경로를 애플리케이션 코드에 고정하면 WAR 파일명이나 외부 Tomcat 설정이 달라지는 순간 링크가 깨집니다.

공개 샘플의 `/entry`는 현재 `HttpServletRequest`의 context path를 읽어 health 링크를 만듭니다.

```text
runtime context path = /demo
entry link          = /demo/healthz
```

통합 테스트는 `/demo/entry`, `/demo/healthz`가 정상이고 루트 `/healthz`는 우연히 노출되지 않는지 확인합니다.

## 설계 3. deploy profile의 필수 값은 외부에서 주입하고 없으면 실패한다

저장소에는 실제 runtime token을 넣지 않습니다.

```text
application-deploy.properties
  portfolio.runtime-token=${PORTFOLIO_RUNTIME_TOKEN:}
```

`deploy` profile에서 값이 비어 있으면 `DeployRuntimeGuard`가 application context 생성을 거부합니다. 테스트에는 합성 값만 주입하며 endpoint나 로그에 token 내용을 노출하지 않습니다.

## 설계 4. 배포 성공을 artifact 교체가 아니라 health gate로 정의한다

`deploy/release-war.sh`의 경계는 다음과 같습니다.

```text
candidate WAR
  -> previous WAR backup
  -> staged replacement
  -> health check
       | PASS -> 새 artifact 유지
       ` FAIL -> 이전 WAR 복구
```

unsafe application name은 파일 교체 전에 거부합니다. 이 스크립트는 실제 조직의 배포 script를 복사한 것이 아니라 backup/replace/health/rollback 원칙만 합성 디렉터리에서 재현한 것입니다.

## 검증 계획

공개 샘플에는 다음 자동 검증이 포함되어 있습니다.

- WAR packaging과 provided Tomcat 구조
- 외부 container용 `ServletInitializer`
- non-root context path에서 entry/health 동작
- root hardcoding 부재
- deploy profile의 외부 runtime 값 fail-closed
- 정상 health 시 candidate WAR 유지와 previous WAR backup
- health 실패 시 previous WAR rollback
- unsafe application name 거부

GitHub Actions `Verify Portfolio` Java matrix에서 `./mvnw -q clean verify`를 실행하도록 연결했습니다. 현재 branch 구현 단계이므로 CI 성공 전에는 `sample-verified`나 `published`로 올리지 않습니다.

## trade-off

실행형 JAR는 프로세스와 런타임을 애플리케이션 팀이 직접 통제하기 쉬운 반면, 조직 표준이 외부 Tomcat이면 connector, 인증서, 배포 디렉터리와 같은 기존 운영 경계에 맞춰야 할 수 있습니다. WAR 방식에서는 context path와 container lifecycle이 별도 변수로 생기므로 테스트와 runbook이 더 중요합니다.

또한 단일 WAR 교체 후 health rollback은 rolling deployment나 session drain을 대체하지 않습니다. 무중단 요구가 있다면 reverse proxy, 이중 인스턴스, readiness와 traffic switching 같은 별도 구조가 필요합니다.

## 확인하지 않은 것

이 공개 샘플만으로 다음을 주장하지 않습니다.

- 실제 외부 Tomcat 버전별 호환성 전체
- 운영 서버의 zero-downtime deployment
- session drain 또는 cluster rolling update
- 실제 인증서/TLS/SSO 통합 성공
- 운영 트래픽 규모와 SLA

이 사례가 목표로 하는 증거 범위는 **본인 귀속 배포·경로·환경 설정 개선 원칙을 비식별화하고, 회사 원본과 독립된 WAR/context-path/profile/health/rollback 샘플로 재검증하는 것**입니다.
