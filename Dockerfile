FROM gradle:8.10.2-jdk21 AS builder
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle gradle
COPY shared shared
COPY server server
ARG FIREBASE_ADMIN_SERVICE_ACCOUNT_KEY
RUN echo "$FIREBASE_ADMIN_SERVICE_ACCOUNT_KEY" > /app/firebase-admin-service-account-key.json
RUN gradle buildFatJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/server/build/libs/*.jar app.jar
COPY --from=builder /app/firebase-admin-service-account-key.json firebase-admin-service-account-key.json
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]