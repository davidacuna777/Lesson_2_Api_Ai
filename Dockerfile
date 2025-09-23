# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -e -DskipTests package

# ---------- Run stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app
ARG JAR_FILE=/app/target/leccion2-servidor-factory-deepseek-telegram-1.0.0.jar
ENV HTTP_PORT=8080
EXPOSE ${HTTP_PORT}
COPY --from=build ${JAR_FILE} /app/app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]