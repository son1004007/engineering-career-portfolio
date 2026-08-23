# Real Model E2E Evidence

## 검증 결과

`2026-08-23` 사설 GPU 호스트의 로컬 Ollama endpoint에 `OpsMate Local`의 실제 모델 E2E gate를 연결해 검증했다.

| 항목 | 검증 값 |
|---|---|
| Source commit | `ff67df0990cbed3a41cf5051a5e2701a7b2a7b50` |
| Test | `RealOpenWeightModelE2EIT` |
| Model | `gemma3:12b` |
| Ollama | `0.13.5` |
| Java | Temurin 21.0.12.1+1 |
| Synthetic prompts | 9 |
| Successful category validations | 9 / 9 |
| Measured p95 | 21,076 ms |
| Gate | p95 <= 30,000 ms |
| Maven exit code | 0 |

테스트 직후 Ollama는 `gemma3:12b`를 `100% GPU`로 표시했다. 해당 시점의 GPU 메모리 사용량은 선택된 RTX 2080 Ti에서 8,895 MiB / 11,264 MiB였다. 이 값은 9개 합성 요청 검증 직후의 관측값이며 일반적인 용량 계획 수치로 확대 해석하지 않는다.

## 검증한 경계

실제 `/api/chat` 호출을 통해 다음 경계를 함께 확인했다.

- 한국어 구매 요청 9건이 서버가 요구하는 `IT_EQUIPMENT`, `SOFTWARE`, `OFFICE_SUPPLIES` 분류를 모두 통과한다.
- 모델 응답이 `OllamaLocalLlmGateway`의 JSON Schema와 엄격한 역직렬화 조건을 통과한다.
- 모델이 반환한 정책 ID와 category가 서버 측 `PurchaseDraftAgent` 검증을 통과한 경우에만 초안이 저장된다.
- 9건 모두 HTTP `201 Created`까지 도달하며 요청·감사 이벤트 저장 건수가 각각 9건이다.
- 9개 요청의 관측 p95가 설정한 30초 상한 이내다.

이 실행은 실제 모델 E2E만 분리해서 재현하기 위해 Maven Failsafe의 `RealOpenWeightModelE2EIT`만 선택했다. 따라서 `2026-08-04`의 전체 `clean verify` 54개 성공 기록을 대체하지 않으며, PostgreSQL Testcontainers 검증을 새로 수행했다는 의미도 아니다.

## 실행 형태

실제 검증에서는 저장소의 clean `main` checkout과 GitHub `main` commit이 일치하는지 먼저 확인한 뒤, `git archive`로 만든 일회성 snapshot에서 테스트했다. 원본 checkout에는 build artifact를 생성하지 않았다.

핵심 gate는 다음 환경을 사용한다.

```text
OPSMATE_REAL_MODEL_E2E=YES
OPSMATE_LLM_BASE_URL=http://127.0.0.1:11434
OPSMATE_LLM_ALLOWED_HOSTS=127.0.0.1,localhost
OPSMATE_LLM_MODEL=gemma3:12b
OPSMATE_REAL_MODEL_P95_MAX_MS=30000
```

endpoint의 실제 외부 주소, 인증 token, 원문 모델 응답은 공개 증적에 기록하지 않는다.

## 아직 검증하지 않은 범위

이번 결과는 실제 오픈웨이트 모델과 애플리케이션 adapter 사이의 로컬 E2E 증거다. 다음 항목은 별도 배포 gate이며 여전히 미검증이다.

- Docker Compose + NVIDIA Container Toolkit 기반 모델 호스트 open/close/reopen rehearsal
- 인증 proxy와 VPN bind를 포함한 사설 모델 endpoint 배포
- 공개 application URL과 HTTPS/ACME
- 외부 모바일 smoke test와 DB·모델 포트 외부 차단 확인
- host egress allowlist와 edge/WAF rate limit
- 애플리케이션·모델 양쪽 호스트의 normal/emergency close 및 same-digest reopen

따라서 전체 프로젝트 상태는 계속 `tested-component`로 유지한다.
