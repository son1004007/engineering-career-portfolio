# Business-rule consistency sample

독립적인 Java 21 / Spring Boot 3.5.16 합성 샘플입니다. 비공개 업무 코드를 복사하지 않고, 여러 계층에 흩어진 사용자 식별 규칙과 기준 시점 정책을 한 흐름으로 정합화하는 방법만 재현합니다.

## 검증하는 경계

- session의 canonical subject가 legacy login 값보다 우선합니다.
- canonical subject가 없을 때만 legacy 값으로 제한적으로 fallback합니다.
- 둘 다 없으면 `401`로 fail-closed 합니다.
- `LATEST_ONLY` 화면은 요청 year/month가 있어도 최신 snapshot을 사용합니다.
- `EXPLICIT_OR_LATEST` 화면은 year/month를 함께 받았을 때만 명시 snapshot을 사용합니다.
- 최신 snapshot 또는 명시 snapshot이 없으면 임의 기본값으로 진행하지 않고 `404`로 실패합니다.
- Mapper는 Service가 확정한 `subjectId + SnapshotKey`만 전달받습니다.
- URL의 임의 `subjectId` 파라미터는 인증 session identity를 덮어쓸 수 없습니다.

## 실행

```bash
chmod +x mvnw
./mvnw -q clean verify
```

현재 데이터와 이름은 모두 합성 fixture입니다. 실제 업무명, 학생 식별자, SQL, schema, Mapper 원문, 운영 데이터와 내부 URL은 포함하지 않습니다.

상세 설계와 한계는 [`ARCHITECTURE.md`](ARCHITECTURE.md), 검증 항목은 [`VERIFICATION.md`](VERIFICATION.md)를 참고합니다.
