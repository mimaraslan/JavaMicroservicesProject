# Multi-stage build: Build stage
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy Gradle files
COPY gradle/ ./gradle/
COPY gradlew ./
COPY gradlew.bat ./
COPY build.gradle ./
COPY settings.gradle ./
COPY dependencies.gradle ./

# Make gradlew executable
RUN chmod +x gradlew

# Copy project source
COPY NotificationService/ ./NotificationService/
COPY ExceptionHandlerService/ ./ExceptionHandlerService/

# Build the project
RUN ./gradlew :NotificationService:bootJar -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/NotificationService/build/libs/*.jar application.jar
EXPOSE 9594
ENTRYPOINT ["java","-jar","application.jar"]
