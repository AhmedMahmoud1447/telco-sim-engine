FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

RUN apk add --no-cache tzdata
ENV TZ=Africa/Cairo

ENTRYPOINT ["java", "-jar", "app.jar"]