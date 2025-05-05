FROM gradle:8.6.0-jdk17 AS build
WORKDIR /app
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src ./server/src
RUN gradle build --no-daemon

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]