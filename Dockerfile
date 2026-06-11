# syntax=docker/dockerfile:1

# ---- Build stage: compile the Spring Boot jar with Maven ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# Cache dependencies first (only re-resolves when pom.xml changes).
COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline

# Compile and package (only src/main/java is relevant; the frontend sources are ignored).
COPY src ./src
RUN mvn -q -DskipTests package

# ---- Runtime stage: distroless JRE — no shell, no package manager, minimal attack surface ----
FROM gcr.io/distroless/java21-debian12:nonroot
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
EXPOSE 8080
# distroless:nonroot runs as UID 65532 by default — no USER directive needed
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
