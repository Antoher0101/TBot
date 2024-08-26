FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/bot*.jar bot.jar

ENTRYPOINT ["java", "-jar", "./bot.jar"]