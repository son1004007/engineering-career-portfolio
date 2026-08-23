FROM eclipse-temurin:21-jdk-jammy@sha256:9d8dcf999b0bce2453e913823595a5ff2a4e8e9e5d5241b45280d0ff069818ec AS build

RUN apt-get update \
    && apt-get install --yes --no-install-recommends unzip \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN sed -i 's/\r$//' mvnw .mvn/wrapper/maven-wrapper.properties \
    && chmod +x mvnw \
    && ./mvnw -q -DskipTests dependency:go-offline

COPY src/ src/
RUN ./mvnw -q -DskipTests clean package

FROM eclipse-temurin:21-jre-jammy@sha256:d63bd8d9b171999cbed8576f2c76e874dd4856791a358536e5c4d407e77edc13 AS runtime

ARG APP_UID=10001
ARG APP_GID=10001

LABEL org.opencontainers.image.source="https://github.com/son1004007/engineering-career-portfolio"
LABEL org.opencontainers.image.description="OpsMate Local controlled public demo application"

RUN apt-get update \
    && apt-get install --yes --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --gid "${APP_GID}" opsmate \
    && useradd --uid "${APP_UID}" --gid opsmate --no-create-home --shell /usr/sbin/nologin opsmate

WORKDIR /app

COPY --from=build --chown=opsmate:opsmate /workspace/target/opsmate-local-*.jar /app/app.jar

USER opsmate:opsmate

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
