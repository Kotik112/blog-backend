FROM openjdk:21-jdk-slim
LABEL maintainer="Arman Iqbal"
LABEL description="A simple Spring Boot application with a blog post REST API."

# Set the working directory
WORKDIR /app .
# Copy the JAR file into the container
COPY target/blog-backend-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

# Health check to ensure the application is running
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1