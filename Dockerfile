# Use official OpenJDK image
FROM openjdk:17

# Expose port (Spring Boot default)
EXPOSE 8080

# Copy JAR file from target folder into container
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app.jar"]
