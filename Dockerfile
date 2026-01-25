FROM maven:3.6.3-openjdk-17 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -DskipTests

FROM openjdk:17
WORKDIR /usr/local/app
COPY --from=build /home/app/target/financeiro-backend.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]