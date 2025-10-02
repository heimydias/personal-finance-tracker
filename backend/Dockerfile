FROM maven:3.9.7-eclipse-temurin-21 AS build
WORKDIR /app

# Cache de dependências
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .
RUN mvn dependency:go-offline -B

# Build
COPY src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine AS runtime

# Instalar curl para healthcheck
RUN apk add --no-cache curl

# Criar usuário não-root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar
RUN chown -R appuser:appgroup /app
USER appuser
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/personal-finance-tracker/actuator/health || exit 1

CMD ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:+UseG1GC", \
    "-XX:+UseStringDeduplication", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "/app/app.jar"]