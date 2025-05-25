# Dockerfile for PharmaLiv Java Web Application
# 
# This Dockerfile creates a container for running the PharmaLiv Java web application.
# It uses a multi-stage build process:
# 1. Build stage: Compiles and packages the application using Maven
# 2. Runtime stage: Deploys the WAR file to Tomcat
#
# The container supports both Java 17 and Java 21 (closest to Java 23 in container images)

# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy the project files
COPY ./pharmaliv/pom.xml ./pom.xml
COPY ./pharmaliv/src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime container
FROM tomcat:10.1.19-jdk21-temurin-jammy

# Remove default Tomcat applications
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy the built WAR file from the build stage
COPY --from=build /app/target/pharmaliv-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# Environment variables for database connection
# These can be overridden when running the container
ENV DB_HOST=mysql \
    DB_PORT=3306 \
    DB_NAME=pharmalive \
    DB_USER=root \
    DB_PASSWORD=password

# Expose the Tomcat port
EXPOSE 8080

# Health check to verify the application is running
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/ || exit 1

# Start Tomcat
CMD ["catalina.sh", "run"]