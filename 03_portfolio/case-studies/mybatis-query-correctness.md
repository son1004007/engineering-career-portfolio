---
title: MyBatis 기간 조회의 정합성과 인덱스 친화 조건을 함께 설계하기
description: 복수 연도 월 범위를 누락·중복 없이 조회하면서 indexed column 변환을 피한 Java/Spring·MyBatis 사례
permalink: /cases/mybatis-query-correctness/
status: published
---

# MyBatis 기간 조회의 정합성과 인덱스 친화 조건을 함께 설계하기

## 문제

월 단위 업무 데이터에서 시작 연월과 종료 연월을 한 번에 조회하려면 경계 조건이 복잡해집니다. 특히 여러 연도에 걸친 기간을 하나의 큰 `OR` 식으로 만들거나 검색 컬럼을 숫자 변환 함수로 감싸면, 결과 정합성을 검토하기 어려워지고 인덱스를 사용할 수 있는 SQL 형태도 훼손될 수 있습니다.

이 사례에서 우선순위는 **성능 수치가 아니라 결과 집합의 정확성**입니다. 시작·종료 경계, 중간 연도, tenant 조건과 pagination을 먼저 회귀 테스트로 고정한 뒤 SQL 형태를 단순화했습니다.

## 담당 범위와 공개 경계

권한 있는 비공개 Java/Spring·MyBatis 원본에서 본인 귀속 변경을 다시 확인했습니다. 확인된 핵심은 다음 두 가지입니다.

- 여러 연도 기간 조건의 복합 `OR`을 서로 겹치지 않는 구간으로 분해해 `UNION ALL` 구조로 변경
- 검색·정렬 컬럼에 적용되던 숫자 변환을 제거해 인덱스 사용 가능성을 높이는 형태로 변경

공개 저장소에는 회사 SQL, schema, 고객·서비스 식별자, 실제 데이터와 운영 성능 수치를 복사하지 않았습니다. 대신 같은 설계 문제를 [독립 합성 샘플](../../02_projects/case-study-samples/mybatis-query-correctness/README.md)로 새로 구현했습니다.

## 설계 1. 입력을 먼저 정규화한다

합성 데이터는 `snapshot_year CHAR(4)`, `snapshot_month CHAR(2)` 형태입니다. 애플리케이션의 `YearMonthRange`가 월을 항상 `01`~`12` 두 자리 문자열로 정규화하므로 SQL에서 indexed column을 숫자로 변환할 필요가 없습니다.

```text
입력 2025 / 2
  -> startYear = "2025"
  -> startMonth = "02"
```

역전된 기간이나 잘못된 월은 DB 호출 전에 거부합니다.

## 설계 2. 같은 연도와 여러 연도를 분리한다

같은 연도는 단일 범위입니다.

```sql
snapshot_year = :year
AND snapshot_month BETWEEN :startMonth AND :endMonth
```

여러 연도는 세 개의 상호 배타적인 집합으로 분리합니다.

```text
1. 시작 연도: snapshot_year = startYear AND snapshot_month >= startMonth
2. 중간 연도: snapshot_year > startYear AND snapshot_year < endYear
3. 종료 연도: snapshot_year = endYear AND snapshot_month <= endMonth
```

세 집합은 서로 겹치지 않으므로 `UNION ALL`로 결합해 중복 제거 연산에 정합성을 의존하지 않습니다.

## 설계 3. count와 page가 같은 조건을 공유한다

목록 SQL과 count SQL에 필터를 각각 복사하면 한쪽만 수정됐을 때 `total`과 실제 행이 달라질 수 있습니다. 공개 샘플은 MyBatis의 공통 `rangeRows` SQL fragment를 두 쿼리가 공유합니다.

```text
rangeRows
  |-> SELECT COUNT(*)
  `-> SELECT ... ORDER BY ... OFFSET/FETCH
```

동일 연월에 여러 행이 있을 수 있으므로 정렬 마지막에 `id ASC`를 두어 pagination tie를 결정적으로 만듭니다.

## 합성 schema와 인덱스

```text
idx_activity_range
(tenant_id, snapshot_year, snapshot_month)
```

테스트는 이 인덱스가 실제 H2 schema에 존재하는지 확인하고, MyBatis가 생성한 `BoundSql`에서 indexed year/month column이 `TO_NUMBER(...)` 같은 함수로 감싸지지 않는지 검사합니다.

## 검증

Java 21, Spring Boot 3.5.16, MyBatis Spring Boot Starter 3.0.5와 H2 합성 DB를 사용했습니다.

자동 테스트 12개가 다음을 검증합니다.

- 같은 연도 시작·종료 경계
- 여러 연도 시작/중간/종료 구간의 누락·중복 부재
- tenant isolation
- count/page 조건 일치
- page 간 중복 부재와 반복 조회 순서 안정성
- 잘못된 기간·월·pagination 입력 거부
- 합성 복합 인덱스 존재
- same-year SQL은 불필요한 `UNION ALL`이 없음
- cross-year SQL은 정확히 세 구간으로 구성됨
- indexed year/month column 변환 함수와 기간 `OR` 부재

PR 검증에서 `./mvnw -q clean verify`가 성공했습니다. 이어 main GitHub Pages run `33251362190`의 전체 regression, Jekyll build와 Pages deploy가 성공해 공개 게시 상태까지 확인했습니다. 이 테스트는 회사 시스템을 검증하는 것이 아니라 **독립 공개 샘플의 동작과 SQL 형태**를 검증합니다.

## trade-off

`UNION ALL` 분해가 모든 쿼리에 항상 더 빠르다고 일반화할 수는 없습니다. 데이터 분포, 통계, 인덱스 구성과 DB optimizer에 따라 실제 실행계획은 달라집니다. 또한 조건 분기가 늘어나면 SQL 자체의 길이와 유지보수 비용도 증가합니다.

따라서 실제 시스템에서는 다음 순서를 유지하는 편이 안전합니다.

1. 변경 전후 결과 집합의 동등성 확인
2. 경계값·중복·누락 회귀 검증
3. 실제 DB의 실행계획과 cardinality 확인
4. 운영에 가까운 데이터 규모에서 성능 측정

## 확인하지 않은 것

공개 H2 샘플만으로 다음을 주장하지 않습니다.

- 실제 Oracle optimizer가 특정 인덱스를 선택했다는 것
- 운영 elapsed time, CPU 또는 logical read 개선률
- 전체 업무 SQL의 성능 개선
- 운영 트래픽 규모에서의 latency·throughput

이 사례가 증명하는 범위는 **본인 귀속 설계 변경의 원칙을 비식별화하고, 회사 코드와 독립된 Java/Spring·MyBatis 샘플로 정합성·경계·SQL 형태를 재검증해 공개 Pages까지 게시한 것**입니다.
