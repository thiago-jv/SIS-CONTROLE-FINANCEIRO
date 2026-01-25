FROM maven:3.9-eclipse-temurin-17 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /usr/local/app
COPY --from=build /home/app/target/financeiro-backend.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]