# Portfolio Completion Plan

- 기준일: `2026-08-23`
- 목적: 현재 공개 포트폴리오와 `OpsMate Local`을 실제 검증 증거에 맞춰 동기화하고, 남은 외부 배포·운영 gate를 순서대로 완료한다.
- 원칙: 문서상 완료가 아니라 최근 commit, test, workflow, bounded runtime E2E를 근거로 상태를 올린다.
- 전역 기준: `son1004007/ai-agent-workflow-playbook/CONTROL.md`
- 실행 ledger: [`WORKS.md`](WORKS.md)
- 장기 backlog: [`TASKS.md`](TASKS.md)

## 상태

| 상태 | 의미 |
|---|---|
| `pending` | 아직 시작하지 않음 |
| `in-progress` | 현재 수행 중 |
| `blocked-user` | 계정 승인·물리 기기 확인 등 사용자만 가능한 입력 대기 |
| `blocked-external` | 외부 서비스/네트워크/runtime 조건 대기 |
| `verified` | 요구된 검증 증거까지 확보 |

## 실행 순서

### P00. 상태 문서 동기화 — `verified`

목표: `2026-08-23` 실제 모델 E2E와 전역 Control/Office runtime 검증 결과를 공개 상태 문서에 일관되게 반영한다.

- [x] `AI_CONTEXT.md` 기준일과 OpsMate 상태 갱신
- [x] `WORKS.md` W09 실제 모델 E2E 증거와 남은 gate 갱신
- [x] `03_portfolio/evidence-index.md` 실제 모델 E2E 증거와 현재 판단 갱신
- [x] `03_portfolio/portfolio-strategy.md` Track A 검증 상태 갱신
- [x] `TASKS.md` 완료된 실제 모델 gate를 제거하고 남은 순서를 명확화
- [x] README/AGENTS/REAL_MODEL_E2E_EVIDENCE와 상호 모순이 없는지 확인

검증: PR `#10`의 `Verify Portfolio` run `32638023909`에서 public portfolio 정합성 검사와 Jekyll build가 성공했다.

사용자 작업: 없음.

### P10. 최신 baseline regression — `verified`

목표: 문서 동기화 후 현재 `main` 후보에 대해 기존 검증을 다시 실행한다.

- [x] OpsMate `clean verify`
- [x] Spring Security 샘플 `clean verify`
- [x] 저장소 공개 링크·credential·상태 정합성 검사
- [x] Jekyll/Pages build 검증
- [x] container/config 정적 검수
- [x] Docker non-root image build와 one-shot migration rehearsal

검증: PR `#10`, `Verify Portfolio` run `32638023909`에서 다음 job이 모두 성공했다.

- `OpsMate Local`
- `Spring Security auth bridge`
- `public-portfolio`
- `Jekyll site build`
- `OpsMate container and runbook assets`

container job은 shell syntax, Compose config, Caddy config, non-root image, one-shot migration container와 cleanup까지 통과했다.

사용자 작업: 없음.

### P20. OpsMate public application 배포 준비 — `in-progress`

목표: 실제 인터넷에서 접근 가능한 애플리케이션 URL을 만들되 DB와 모델 endpoint는 공개하지 않는다.

- [x] 현재 배포 자산과 target runtime 재점검
- [x] 모델 호스트 사용 권한 확인: 조직 승인 확인됨
- [x] app-host 후보 확정: 개인 Synology NAS
- [x] model-host 후보 확정: Office GPU 서버
- [ ] NAS↔Office 모델 전용 연결 방식의 실제 runtime preflight
- [ ] 공개 hostname/TLS 방식 확정
- [x] app runtime secret 주입 경로 확인: 실제 값은 target-local `deploy/.env`에만 저장하고 Git/Issue/CI log에는 남기지 않음
- [x] immutable image digest와 one-shot migration 절차 확인: CI에서 non-root image와 migration rehearsal 성공
- [ ] 외부 공개 전 actual app host의 closed 상태 검증

확인 결과:

1. public deploy 설정의 `DEMO_DOMAIN`은 아직 예시값이며 실제 hostname이 확정되지 않았다.
2. 개인 Synology NAS는 Docker/Compose가 검증된 개인 소유 container host이며 app-host로 사용한다. `device-control`의 Synology target은 Tailscale이 아니라 공인 SSH endpoint `son1004007.synology.me:65022`를 사용하고 read-only E2E가 검증됐다. 이는 NAS가 tailnet 전용이라는 뜻이 아니다. OpsMate의 별도 public HTTPS application ingress는 아직 구성·검증되지 않았다.
3. Office GPU 서버는 조직 승인을 받아 외부 개인 포트폴리오 데모의 모델 추론 용도로 사용할 수 있다. 공개 저장소에는 승인 주체·내부 문서 원문을 기록하지 않고 승인 경계만 기록한다.
4. `2026-08-23` Office GPU의 Ollama `gemma3:12b` 실제 adapter E2E는 9/9 성공했고 관측 p95 21,076ms로 30초 gate를 통과했다.
5. 모델 API/Ollama 자체 포트를 인터넷에 공개하지 않는다. NAS가 Office의 기존 승인 SSH 경로를 사용해 제한된 모델 연결을 만드는 방식을 우선 검토한다.
6. IDC Docker 서버는 이번 공개 포트폴리오 배포 경로에서 제외한다.

