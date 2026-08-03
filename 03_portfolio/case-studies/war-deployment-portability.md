---
title: WAR 기반 Spring 서비스의 배포 이식성
description: context path와 환경 차이로 발생하는 배포 결함을 profile, health와 rollback 관점에서 정리한 비식별 사례
permalink: /cases/war-deployment-portability/
status: source-reviewed
---

# WAR 기반 Spring 서비스의 배포 이식성

> 상태: `source-reviewed` · 공개 Tomcat 재현 샘플은 아직 작성 전

## 확인된 범위

비공개 Spring 기반 서비스에서 WAR 배포, 환경 설정과 경로 처리 관련 본인 기여를 확인했습니다. 서버 주소, 인증서, 내부 배포 경로와 workflow 원문은 공개하지 않습니다.

## 문제

개발 환경의 루트 경로에서 동작하던 화면이 외부 Tomcat의 context path 아래에서는 redirect, 정적 자원 또는 health 확인 때문에 실패할 수 있습니다. 환경값이 패키지 안에 고정되면 동일 산출물을 여러 환경에 배포하기도 어렵습니다.

## 설계 판단

- 애플리케이션 URL은 문자열 결합이 아니라 context-aware API로 생성합니다.
- 환경별 차이는 profile과 외부 설정으로 분리하고 WAR 자체는 동일하게 유지합니다.
- 배포 성공은 파일 복사가 아니라 health 확인까지로 정의합니다.
- 실패 시 이전 산출물로 되돌릴 수 있게 backup, 검증과 rollback 단계를 분리합니다.

## 대안과 trade-off

실행형 JAR는 단순하지만 조직의 외부 Tomcat 운영 제약과 맞지 않을 수 있습니다. 반대로 WAR는 컨테이너 설정과 애플리케이션 책임 경계가 늘어나므로 경로·세션·인증서 검수표가 필요합니다.

## 공개 재현 계획

합성 서비스 하나를 루트와 `/portfolio-demo` context path에서 각각 실행하고 redirect, 정적 자원, profile 주입, health 실패와 rollback을 자동 검증합니다.

## 한계

실제 인프라와 배포 결과는 공개하지 않습니다. 독립 컨테이너 재현과 테스트가 완료되기 전 상태는 `source-reviewed`입니다.
