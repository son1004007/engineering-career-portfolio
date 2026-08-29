# Verification

이 문서는 공개 샘플이 검증하는 범위와 검증하지 않는 범위를 구분합니다.

## 자동 검증 gate

`./mvnw clean verify`에서 다음을 확인합니다.

1. 같은 연도 범위
   - 시작·종료 월 포함
   - 범위 밖 row 제외
2. 여러 연도 범위
   - 시작 연도 tail + 중간 연도 + 종료 연도 head 포함
   - 누락·중복 없음
3. tenant isolation
   - 같은 연월의 다른 tenant row 제외
4. count/page 정합성
   - page가 달라도 동일 filter의 total 유지
5. 결정적 pagination
   - 같은 연월 tie에 `id`를 사용해 반복 조회 순서 고정
6. 입력 실패 경계
   - 역전된 기간, 잘못된 월, 음수 page, 과도한 page size, 빈 tenant 거부
7. schema boundary
   - `(tenant_id, snapshot_year, snapshot_month)` 복합 인덱스 존재 확인
8. MyBatis SQL shape
   - same-year branch는 `UNION ALL` 불필요
   - cross-year branch는 정확히 세 구간(두 개의 `UNION ALL`)
   - indexed year/month column에 `TO_NUMBER` 같은 변환 함수 없음
   - 기간 조건을 복합 `OR`로 되돌리지 않음

## 성공으로 간주하지 않는 것

아래는 별도 증거가 없으므로 이 샘플의 `verified` 범위가 아닙니다.

- 실제 Oracle optimizer가 특정 인덱스를 선택했다는 주장
- 운영 쿼리 elapsed time, CPU, logical read 개선률
- 운영 데이터 규모에서의 throughput 또는 latency
- 회사 시스템의 전체 SQL 품질 또는 성능

## 근거 해석

권한 있는 비공개 원본에서는 본인 귀속 변경으로 기간 조건의 복합 OR을 서로 겹치지 않는 구간으로 분해하고 indexed column의 숫자 변환을 제거한 범위를 재확인했습니다. 공개 저장소는 그 원칙을 회사 코드와 독립된 합성 샘플로만 재현합니다.
