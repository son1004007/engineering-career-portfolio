---
title: 실무 사례
description: 비식별 원본 검토와 공개 재현 상태를 분리한 엔지니어링 사례 모음
permalink: /cases/
---

# 실무 사례

회사 업무는 원본 코드를 공개하지 않습니다. 각 글은 본인 귀속을 확인한 범위만 비식별화하며, 공개 코드는 합성 데이터로 독립 구현합니다.

## Java/Spring

- [Spring Security 인증 통합](spring-security-auth-bridge.md) — `sample-verified`, 공개 샘플 24개 테스트 성공
- [MyBatis 조회 정합성과 성능](mybatis-query-correctness.md) — `source-reviewed`
- [WAR 배포 이식성](war-deployment-portability.md) — `source-reviewed`
- [분산된 업무 규칙 정합화](business-rule-consistency.md) — `source-reviewed`
- [통계 품질 분석 화면](statistical-analysis-ui.md) — `source-reviewed`

## AI 응용

- [Text2SQL 검증과 실패 분류](text2sql-validation.md) — `source-reviewed`, `tested-component`
- [Agent Runtime 작업 격리와 산출물 추적](agent-runtime-artifact-provenance.md) — `source-reviewed`, `tested-component`

상태 정의와 전체 후보는 [사례 인덱스](../case-study-index.md)에서 확인할 수 있습니다.
