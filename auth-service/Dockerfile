FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# --- zainstaluj common-dto ---
COPY common-dto ./common-dto
RUN cd common-dto && mvn -q clean install

# --- skopiuj serwis ---
COPY auth-service/pom.xml .
COPY auth-service/src ./src

# --- build ---
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

