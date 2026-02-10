FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy parent POM and all module POMs
COPY pom.xml /app/
COPY common-core/pom.xml /app/common-core/
COPY task2/backend/pom.xml /app/task2/backend/
COPY task3/backend_t3/pom.xml /app/task3/backend_t3/

# Copy source for common-core and task3
COPY common-core/src /app/common-core/src
COPY task3/backend_t3/src /app/task3/backend_t3/src

# Build from root - reactor resolves parent + common-core, then task3
WORKDIR /app
RUN mvn clean install -pl task3/backend_t3 -am -DskipTests -B

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/task3/backend_t3/target/*-jar-with-dependencies.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
