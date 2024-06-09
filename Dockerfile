# Use the official JDK 11 image as the base image
FROM openjdk:11.0-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the jar file into the container
COPY target/onebank-0.0.1-SNAPSHOT.jar /app/onebank.jar

# Expose the port that the application runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/onebank.jar"]
