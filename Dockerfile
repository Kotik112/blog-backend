FROM openjdk:21-jdk-slim
LABEL maintainer="Arman Iqbal"
LABEL description="A simple Spring Boot application with a blog post REST API."

# Set the working directory
WORKDIR /app
# Copy the JAR file into the container
