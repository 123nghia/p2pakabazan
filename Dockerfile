# syntax=docker/dockerfile:1.4

ARG MAVEN_VERSION=3.9.6
ARG JDK_VERSION=17

FROM maven:${MAVEN_VERSION}-eclipse-temurin-${JDK_VERSION} AS builder
WORKDIR /workspace

# Copy only pom files first to leverage build cache for dependencies
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

RUN mvn -pl p2p_p2p -am dependency:go-offline

# Copy the remainder of the source code and build the application
COPY . .
RUN mvn -pl p2p_p2p -am clean package -DskipTests


ARG JDK_VERSION=17
FROM eclipse-temurin:${JDK_VERSION}-jre
WORKDIR /app

# Install netcat for wait-for-it script
RUN apt-get update && \
    apt-get install -y netcat-openbsd && \
    rm -rf /var/lib/apt/lists/*

# Copy the built jar from the builder image
COPY --from=builder /workspace/p2p_p2p/target/*.jar app.jar

# Expose port (default 9000, can be overridden by env)
EXPOSE 9000

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
