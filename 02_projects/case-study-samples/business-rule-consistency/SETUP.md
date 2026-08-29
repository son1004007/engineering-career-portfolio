# Setup

## 요구사항

- Java 21
- Maven 3.9.9 또는 저장소의 `mvnw`

## 테스트

```bash
chmod +x mvnw
./mvnw -q clean verify
```

별도 DB, 외부 API, 인증 서버 또는 비밀정보는 필요하지 않습니다. 실행 데이터는 코드 내부의 합성 fixture만 사용합니다.

## 선택적 로컬 실행

```bash
./mvnw spring-boot:run
```

샘플 endpoint는 session에 `subjectId` 또는 fallback용 `legacyLoginId`가 존재한다는 전제로 동작합니다. 공개 검증은 실제 로그인 UI가 아니라 MockMvc session fixture로 수행합니다.
