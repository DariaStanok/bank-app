FROM eclipse-temurin:21-jdk AS build
WORKDIR /wrk

COPY ./pom.xml ./pom.xml
COPY ./config-server/pom.xml ./config-server/pom.xml

COPY .mvn/ .mvn/
COPY mvnw .
RUN chmod +x mvnw && ./mvnw -q -DskipTests dependency:go-offline

COPY ./config-server/src ./config-server/src

RUN ./mvnw -q -DskipTests -pl config-server -am package

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /wrk/config-server/target/*.jar /app/app.jar

RUN useradd -r -u 1001 appuser && chown -R appuser /app
USER appuser

ENV TZ=UTC \
    SPRING_PROFILES_ACTIVE=default \
    JAVA_TOOL_OPTIONS="-XX:+UseZGC -XX:+HeapDumpOnOutOfMemoryError -XX:+ExitOnOutOfMemoryError"

ENTRYPOINT ["java","-jar","/app/app.jar"]

