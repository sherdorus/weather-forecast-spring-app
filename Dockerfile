FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /weatherApp

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -Pproduction -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /weatherApp

COPY --from=build /weatherApp/target/*.jar weatherApp.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "weatherApp.jar"]
