FROM eclipse-temurin:23-jre

COPY target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]