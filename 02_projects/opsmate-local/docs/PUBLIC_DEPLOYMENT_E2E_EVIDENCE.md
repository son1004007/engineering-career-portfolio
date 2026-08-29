# OpsMate Local Public Deployment E2E Evidence

- 검증일: `2026-08-29` (Asia/Seoul)
- 상태: public Internet deployment/network/lifecycle boundary `verified`
- 범위: 개인 Synology NAS의 HTTPS ingress -> loopback Nginx edge -> Spring Boot -> private PostgreSQL / restricted model tunnel -> 승인된 native Ollama
- 최종 runtime 상태: `CLOSED`
- 주의: 이 문서는 bounded verification 증거이며 24x7 가용성, SLA, 장기 부하 또는 production traffic 규모를 증명하지 않습니다.

## Reviewed release

- portfolio source: `f99686981da7efb8802635ae2bde5b0f781433ad`
- application image: `ghcr.io/son1004007/opsmate-local@sha256:61e267c05bf0ce0ea932ae62a3989194bd2a0065532d0ee4caee8b37c8f9d40b`
- model-tunnel image: `ghcr.io/son1004007/opsmate-model-tunnel@sha256:7fc133485a8ba60190e55b5eeb2da8b5eb02c1aa7e70e0cc0ce7b723746db1df`
- model: `gemma3:12b`

동일 release는 먼저 Synology internal E2E에서 검증됐으며, 상세 내부 경계는 [`NAS_INTERNAL_E2E_EVIDENCE.md`](NAS_INTERNAL_E2E_EVIDENCE.md)를 기준으로 합니다.

## Public ingress boundary

공개 검증은 다음 구조에서 수행했습니다.

```text
Internet
  -> Synology DSM Reverse Proxy / TLS
  -> NAS loopback Nginx edge
  -> Spring Boot app
       -> private PostgreSQL
       -> private model_link
            -> non-root destination-restricted SSH tunnel
                 -> approved native Ollama
```

실제 hostname, NAS/IP, SSH user, key, known_hosts 원문과 credential은 공개하지 않습니다.

## Verification result

`device-control`의 bounded `runtime-public-verify`에서 exact reviewed release를 잠시 열고 외부 GitHub-hosted runner에서 실제 HTTPS origin을 검증한 뒤 다시 닫았습니다.

검증된 항목:

- public HTTPS root/live marker: `PASS`
- public `/api/**` denial과 `/actuator/**` 차단: `PASS`
- restricted tunnel을 통한 실제 model path: `PASS`
- 실제 모델 기반 draft -> submit -> approve -> order -> audit -> cleanup: `PASS`
- 두 외부 session의 cross-workspace isolation: `PASS`
- COOKIE-only session tracking, URL `;jsessionid` rewriting 부재: `PASS`
- app direct Internet egress: `blocked`
- app/DB/model-tunnel host port publish: `none`
- 외부 PostgreSQL/model/loopback edge 직접 TCP 노출: `closed`
- public Nginx rate limit: bounded burst 60건 중 allowed `24`, HTTP `429` `36`, transport failure `0`
- container log의 DB credential/private-key/Bearer marker scan: `PASS`

## Lifecycle result

첫 공개 smoke가 끝난 뒤 동일 실행에서 다음 lifecycle을 검증했습니다.

1. normal close
   - synthetic workspace purge: `PASS`
   - public live marker 부재: `PASS`
   - running OpsMate container `0`
   - PostgreSQL persistent volume 보존
   - strict state `CLOSED`
2. same-digest reopen
   - 동일 app/tunnel immutable digest 사용
   - public HTTPS + 실제 모델 persona smoke 재실행: `PASS`
3. emergency close
   - env-independent targeted stop: `PASS`
   - public live marker 부재: `PASS`
   - strict state `CLOSED`
4. recovery normal close
   - synthetic data purge 재확인
   - running container `0`
   - PostgreSQL volume 보존
   - policy flags `YES_YES`
   - final state `CLOSED`

최종 marker:

```text
opsmate_nas_runtime_public_verify=PASS
runtime_policy_flags=YES_YES
runtime_final_state=CLOSED
```

## Evidence chain

- public runtime verifier implementation/CI: private `device-control` PR `#150`, 후속 verifier fixes `#153`, `#155`
- first public diagnostic: private runtime run `33240608943`
  - HTTPS/model/persona/isolation/non-exposure까지 성공
  - 순차 요청 방식이 설정 rate를 넘지 못해 external 429 probe 자체를 수정
  - failure cleanup 후 `CLOSED` 재확인
- final public E2E: private runtime run `33241004788`
  - public/network/security/lifecycle 전체 bounded gate `PASS`

private control repository와 실제 runtime credential은 공개 증거에 포함하지 않습니다. 위 run ID는 결과 추적용 식별자이며 공개 접근 가능성을 의미하지 않습니다.

## Safe interpretation

이 결과로 말할 수 있는 것:

- exact immutable artifact를 실제 Synology runtime에서 열고 인터넷 HTTPS 경로로 사용할 수 있음을 검증했습니다.
- 실제 open-weight model을 사용한 업무 흐름이 public ingress를 통과했고, workspace/session/DB/model/egress/rate 경계가 bounded E2E에서 동작했습니다.
- normal close, same-digest reopen, emergency close와 최종 CLOSED를 실제 target에서 수행했습니다.

이 결과로 말할 수 없는 것:

- 24x7 production 운영 또는 SLA
- 장기 사용자 트래픽, 대규모 동시 사용자 성능
- DDoS/WAF 전문 방어 검증
- Office/native Ollama 자체의 장기 운영 보장
- 실제 기업 사용자·실데이터 처리

공개 데모는 합성 데이터와 승인된 private model path만 사용하며, 검증 종료 후 애플리케이션 workload는 `CLOSED` 상태로 유지합니다.
