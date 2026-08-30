---
title: 실무 문제 해결 사례
description: 실제 업무에서 다룬 문제를 회사 코드 없이 일반화하고 독립 구현과 테스트로 검증한 엔지니어링 사례 모음
permalink: /cases/
---

# 실무 문제 해결 사례

이 페이지는 기술 이름을 나열하기보다 **어떤 문제가 있었고, 어떤 판단으로 해결했는지**를 보여줍니다.

회사 코드와 데이터는 공개하지 않습니다. 공개 코드가 필요한 사례는 합성 데이터와 일반화한 도메인으로 독립 구현하고 테스트 결과를 함께 기록합니다.

## 사용자와 권한

- [사용자 로그인과 권한을 안전하게 통합](spring-security-auth-bridge.md)
  - 서로 다른 인증 경로에서도 사용자와 권한이 잘못 연결되지 않게 만든 사례
  - 공개 독립 샘플 자동 테스트 24개
  - 기술: Spring Security, RBAC, session, CSRF, assertion validation

## 데이터 정합성

- [복잡한 기간 조회에서 데이터 정합성 유지](mybatis-query-correctness.md)
  - 여러 연도와 월을 함께 조회할 때 빠지거나 겹치는 데이터가 없도록 SQL 구조를 검증한 사례
  - 공개 독립 샘플 자동 테스트 12개
  - 기술: Spring Boot, MyBatis, SQL, H2

- [통계 품질 분석 화면](statistical-analysis-ui.md)
  - 데이터 품질 결과를 사용자가 이해할 수 있는 화면으로 연결한 경험을 정리한 source-reviewed 후보

## 배포와 운영

- [환경이 달라도 배포하고 복구할 수 있는 구조](war-deployment-portability.md)
  - 애플리케이션 서버와 context path가 달라도 배포, 상태 확인과 rollback이 가능하도록 재현한 사례
  - 공개 독립 샘플 자동 테스트 10개
  - 기술: Spring Boot WAR, Tomcat, external config, health check, rollback

## 업무 규칙의 일관성

- [사용자 식별과 업무 기준을 여러 계층에서 일관되게 유지](business-rule-consistency.md)
  - 화면, service와 data access에서 사용자 식별과 최신 기준이 다르게 적용되지 않도록 정리한 사례
  - 공개 독립 샘플 자동 테스트 11개
  - 기술: Spring Boot, MockMvc, service/mapper boundary

## AI와 데이터 서비스

- [자연어 질문을 SQL과 데이터 조회로 연결하고 검증](text2sql-validation.md)
  - LLM이 만든 SQL을 그대로 신뢰하지 않고 문법, 실행 가능성, 업무 정답 관점에서 검증한 경험
  - 기술: Python, FastAPI, Text2SQL/NL2SQL, validation set

- [AI 작업 결과가 어디서 만들어졌는지 추적](agent-runtime-artifact-provenance.md)
  - 여러 AI 작업의 결과 파일이 섞이지 않게 분리하고 입력, 작업과 결과를 다시 추적할 수 있도록 만든 경험
  - 기술: workspace isolation, artifact, provenance, manifest

## 읽는 방법

각 사례는 가능한 범위에서 아래 순서를 따릅니다.

```text
문제
-> 왜 중요했는가
-> 제약
-> 판단과 대안
-> 구현
-> 테스트와 검증
-> 결과
-> 한계
-> 사용 기술
```

전문 용어를 없애지는 않습니다. 대신 비개발자가 전문 용어를 알아야만 문제와 결과를 이해하는 구조는 피합니다.

각 사례의 공개 범위와 후속 구현 계획은 [사례 인덱스](../case-study-index.md)에서 확인할 수 있습니다.
