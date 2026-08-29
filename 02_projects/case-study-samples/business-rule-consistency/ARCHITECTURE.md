# Architecture

## 목표

화면별 임시 조건을 Controller와 Mapper에 반복하지 않고, 인증 주체와 기준 시점 정책을 각각 하나의 책임으로 고정합니다.

```text
HttpSession
  -> SessionSubjectResolver
       canonical subject
       -> legacy fallback only when absent
       -> missing => 401
  -> MemberSnapshotController
       endpoint policy only
  -> MemberSnapshotService
       LATEST_ONLY | EXPLICIT_OR_LATEST
       -> resolved SnapshotKey
  -> MemberSnapshotMapper
       explicit subjectId + SnapshotKey
```

## 설계 결정

### 1. 인증 주체는 요청 파라미터가 아니라 session에서 결정

`SessionSubjectResolver`는 `subjectId`를 우선하고, 없을 때만 `legacyLoginId`를 사용합니다. URL이나 form의 subject 파라미터는 identity source가 아닙니다.

### 2. 최신값 정책은 Service가 소유

`LATEST_ONLY`는 호출자가 year/month를 전달해도 사용하지 않습니다. 화면에 기간 선택 기능이 없는 경우, 과거 요청 파라미터가 우연히 남아 있어 결과를 바꾸는 회귀를 차단합니다.

`EXPLICIT_OR_LATEST`는 두 값이 모두 있을 때만 명시 기간을 허용하며 한쪽만 있으면 `400`입니다.

### 3. Mapper는 이미 해석이 끝난 값만 받음

Mapper는 "최신인지", "fallback 가능한지", "어떤 사용자인지"를 다시 해석하지 않습니다. `subjectId`와 `SnapshotKey`가 확정된 뒤 조회만 수행합니다.

### 4. 데이터 부재는 조용히 다른 기준으로 우회하지 않음

최신 snapshot이나 요청 snapshot이 없으면 `404`로 종료합니다. null을 다음 계층으로 흘려 NPE를 만들거나 임의 기간으로 대체하지 않습니다.

## 공개 경계

이 샘플은 회사 원본의 도메인, 클래스명, 식별자, SQL, schema, 실제 데이터와 URL을 사용하지 않습니다. 원본에서 확인한 것은 "계층별 규칙 불일치를 정합화한 개인 기여 범위"이며, 공개 코드는 그 설계 원칙만 새 합성 도메인으로 재구현합니다.
