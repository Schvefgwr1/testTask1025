FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy parent POM and all module POMs first (for layer caching)
COPY pom.xml /app/
COPY common-core/pom.xml /app/common-core/
COPY task2/backend/pom.xml /app/task2/backend/
COPY task3/backend_t3/pom.xml /app/task3/backend_t3/

# Copy source for common-core and task2
COPY common-core/src /app/common-core/src
COPY task2/backend/src /app/task2/backend/src

# Build from root - Maven reactor resolves parent + common-core, then task2
WORKDIR /app
RUN mvn clean install -pl task2/backend -am -DskipTests -B

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/task2/backend/target/*-jar-with-dependencies.jar app.jar
RUN mkdir -p /app/uploads

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
