FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY SecondMarket-backend/SecondMarket-backend/pom.xml ./pom.xml
COPY SecondMarket-backend/SecondMarket-backend/src ./src
RUN mvn -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/project-backend-1.0.0.jar ./app.jar
EXPOSE 8001
ENTRYPOINT ["sh","-c","java -Djava.net.preferIPv4Stack=true -Dserver.address=0.0.0.0 -Dserver.port=${PORT:-8001} -Dserver.servlet.context-path=/api -jar /app/app.jar"]
