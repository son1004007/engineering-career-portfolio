---
title: 실무 사례
description: 실제 업무에서 다룬 문제와 독립 구현 프로젝트를 공개 가능한 범위로 정리한 엔지니어링 사례 모음
permalink: /cases/
---

# 실무 사례

실제 업무에서 다룬 문제와 설계 판단을 회사 정보 없이 일반화해 정리했습니다. 공개 코드가 있는 사례는 합성 데이터로 독립 구현하고 테스트 결과를 함께 기록했습니다.

## Java/Spring

- [Spring Security 인증 통합](spring-security-auth-bridge.md) — 독립 구현과 자동화 테스트 24개 완료
- [MyBatis 조회 정합성과 실행계획](mybatis-query-correctness.md) — 담당 경험과 설계 판단 정리
- [WAR 배포 이식성](war-deployment-portability.md) — 담당 경험과 설계 판단 정리
- [분산된 업무 규칙 정합화](business-rule-consistency.md) — 담당 경험과 설계 판단 정리
- [통계 품질 분석 화면](statistical-analysis-ui.md) — 담당 경험과 설계 판단 정리

## AI 응용

- [Text2SQL 검증과 실패 분류](text2sql-validation.md) — 담당 구현과 구성요소 검증 범위 정리
- [Agent Runtime 작업 격리와 산출물 추적](agent-runtime-artifact-provenance.md) — 담당 구현과 구성요소 검증 범위 정리

각 사례의 공개 범위와 후속 구현 계획은 [사례 인덱스](../case-study-index.md)에서 확인할 수 있습니다.
