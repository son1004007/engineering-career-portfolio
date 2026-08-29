# Architecture

## 목표

월 단위 문자열 컬럼(`YYYY`, `MM`)을 가진 업무 조회를 합성 도메인으로 재현합니다. 핵심은 **정합성을 먼저 고정한 뒤 index-friendly predicate를 유지하는 것**입니다.

## 구조

```text
ActivityQueryService
  -> YearMonthRange validation / normalization
  -> ActivitySnapshotMapper
       -> same year: one range branch
       -> cross year: start + middle + end UNION ALL branches
       -> shared rangeRows fragment
            |-> count
            `-> page + deterministic ORDER BY
  -> H2 synthetic schema/data
```

## 범위 분해

### 같은 연도

```text
snapshot_year = startYear
AND snapshot_month BETWEEN startMonth AND endMonth
```

### 여러 연도

세 집합은 서로 겹치지 않습니다.

```text
1. snapshot_year = startYear AND snapshot_month >= startMonth
2. snapshot_year > startYear AND snapshot_year < endYear
3. snapshot_year = endYear AND snapshot_month <= endMonth
```

이를 `UNION ALL`로 결합합니다. 중복 제거 연산에 의존하지 않고, 구간 자체를 상호 배타적으로 설계합니다.

## 인덱스와 입력 정규화

합성 스키마의 인덱스는 다음 순서입니다.

```text
(tenant_id, snapshot_year, snapshot_month)
```

월은 애플리케이션에서 항상 두 자리 문자열로 정규화합니다. 따라서 mapper는 indexed column에 숫자 변환 함수를 적용할 필요가 없습니다.

이 설계는 **인덱스를 사용할 수 있는 형태를 보존**하려는 것이며, H2 테스트가 Oracle 운영 실행계획이나 성능 향상을 증명한다는 뜻은 아닙니다.

## count/page 정합성

MyBatis `<sql id="rangeRows">`를 count와 page가 공유합니다. 필터 로직을 두 곳에서 따로 유지해 total count와 실제 목록의 조건이 벌어지는 회귀를 줄입니다.

## 결정적 pagination

동일 연월에 복수 row가 있을 수 있으므로 정렬 마지막에 `id ASC`를 둡니다.

```text
snapshot_year DESC,
snapshot_month DESC,
id ASC
```

이 tie-breaker가 없으면 같은 조건의 반복 조회에서 page 경계가 불안정해질 수 있습니다.

## 공개 안전성

회사 원본의 SQL, schema, 데이터와 식별자를 사용하지 않습니다. 공개 코드는 `activity_snapshot`, `alpha/beta` tenant와 임의 점수 등 합성 fixture만 포함합니다.
