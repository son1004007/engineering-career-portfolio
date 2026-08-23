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

### P20. OpsMate public application 배포 준비 — `blocked-user`

목표: 실제 인터넷에서 접근 가능한 애플리케이션 URL을 만들되 DB와 모델 endpoint는 공개하지 않는다.

- [x] 현재 배포 자산과 target runtime 재점검
- [ ] 공개 hostname/TLS 방식 확정
- [x] app runtime secret 주입 경로 확인: 실제 값은 target-local `deploy/.env`에만 저장하고 Git/Issue/CI log에는 남기지 않음
- [x] immutable image digest와 one-shot migration 절차 확인: CI에서 non-root image와 migration rehearsal 성공
- [ ] 외부 공개 전 actual app host의 closed 상태 검증

확인 결과:

1. public deploy 설정의 `DEMO_DOMAIN`은 아직 예시값이며 실제 hostname이 확정되지 않았다.
2. 개인 Synology NAS는 Docker/Compose와 Tailscale이 검증된 개인 소유 container host이므로 app-host 후보로 검토할 수 있다. 현재 확인된 서비스 노출은 tailnet 전용이며 OpsMate의 public ingress는 아직 구성되지 않았다.
3. Office GPU 서버는 회사 소유 자산이다. `2026-08-23` 실제 `gemma3:12b` adapter E2E는 검증됐지만, **외부 개인 포트폴리오 방문자의 추론 요청을 이 회사 자산에서 처리해도 된다는 명시적 승인 증거는 현재 repository/control/runtime evidence에서 확인되지 않았다.**
4. IDC Docker 서버 역시 회사 소유이므로 승인 없이 public app-host로 사용하지 않는다.
5. 회사 소유 GPU의 public-traffic 사용 승인 전에는 model proxy, VPN/tunnel, public app 연결이나 공개 포트를 구성하지 않는다.

현재 사용자 작업:

- **회사 Office GPU 서버를 외부 개인 포트폴리오 데모의 모델 추론 용도로 사용하는 것이 명시적으로 허용되는지 확인한다.**
- 승인됐다면 비밀값이나 내부 문서를 전달할 필요 없이 `승인됨`과 허용 범위(예: 외부 포트폴리오 요청 처리 가능, 사용 기간/시간 제한 여부)만 알려준다.
- 승인되지 않았거나 확인할 수 없으면 `승인 없음`이라고 알려준다. 그러면 회사 장비를 제외하고 개인/외부 모델 호스트 대안으로 설계를 변경한다.

이 승인 여부가 확정되기 전에는 hostname/DNS나 public edge 계정을 먼저 만들지 않는다. 모델 호스트 경계가 확정된 뒤 가장 적은 추가 계정·비용으로 public ingress를 선택한다.

완료 조건: 모델 호스트 사용 권한, app host, 공개 hostname/TLS, secret 주입 경로와 보안 경계가 확정되고 실제 target에서 공개 전 preflight가 성공한다.

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

`P00`과 `P10`은 사용자 작업 없이 완료했다.

현재 필요한 작업은 P20의 **회사 Office GPU 서버 public-traffic 사용 승인 여부 확인 한 가지**다. 승인 여부가 정해지기 전에는 회사 장비에 공개 트래픽 경로를 만들거나 외부 서비스를 임의로 구독하지 않는다.

## 완료 판정

전체 포트폴리오를 단순히 `완료`라고 부르지 않고 다음을 분리한다.

1. GitHub Pages 포트폴리오: `published`
2. OpsMate 코드/컴포넌트: `implemented` + 최신 CI regression 성공
3. 실제 모델 adapter E2E: `verified` 범위 명시
4. public application/network/lifecycle: 해당 gate 완료 전까지 `pending`/`tested-component`
5. 회사 업무 사례: 원본 검토와 독립 공개 샘플 검증 상태를 분리
