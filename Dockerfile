# syntax=docker/dockerfile:1.7

FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY .mvn .mvn
COPY mvnw pom.xml sonar-project.properties checkstyle.xml ./
COPY src ./src

# The Angular UI is built in Dockerfile.frontend. This backend image packages only
# the Spring Boot API and solver core so Compose can run backend/frontend separately.
RUN ./mvnw -ntp --batch-mode -Pprod -DskipTests -Dskip.installnodenpm -Dskip.npm package \
    && mkdir -p /app \
    && cp target/*.jar /app/app.jar

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

ENV JAVA_OPTS="" \
    SPRING_OUTPUT_ANSI_ENABLED=ALWAYS

RUN groupadd --system unischeduler \
    && useradd --system --gid unischeduler --uid 1001 unischeduler

COPY --from=build --chown=unischeduler:unischeduler /app/app.jar /app/app.jar

USER unischeduler
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
