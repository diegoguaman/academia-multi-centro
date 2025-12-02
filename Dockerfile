# Multi-stage Dockerfile for Spring Boot application
# Stage 1: Build stage (optional - use if building in Docker)
# If you're building with Maven in CI/CD, skip this stage
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
# Copy maven wrapper and pom.xml
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -B
# Copy source code
COPY src ./src
# Build application (skip tests - already run in CI)
RUN ./mvnw package -DskipTests -B

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre-alpine
LABEL maintainer="academia-multi-centro"
LABEL description="Academia Multi-Centro Spring Boot Application"

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Set working directory
WORKDIR /app

# Copy JAR from builder stage or from CI/CD build
# If building in Docker: COPY --from=builder /app/target/*.jar app.jar
# If using CI/CD built JAR:
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Change ownership to non-root user
RUN chown spring:spring app.jar

# Switch to non-root user
USER spring:spring

# Expose application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# JVM optimization flags
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:InitialRAMPercentage=50.0 \
               -XX:+UseG1GC \
               -XX:+UseStringDeduplication \
               -Djava.security.egd=file:/dev/./urandom"

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

