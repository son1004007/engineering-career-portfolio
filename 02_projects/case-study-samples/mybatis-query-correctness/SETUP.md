# Setup

## 요구사항

- JDK 21
- `sh`, `curl` 또는 `wget`, `unzip` 중 Maven Wrapper 실행에 필요한 기본 도구

외부 Oracle이나 별도 DB 서버는 필요하지 않습니다. 애플리케이션은 테스트용 in-memory H2를 사용합니다.

## 검증

```bash
chmod +x mvnw
./mvnw clean verify
```

테스트가 실행되면 `schema.sql`과 `data.sql`이 새 H2 메모리 DB에 적용됩니다.

## 데이터 모델

합성 테이블:

```text
activity_snapshot
- id
- tenant_id
- snapshot_year
- snapshot_month
- item_key
- status
- score
```

복합 인덱스:

```text
idx_activity_range(tenant_id, snapshot_year, snapshot_month)
```

fixture는 `alpha`, `beta`라는 임의 tenant와 2024~2026년의 합성 월별 row만 사용합니다.

## Oracle 관련 범위

H2는 `MODE=Oracle`로 SQL 문법 호환 범위를 일부 높이지만 Oracle optimizer를 재현하지 않습니다. 따라서 이 샘플의 테스트 결과를 실제 Oracle 실행계획이나 운영 성능 수치로 해석하지 않습니다.
