# Setup

## Requirements

- JDK 21
- POSIX shell for deployment-script rehearsal
- Maven 3.9.x, or network access for the included launcher to bootstrap Maven 3.9.9

## Build and test

```bash
chmod +x mvnw
./mvnw clean verify
```

성공하면 `target/portable-war.war`가 생성됩니다.

## Local context-path run

개발 확인은 executable WAR/JAR style로도 가능합니다.

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.servlet.context-path=/demo --spring.profiles.active=local"
```

확인 경로:

```text
/demo/entry
/demo/healthz
```

## Deploy profile

`deploy` profile은 필수 런타임 값을 저장소 밖에서 요구합니다.

```bash
export PORTFOLIO_RUNTIME_TOKEN='local-synthetic-value'
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=deploy --server.servlet.context-path=/demo"
```

값이 없으면 `DeployRuntimeGuard`가 기동을 거부합니다. 실제 비밀값을 저장소나 명령행 기록에 넣는 방법은 권장하지 않습니다.

## WAR replacement rehearsal

실제 서버가 아닌 임시/합성 배포 디렉터리에 대해 다음 인터페이스를 사용합니다.

```bash
sh deploy/release-war.sh \
  target/portable-war.war \
  /path/to/synthetic-webapps \
  demo \
  http://127.0.0.1:8080/demo/healthz
```

실제 Tomcat의 reload, connector, TLS, session drain은 이 샘플 범위 밖입니다.
