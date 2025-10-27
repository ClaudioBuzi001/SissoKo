# syntax=docker/dockerfile:1.6

##
## Build stage
##
FROM maven:3.9.10-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml .
COPY src ./src

RUN mvn -B clean package -DskipTests

##
## Runtime stage
##
FROM eclipse-temurin:21-jre
WORKDIR /app

ENV JAVA_OPTS="" \
    SPRING_PROFILES_ACTIVE= \
    SPRING_DATA_MONGODB_URI=mongodb://mongo:27017/pizzeria \
    SPRING_DATA_MONGODB_DATABASE=pizzeria

COPY --from=build /workspace/target/pizzeria-service-0.0.1-SNAPSHOT.jar /app/pizzeria-service.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/pizzeria-service.jar"]
