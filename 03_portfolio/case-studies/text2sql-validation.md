---
title: Text2SQL을 다중사용자 백엔드 기능으로 안전하게 실행하기
description: 사용자별 작업 공간, SQL 정책 검증, PostgreSQL 읽기 전용 권한과 결과 기반 평가를 연결한 Python/FastAPI 사례
permalink: /cases/text2sql-validation/
status: sample-verified
---

# Text2SQL을 다중사용자 백엔드 기능으로 안전하게 실행하기

## 문제

LLM이 SQL 문자열을 생성했다는 사실만으로는 여러 사용자가 사용하는 업무 기능이 되지 않습니다.

서비스로 만들려면 적어도 다음 문제를 별도로 해결해야 합니다.

- 다른 사용자의 질문과 결과가 섞이지 않는가
- 모델이 만든 위험한 SQL이 데이터베이스에 도달하지 않는가
- SQL이 실행됐다는 사실과 실제 질문에 맞는 결과라는 사실을 구분하는가
- 애플리케이션 검증이 실패하더라도 DB 권한 자체가 쓰기를 막는가
- 테스트용 데이터베이스에서는 보이지 않던 실제 엔진 차이를 실행 환경에서 확인하는가

## 담당 범위와 공개 재현 원칙

권한 있는 비공개 업무 구현에서 본인이 담당한 Python/FastAPI 기반 Text2SQL/NL2SQL 호출 구조, SQL 검증과 선택적 실행, 결과 기록과 모델 비교 범위를 확인했습니다.

