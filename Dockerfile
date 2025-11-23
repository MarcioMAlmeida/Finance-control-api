FROM eclipse-temurin:17-jre-focal

COPY target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]