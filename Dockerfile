# Build stage: no local Maven or JDK needed to run the app.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /src
COPY pom.xml .
RUN mvn -B -q dependency:go-offline || true
COPY src ./src
RUN mvn -B -q package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /src/target/quarkus-app/ ./
ENV QUARKUS_HTTP_HOST=0.0.0.0
EXPOSE 8080
USER 1001
CMD ["java", "-jar", "quarkus-run.jar"]
