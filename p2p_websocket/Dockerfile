# Use Eclipse Temurin JDK base for better availability
FROM eclipse-temurin:17-jdk

# Set the working directory
WORKDIR /app

# Copy the jar file
COPY target/*.jar app.jar

# Expose port (default 9002, can be overridden by env)
EXPOSE 9002

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
