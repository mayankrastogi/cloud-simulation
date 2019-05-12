# Start with a base image containing Java runtime
FROM openjdk:8-jdk-alpine

# Add Maintainer Info
LABEL maintainer="Manoj Prabhakar"

# Add a volume pointing to /tmp
VOLUME /tmp

# Make port 8080 available to the world outside this container
EXPOSE 8080

# The application's jar file
ARG JAR_FILE=target/scala-**/Mayank_K_Rastogi_project-assembly-0.1.jar

# Add the application's jar to the container
ADD ${JAR_FILE} cloudsimulation.jar 

# Run the jar file
ENTRYPOINT ["java","-jar","/cloudsimulation.jar"]
