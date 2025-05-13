# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and download dependencies first (for better caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build the application
COPY src/ /app/src/
RUN mvn clean package -DskipTests

# Production stage
FROM eclipse-temurin:23-jre
WORKDIR /app

# Create a non-root user to run the application
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Copy the built artifact from the build stage
COPY --from=build /app/target/*.jar app.jar

# Set proper ownership
RUN chown -R appuser:appuser /app

# Expose application port
EXPOSE 8080

# Switch to non-root user
USER appuser

# Configure JVM options
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Entry point
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar