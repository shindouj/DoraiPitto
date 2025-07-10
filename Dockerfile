FROM eclipse-temurin:21-alpine AS builder
LABEL authors="jeikobu__"

COPY ./build/libs/doraipitto-0.0.1-SNAPSHOT.jar /app/doraipitto.jar

ENTRYPOINT ["java", "-jar", "/app/doraipitto.jar", "--spring.profiles.active=prod"]