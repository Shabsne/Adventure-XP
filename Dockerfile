# STAGE 1: Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# 1. Cache dependencies: Kopier kun pom.xml først
COPY pom.xml .
# Hent alle dependencies uden at bygge koden (dette caches af Docker)
RUN mvn dependency:go-offline -B

# 2. Kopier kildekoden og byg appen
COPY src ./src
# -DskipTests sørger for at Maven ikke kører test under build
RUN mvn package -DskipTests

# STAGE 2: Runtime stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Kopier kun den færdige .jar fil fra build-stadiet
COPY --from=build /app/target/*.jar app.jar

# Start applikationen
ENTRYPOINT ["java", "-jar", "app.jar"]

