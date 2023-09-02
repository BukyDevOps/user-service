FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/user-service-0.0.1-SNAPSHOT.jar /app/
EXPOSE 8081
CMD ["java", "-jar", "user-service-0.0.1-SNAPSHOT.jar"]