# syntax=docker/dockerfile:1.4

ARG MAVEN_VERSION=3.9.6
ARG JDK_VERSION=17

FROM maven:${MAVEN_VERSION}-eclipse-temurin-${JDK_VERSION} AS builder
WORKDIR /workspace

# Copy pom files first to leverage cache for dependency resolution
COPY pom.xml ./
COPY p2p_common/pom.xml p2p_common/
COPY p2p_repository/pom.xml p2p_repository/
COPY p2p_framework_data/pom.xml p2p_framework_data/
COPY p2p_service/pom.xml p2p_service/
COPY p2p_security/pom.xml p2p_security/
COPY p2p_notification/pom.xml p2p_notification/
COPY p2p_scheduler/pom.xml p2p_scheduler/
COPY p2p_admin/pom.xml p2p_admin/
COPY p2p_websocket/pom.xml p2p_websocket/
COPY p2p_p2p/pom.xml p2p_p2p/
COPY partner_mock/pom.xml partner_mock/

RUN mvn -pl p2p_admin -am dependency:go-offline

# Copy full source and build only the admin module and its dependencies
COPY . .
RUN mvn -pl p2p_admin -am clean package -DskipTests

FROM eclipse-temurin:${JDK_VERSION}-jre
WORKDIR /app

COPY --from=builder /workspace/p2p_admin/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
