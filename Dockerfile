# Stage 1: Build React frontend
FROM node:20-alpine AS frontend
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ .
RUN npm run build

# Stage 2: Build Spring Boot JAR
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app
COPY . .
COPY --from=frontend /app/src/main/resources/static/ src/main/resources/static/
RUN ./gradlew bootJar -x test

# Stage 3: Minimal runtime image
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