공개 증거는 회사 코드를 옮기지 않고 별도 저장소 [`text2sql-workspace`](https://github.com/son1004007/text2sql-workspace)에 처음부터 독립 구현했습니다.

공개 샘플에는 다음을 사용하지 않습니다.

- 회사 source code
- 실제 DB schema와 SQL
- 업무 질문과 모델 응답
- 고객 또는 내부 식별자
- 내부 endpoint와 credential

대신 합성 commerce 데이터와 synthetic 사용자만 사용합니다.

## 설계 1. 사용자 ID가 아니라 소유권을 서버에서 다시 확인한다

초기 공개 MVP는 두 synthetic 사용자를 사용하지만, 요청에 포함된 workspace/query ID를 권한의 근거로 신뢰하지 않습니다.

```text
Authenticated user
  -> server resolves owned workspace
      -> owned query/history
```

다른 사용자가 유효한 workspace ID를 알고 있더라도 조회할 수 없어야 합니다. 공개 E2E는 사용자 A의 workspace를 사용자 B가 조회했을 때 `404`가 반환되는 경계를 검증합니다.

현재 demo-token endpoint는 이 authorization 흐름을 재현하기 위한 fixture일 뿐 production IdP 구현으로 주장하지 않습니다.

## 설계 2. 모델 출력과 실행 권한을 분리한다

Text2SQL model은 SQL **후보**만 반환합니다.

```text
Natural-language question
  -> Text2SqlModel
  -> candidate SQL
  -> SQL policy validation
  -> read-only query executor
```

SQLGlot 기반 application policy는 현재 다음을 검사합니다.

- exactly one statement
- SELECT/query only
- table allowlist

위험 SQL은 executor 호출 전에 차단합니다. 테스트에서는 의도적으로 `DELETE`를 반환하는 fixture를 사용해 DB executor 호출이 발생하지 않는 경계를 확인합니다.

## 설계 3. 애플리케이션 검증과 DB 권한을 이중화한다

애플리케이션 validator가 안전성의 유일한 경계가 되지 않도록 PostgreSQL 실행 계정도 별도로 제한했습니다.

```text
FastAPI service state
  -> application-owned metadata storage

Validated analytics query
  -> dedicated PostgreSQL analytics reader
      -> SELECT allowed
      -> write denied
```

Docker E2E에서는 애플리케이션을 우회해 동일한 reader 계정으로 직접 `SELECT`와 `INSERT`를 시도합니다.

- `SELECT`: 성공
- `INSERT`: 실패해야 gate 통과

또한 PostgreSQL은 host port를 publish하지 않고, bounded demo API만 loopback에 노출합니다.

## 설계 4. 생성, 검증, 실행, 정답을 다른 결과로 본다

평가에서는 다음 단계를 하나의 성공률로 합치지 않습니다.

```text
generation
!= validation
!= execution
!= correctness
```

Correctness는 SQL 문자열이 같은지 비교하지 않고 실제 columns와 rows가 expected result와 일치하는지 검사합니다.

따라서 서로 다른 SQL이 같은 의미의 결과를 반환하면 correctness는 성공할 수 있고, 정상 실행된 SQL이라도 질문에 맞지 않는 결과를 반환하면 execution success / correctness failure로 기록합니다.

## 자동 검증과 PostgreSQL runtime evidence

공개 저장소의 현재 deterministic fixture는 의도적으로 작은 2개 질문입니다.

```text
total cases:          2
generation success:   2
validation success:   2
execution success:    2
correctness success:  2
```

이 수치는 실제 LLM 정확도 100%를 의미하지 않습니다. **evaluation pipeline과 runtime boundary가 예상대로 동작하는지 확인하는 synthetic fixture 결과**입니다.

현재 공개 CI는 두 gate를 분리합니다.

1. Python test suite
2. Docker/PostgreSQL E2E

Docker/PostgreSQL E2E에서 확인한 범위:

- FastAPI startup/health
- 사용자별 workspace 접근 분리
- 자연어 질문 -> SQL -> PostgreSQL 조회 결과
- 위험 SQL application-level 차단
- PostgreSQL-backed result evaluation
- API loopback host binding
- PostgreSQL host port 비노출
- dedicated reader의 SELECT 성공
- 동일 reader의 write 실패
- clean volume에서 시작하고 종료 후 runtime 정리

상세 근거는 공개 저장소의 [PostgreSQL/Docker Runtime Evidence](https://github.com/son1004007/text2sql-workspace/blob/main/docs/POSTGRES_DOCKER_EVIDENCE.md)에 기록했습니다.

## 실제 runtime 검증에서 발견한 차이

SQLite-only 테스트에서는 드러나지 않았던 numeric aggregation 타입 차이가 첫 PostgreSQL E2E에서 실제 실패로 나타났습니다.

합성 money column을 PostgreSQL `NUMERIC(12, 2)`로 조정하고 evaluator가 `Decimal` 결과도 동일한 의미의 숫자로 비교하도록 수정한 뒤 PostgreSQL evaluation gate가 통과했습니다.

이 사례에서 실제 DB runtime을 별도 gate로 둔 이유는 단순히 Docker를 사용했다는 사실이 아니라 **빠른 테스트 환경과 실제 실행 엔진 사이의 가정을 검증하기 위해서**입니다.

## 대안과 trade-off

### 문자열/정규식만으로 SQL을 검사

구현은 단순하지만 중첩 query와 SQL 구조를 안정적으로 다루기 어렵습니다. 공개 샘플은 AST 기반 SQLGlot parser를 사용합니다.

### DB superuser 하나로 모든 처리를 실행

설정은 간단하지만 application validation 실패가 곧 DB write 위험으로 이어집니다. 별도 read-only analytics role을 사용해 권한 경계를 추가했습니다.

### 실제 LLM을 core CI에 필수화

실제 모델 결과를 볼 수 있지만 외부 API 상태, 비용과 비결정성 때문에 기본 회귀 gate가 불안정해집니다. 현재 core gate는 deterministic model을 사용하고, 실제 모델 검증은 별도 evidence가 생길 때만 추가하는 구조입니다.

## 현재 확인하지 않은 것

이 공개 샘플만으로 다음을 주장하지 않습니다.

- production authentication 또는 외부 IdP 연동
- arbitrary customer database 연결
- 실제 LLM의 통계적 Text2SQL 정확도
- production secret management
- 대규모 동시 사용자와 connection pool 용량
- 장기 부하, availability와 SLA
- 실제 회사 데이터나 운영 시스템 성능

## 현재 상태

`text2sql-workspace` 자체의 main CI에서 Python test와 Docker/PostgreSQL E2E는 성공했습니다. 공개 안전성 검토도 별도 문서로 기록했습니다.

이 사례는 현재 `sample-verified`입니다. 이 포트폴리오의 main Pages build/deploy까지 성공한 뒤에만 `published`로 승격합니다.
