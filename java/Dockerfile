FROM gradle:jdk17-alpine AS build
COPY --chown=gradle:gradle ./web /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew build --no-daemon -x test

FROM openjdk:17
EXPOSE 8080

RUN mkdir /app
RUN mkdir /app/web
RUN mkdir /app/web/java
RUN mkdir /app/python
COPY ./web/src/main/resources/import-prod.sql /app/web/java
COPY ./web/resources/ /app/web/java/resources/

COPY --from=build /home/gradle/src/build/libs/*.jar /app/web/java/application.jar

WORKDIR /app/web/java
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/app/web/java/application.jar"]
