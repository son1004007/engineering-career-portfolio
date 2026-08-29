---
title: 분산된 업무 규칙을 한 흐름으로 정합화하기
description: Controller, Service와 Mapper에 흩어진 기준값·사용자 식별 규칙을 회귀 가능한 구조로 정리한 비식별 사례
permalink: /cases/business-rule-consistency/
status: sample-verified
---

# 분산된 업무 규칙을 한 흐름으로 정합화하기

## 문제

같은 의미의 사용자와 기준 시점을 화면, Controller, Service와 Mapper가 서로 다르게 해석하면 특정 화면에서만 과거 기준이 섞이거나 다른 사용자의 조회 경로가 선택되는 회귀가 생길 수 있습니다. 한 화면의 결함처럼 보여도 실제 원인은 요청부터 데이터 조회까지 이어지는 업무 규칙의 소유자가 불분명한 데 있습니다.

## 담당한 부분과 원본 확인

권한 있는 비공개 Java/Spring 원본에서 본인 author/committer 변경을 다시 확인했습니다. 공개 가능한 수준에서 확인된 개인 기여 범위는 다음과 같습니다.

- session에 존재하는 canonical 사용자 식별자를 우선하고 legacy 로그인 값은 제한적으로 fallback
- 기간 선택 기능이 없는 사용자 화면은 전달된 year/month와 무관하게 최신 기준을 사용하도록 정합화
- 사용자 전용 조회가 일반 조회 Mapper 경로를 우회하지 않도록 전용 Mapper 경계로 통일
- 최신 기준 데이터가 없을 때 null이 후속 로직으로 흘러 NPE가 되지 않도록 원인 식별 가능한 실패 경계 추가
- Controller와 Service에서 같은 사용자·기준 시점 규칙을 사용하도록 흐름 보정

회사 클래스명, endpoint, field, SQL, schema, 테스트 계정, 실제 데이터와 내부 식별자는 공개 저장소에 복사하지 않습니다. 기존 서비스 전체의 업무 규칙을 단독 설계했다는 주장도 하지 않습니다.

같은 문제를 [독립 합성 샘플](../../02_projects/case-study-samples/business-rule-consistency/README.md)로 새로 구현했습니다.

## 설계 1. 인증 주체와 요청 데이터의 책임을 분리한다

공개 샘플은 session의 `subjectId`를 canonical identity로 사용하고, 없을 때만 `legacyLoginId`를 fallback으로 사용합니다. URL의 임의 `subjectId`는 인증 주체를 바꿀 수 없습니다.

```text
HttpSession
  subjectId      -> canonical
  legacyLoginId  -> fallback only when canonical is absent
  neither        -> 401 fail-closed
```

사용자 식별 규칙을 각 Controller에서 반복하지 않고 `SessionSubjectResolver` 하나에 고정했습니다.

## 설계 2. 화면의 기준 시점 정책은 Service가 소유한다

기간 선택 기능이 없는 화면은 호출자가 과거 year/month를 보내더라도 최신 snapshot을 사용해야 합니다. 반대로 기간 조회가 허용된 화면은 year/month를 둘 다 제공했을 때만 명시 기간을 사용합니다.

```text
LATEST_ONLY
  request year/month -> ignored
  -> latest snapshot

EXPLICIT_OR_LATEST
  year + month -> explicit snapshot
  neither      -> latest snapshot
  only one     -> 400
```

이 규칙을 Controller별 조건문이 아니라 `MemberSnapshotService`의 policy로 모았습니다.

## 설계 3. Mapper에는 해석이 끝난 값만 전달한다

Mapper가 사용자 fallback, 최신값 결정, 화면별 기본값을 다시 판단하면 계층마다 규칙이 갈라질 수 있습니다. 공개 샘플의 Mapper 계약은 다음 두 가지로 제한합니다.

```text
findLatestSnapshot(subjectId)
findBySubjectAndSnapshot(subjectId, SnapshotKey)
```

즉, Service가 사용자와 기준 시점을 확정한 뒤 Mapper는 조회만 수행합니다.

## 설계 4. 기준 데이터 부재를 null 전파가 아니라 명시적 실패로 처리한다

최신 snapshot이 없거나 명시한 snapshot에 데이터가 없으면 임의 다른 기간으로 넘어가지 않습니다. 공개 샘플은 `404 Snapshot unavailable`로 종료합니다. 인증 주체가 없으면 `401`, 불완전하거나 잘못된 기간은 `400`입니다.

## 자동 회귀 검증

합성 샘플의 11개 자동 테스트가 PR run `33275860098`의 `Business-rule consistency` job에서 `./mvnw -q clean verify`로 PASS했습니다. 같은 run의 Jekyll, 공개 텍스트 검사, 기존 Spring Security/MyBatis/WAR/OpsMate와 container/runbook을 포함한 **전체 8개 job도 모두 PASS**했습니다.

검증 범위:

- canonical session identity 우선
- canonical 부재 시 legacy fallback
- identity 부재 `401`
- latest-only 화면에서 과거 year/month 무시
- 명시 snapshot 조회
- 기간 미지정 시 latest fallback
- year/month 부분 입력 거부
- 잘못된 month 거부
- 존재하지 않는 snapshot `404`
- 요청 parameter로 session subject를 덮어쓸 수 없음
- latest snapshot이 없는 subject `404`

현재 상태는 `sample-verified`입니다. main merge와 GitHub Pages build/deploy가 성공한 뒤에만 `published`로 올립니다.

## 대안과 trade-off

각 화면을 개별 수정하면 변경량은 적지만 동일 규칙이 다시 갈라집니다. 공통 유틸리티만 두는 방법도 호출자가 우회하면 효과가 없습니다. Resolver와 Service policy를 명시하면 구조가 한 단계 늘어나지만 사용자 식별과 기준 시점의 책임이 테스트 가능한 위치로 고정됩니다.

legacy fallback 자체도 영구 구조로 권장되는 것은 아닙니다. 마이그레이션 기간에는 호환성을 제공하지만, 장기적으로는 canonical identity만 사용하도록 호출 경계를 정리하는 편이 더 단순합니다.

## 확인하지 않은 것

이 공개 샘플만으로 다음을 주장하지 않습니다.

- 실제 회사 시스템의 SSO/session 전체 동작
- 실제 Mapper SQL과 운영 DB 결과
- 운영 데이터 정합성 전체
- 조직 전체의 업무 규칙 설계 책임
- 운영 트래픽 규모·성능·SLA

이 사례가 증명하려는 범위는 **본인 귀속으로 확인된 사용자 식별·기준 시점·조회 경로 정합화 원칙을 회사 원본과 독립된 Spring 샘플과 회귀 테스트로 재현하는 것**입니다.
