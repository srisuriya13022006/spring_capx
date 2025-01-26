# Use Maven with OpenJDK 17 for building the project
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the source code into the container
COPY . .

# Build the project and skip tests
RUN mvn clean package -DskipTests

# Use OpenJDK 17 runtime for running the application
FROM openjdk:17.0.1-jdk-slim

# Copy the JAR file from the build stage to the runtime stage
COPY --from=build /app/target/portfolio-0.0.1-SNAPSHOT.jar portfolio.jar

# Expose the application on port 8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java","-jar","portfolio.jar"]
