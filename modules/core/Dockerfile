FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/core*.jar api.jar

ENTRYPOINT ["java", "-jar", "./api.jar"]