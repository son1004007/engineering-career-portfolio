# Verification

## 자동 검증 명령

```bash
./mvnw -q clean verify
```

## 현재 테스트 항목

1. canonical session subject가 legacy login보다 우선
2. canonical subject가 없을 때만 legacy fallback
3. session identity가 없으면 `401`
4. `LATEST_ONLY` endpoint가 요청된 과거 year/month를 무시
5. explicit snapshot endpoint가 완전한 year/month를 사용
6. explicit snapshot endpoint의 기간 생략 시 최신값 사용
7. year/month 한쪽만 있으면 `400`
8. 잘못된 month는 `400`
9. 존재하지 않는 명시 snapshot은 `404`
10. URL의 `subjectId`가 session identity를 덮어쓸 수 없음
11. 최신 snapshot이 없는 subject는 `404`

## 검증하지 않는 것

- 실제 회사 시스템의 화면·SSO·세션 구현
- 회사 Mapper SQL 또는 운영 DB 결과
- 실제 업무 데이터의 정확도
- 운영 트래픽과 성능
- 조직 전체의 업무 규칙 설계 책임

CI가 성공하기 전에는 이 샘플을 `sample-verified`로 표시하지 않습니다.