현재 사용자 작업: **없음**. GitHub와 기존 허용 runtime으로 NAS/Office read-only preflight를 먼저 수행한다. 공유기/DSM UI, DNS, 방화벽 또는 계정 승인이 실제 blocker가 될 때만 한 가지 작업으로 요청한다.

완료 조건: NAS↔Office 모델 연결, app host, 공개 hostname/TLS, secret 주입 경로와 보안 경계가 확정되고 실제 target에서 공개 전 preflight가 성공한다.

### P30. 외부 네트워크·보안 gate — `pending`

목표: 공개 app만 외부에서 접근 가능하고 DB/model/admin 경계는 닫혀 있음을 증명한다.

- [ ] HTTPS/ACME 확인
- [ ] 외부 smoke test
- [ ] DB 외부 비노출 확인
- [ ] model endpoint 외부 비노출 확인
- [ ] app-host egress allowlist 검증
- [ ] edge/WAF 익명 요청 rate limit 검증
- [ ] 공개 로그에 credential/민감 endpoint가 남지 않는지 확인

사용자 작업: 외부 계정에서 MFA/보안 승인이 요구될 때만 승인한다.

완료 조건: 외부에서 app은 정상 사용 가능하고 내부 의존성은 노출되지 않으며 rate/egress 통제가 검증된다.

### P40. close/reopen lifecycle rehearsal — `pending`

목표: 공개 데모를 안전하게 닫고 동일 artifact로 다시 열 수 있음을 실제로 검증한다.

- [ ] app normal close
- [ ] model normal close
- [ ] environment-independent emergency close
- [ ] closed verifier
- [ ] same-image-digest reopen
- [ ] smoke 재검증
- [ ] 최종 상태를 `CLOSED`로 유지

사용자 작업: 없음. 단, target이 사람 승인을 강제하는 보안 정책을 사용하면 그 승인만 요청한다.

완료 조건: 닫기·비상 종료·동일 digest 재개가 실제 target에서 성공하고 최종적으로 닫힌 상태가 확인된다.

### P50. 두 번째 Java/Spring 사례 공개 — `pending`

목표: 대표 AI 프로젝트 외에 기존 Java/Spring 실무 깊이를 독립 재현 사례로 한 건 더 증명한다.

- [ ] `03_portfolio/case-study-index.md`에서 우선 후보 선택
- [ ] 기존 authorized evidence에서 본인 기여·공개 경계 재확인
- [ ] 합성 도메인으로 독립 구현
- [ ] 정상·실패·경계 테스트
- [ ] 공개 문서와 코드 검수
- [ ] Pages 게시

사용자 작업: 회사 내부 사실 중 저장소/기존 authorized evidence로 확정할 수 없는 항목이 실제로 필요한 경우에만 사실 확인을 요청한다.

완료 조건: 공개 샘플 코드와 최근 성공 테스트가 있는 두 번째 사례가 게시된다.

### P60. 포트폴리오 유지관리 — `pending`

- [ ] Pages 링크와 배포 상태 정기 확인
- [ ] 물리 모바일 화면 최종 검수
- [ ] 회사 GitHub evidence 월말 갱신
- [ ] 완료·미검증 badge와 테스트 수 동기화

사용자 작업: 물리 모바일 기기에서의 최종 UX 확인은 사용자 확인이 가장 신뢰할 수 있다. 문제가 발견되면 화면/증상을 전달하고 수정은 저장소에서 수행한다.

## 현재 사용자에게 필요한 작업

`P00`과 `P10`은 사용자 작업 없이 완료했다. Office GPU의 공개 포트폴리오 추론 사용 승인도 확인됐다.

현재 즉시 필요한 사용자 작업은 **없음**. P20 runtime preflight와 설계 확정을 계속 수행한다. 사용자만 가능한 공유기/DSM/DNS/보안 승인이 실제 blocker가 되었을 때 그 한 가지 작업만 요청한다.

## 완료 판정

전체 포트폴리오를 단순히 `완료`라고 부르지 않고 다음을 분리한다.

1. GitHub Pages 포트폴리오: `published`
2. OpsMate 코드/컴포넌트: `implemented` + 최신 CI regression 성공
3. 실제 모델 adapter E2E: `verified` 범위 명시
4. public application/network/lifecycle: 해당 gate 완료 전까지 `pending`/`tested-component`
5. 회사 업무 사례: 원본 검토와 독립 공개 샘플 검증 상태를 분리
