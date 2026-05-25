FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN set -e; \
    if [ -f second-market-backend-yxx/pom.xml ]; then cd second-market-backend-yxx; fi; \
    mvn -q -e -DskipTests dependency:go-offline; \
    mvn -q -DskipTests package; \
    mv target/*.jar /app/app.jar

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/app.jar /app/app.jar
EXPOSE 8001
ENTRYPOINT ["java","-jar","/app/app.jar"]
