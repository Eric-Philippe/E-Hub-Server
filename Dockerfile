# Multi-stage Dockerfile for Spring Boot Kotlin application

# Build stage
FROM gradle:8.10-jdk21-alpine AS builder

WORKDIR /app

# Copy Gradle wrapper and build files first
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle .
COPY settings.gradle .

# Make gradlew executable
RUN chmod +x gradlew

# Download dependencies first
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src/ src/

# Build the application
RUN ./gradlew clean bootJar -x test --no-daemon

# Extract JAR layers for better caching
RUN java -Djarmode=layertools -jar build/libs/*.jar extract

# Runtime stage - Use distroless for minimal size
FROM gcr.io/distroless/java21-debian12:nonroot

WORKDIR /app

# Copy extracted layers
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./

# Copy resources folder to make it accessible
COPY --from=builder /app/src/main/resources/ resources/

# Expose the port the app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
