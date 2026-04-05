FROM eclipse-temurin:21 AS builder

WORKDIR /app

COPY build.gradle .
COPY settings.gradle .
COPY gradle gradle/
COPY gradlew .
COPY frontend frontend/
COPY src src/

RUN chmod +x ./gradlew && ./gradlew clean build -x test


FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=builder /app/build/libs/*.jar app.jar

ENV JAVA_OPTS="-Xms256m -Xmx512m"

EXPOSE 8000

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]