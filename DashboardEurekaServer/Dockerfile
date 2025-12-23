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
COPY DashboardEurekaServer/ ./DashboardEurekaServer/

# Build the project
RUN ./gradlew :DashboardEurekaServer:bootJar -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/DashboardEurekaServer/build/libs/*.jar application.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","application.jar"]
