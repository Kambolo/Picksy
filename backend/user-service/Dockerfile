FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY common-dto ./common-dto
RUN cd common-dto && mvn -q clean install

COPY user-service/pom.xml .
COPY user-service/src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]

