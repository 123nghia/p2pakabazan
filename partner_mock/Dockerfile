# Use Eclipse Temurin JDK base for better availability
FROM eclipse-temurin:17-jdk

# Set the working directory
WORKDIR /app

# Copy the jar file
COPY target/*.jar app.jar

# Expose port (default 9001, can be overridden by env)
EXPOSE 9001

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
