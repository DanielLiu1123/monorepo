FROM eclipse-temurin:25-jdk AS builder

WORKDIR /builder

COPY build/libs/app.jar app.jar

RUN java -Djarmode=tools -jar app.jar extract --destination app

WORKDIR /builder/app

RUN java -XX:+UseCompactObjectHeaders -XX:AOTCacheOutput=app.aot -jar app.jar

FROM eclipse-temurin:25-jdk

WORKDIR /app

COPY --from=builder /builder/app/ .

ARG JAVA_OPTS="-XX:InitialRAMPercentage=75.0 -XX:MinRAMPercentage=75.0 -XX:MaxRAMPercentage=75.0"

ENV TZ=UTC

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -XX:+UseCompactObjectHeaders -XX:AOTCache=app.aot -jar app.jar"]
