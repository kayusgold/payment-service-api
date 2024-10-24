# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace/app

# Copy pom.xml first to cache dependencies
COPY pom.xml .
COPY src src

# Build the application
RUN mvn clean package -DskipTests

# Extract the jar for layered copy
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# Run stage
FROM eclipse-temurin:17-jre
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Create directory for external config
RUN mkdir -p /app/config

# Add a non-root user
RUN groupadd -r spring && useradd -r -g spring spring
RUN chown -R spring:spring /app
USER spring:spring

ENTRYPOINT ["java","-cp","app:app/lib/*","-Dspring.config.location=/app/application.properties","com.kayode.paymentservice.PaymentServiceApplication"]