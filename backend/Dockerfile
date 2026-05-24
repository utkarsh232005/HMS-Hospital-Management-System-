FROM maven:3.9.11-eclipse-temurin-17 AS build
WORKDIR /build

# copy only what is needed for maven to resolve and build
COPY pom.xml .
COPY src ./src

RUN mvn -DskipTests package --no-transfer-progress

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
