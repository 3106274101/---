# Render 没有原生 Java，必须用 Docker 构建并启动 Spring Boot。
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/tradehub-api-1.0.0.jar app.jar
RUN mkdir -p /app/uploads
ENV TRADEHUB_UPLOAD_DIR=/app/uploads
EXPOSE 8080
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0"
ENTRYPOINT ["java", "-jar", "app.jar"]
