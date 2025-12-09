FROM eclipse-temurin:25-jdk AS builder

WORKDIR /builder

COPY build/libs/app.jar app.jar

RUN java -Djarmode=tools -jar app.jar extract --destination app

WORKDIR /builder/app

RUN java -XX:+UseCompactObjectHeaders -XX:AOTCacheOutput=app.aot -jar app.jar

# Final stage
FROM eclipse-temurin:25-jdk

WORKDIR /app

COPY --from=builder /builder/app/ .

ENV TZ=UTC

ENTRYPOINT ["java", "-XX:InitialRAMPercentage=75.0", "-XX:MinRAMPercentage=75.0", "-XX:MaxRAMPercentage=75.0", "-XX:+UseCompactObjectHeaders", "-XX:AOTCache=app.aot", "-jar", "app.jar"]
