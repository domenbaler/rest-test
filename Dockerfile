FROM openjdk:16-alpine

COPY target/test-project-1.0.0.jar /test-project.jar

CMD ["java", "-jar", "/test-project.jar"]