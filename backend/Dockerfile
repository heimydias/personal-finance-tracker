FROM maven:3.9.7-eclipse-temurin-21 AS build-env
WORKDIR /app
COPY pom.xml ./
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build-env /app/target/*.jar /app/app.jar
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=dev

CMD ["java", "-jar", "/app/app.jar"]