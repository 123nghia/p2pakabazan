# syntax=docker/dockerfile:1.4

ARG MAVEN_VERSION=3.9.6
ARG JDK_IMAGE=eclipse-temurin:17-jdk

FROM maven:${MAVEN_VERSION}-${JDK_IMAGE#*:} AS builder
WORKDIR /workspace

# Copy only pom files first to leverage build cache for dependencies
COPY pom.xml ./
COPY p2p_common/pom.xml p2p_common/
COPY p2p_repository/pom.xml p2p_repository/
COPY p2p_service/pom.xml p2p_service/
COPY p2p_p2p/pom.xml p2p_p2p/

RUN mvn -pl p2p_p2p -am dependency:go-offline

# Copy the remainder of the source code and build the application
COPY . .
RUN mvn -pl p2p_p2p -am package -DskipTests


FROM eclipse-temurin:17-jre
WORKDIR /app

ARG JAR_FILE=/workspace/p2p_p2p/target/p2p_p2p-1.0-SNAPSHOT.jar
COPY --from=builder ${JAR_FILE} app.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java","-jar","/app/app.jar"]
