# MyBatis Query Correctness Sample

회사 코드와 독립된 합성 Java/Spring/MyBatis 샘플입니다. 복수 연도에 걸친 월 단위 조회에서 결과 정합성을 보존하면서 indexed column을 변환 함수로 감싸지 않는 범위 조건을 재현합니다.

## 보여주는 것

- `YearMonthRange` 입력 검증과 `YYYY`/`MM` 정규화
- 같은 연도는 하나의 `BETWEEN` 범위로 조회
- 여러 연도는 서로 겹치지 않는 시작 연도 / 중간 연도 / 종료 연도 세 구간을 `UNION ALL`로 결합
- `tenant_id, snapshot_year, snapshot_month` 복합 인덱스
- count와 page가 동일한 MyBatis SQL fragment를 공유
- `ORDER BY snapshot_year DESC, snapshot_month DESC, id ASC`의 결정적 pagination
- H2 합성 데이터 기반 정상·실패·경계 회귀 테스트
- MyBatis `BoundSql`을 검사해 indexed year/month column에 `TO_NUMBER` 같은 변환을 적용하지 않고 cross-year 쿼리가 두 개의 `UNION ALL`을 사용하는지 검증

## 공개 경계

이 샘플은 권한 있는 비공개 원본에서 확인한 **기간 조건 분해와 indexed column 변환 제거라는 설계 원리**만 새로 구현했습니다.

포함하지 않는 것:

- 회사 SQL, 테이블/컬럼명, 고객·서비스 식별자 또는 데이터
- 회사 Oracle 실행계획 원문
- 운영 환경 성능 수치
- 공개 샘플의 H2 결과를 Oracle 운영 성능으로 일반화한 주장

## 실행

```bash
chmod +x mvnw
./mvnw clean verify
```

Java 21을 사용합니다. 테스트는 외부 DB 없이 in-memory H2에서 실행됩니다.

상세 설계는 [ARCHITECTURE.md](ARCHITECTURE.md), 재현 방법은 [SETUP.md](SETUP.md), 검증 범위는 [VERIFICATION.md](VERIFICATION.md)를 참고합니다.
