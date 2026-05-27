FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
RUN groupadd -r appgroup && useradd -r -g appgroup math
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN chown math:appgroup app.jar
USER math
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
