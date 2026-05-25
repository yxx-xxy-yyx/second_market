FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY second-market-backend-yxx/pom.xml ./pom.xml
RUN mvn -q -e -DskipTests dependency:go-offline
COPY second-market-backend-yxx/src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar
EXPOSE 8001
ENTRYPOINT ["java","-jar","/app/app.jar"]
