FROM eclipse-temurin:21-jdk AS build
WORKDIR /wrk

COPY ./pom.xml ./pom.xml
COPY ./blocker-service/pom.xml ./blocker-service/pom.xml

COPY .mvn/ .mvn/
COPY mvnw .
RUN chmod +x mvnw && ./mvnw -q -DskipTests dependency:go-offline

COPY ./blocker-service/src ./blocker-service/src

RUN ./mvnw -q -DskipTests -pl blocker-service -am package

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /wrk/blocker-service/target/*.jar /app/app.jar

RUN useradd -r -u 1001 appuser && chown -R appuser /app
USER appuser

ENV TZ=UTC \
    SPRING_PROFILES_ACTIVE=default \
    JAVA_TOOL_OPTIONS="-XX:+UseZGC -XX:+HeapDumpOnOutOfMemoryError -XX:+ExitOnOutOfMemoryError"

ENTRYPOINT ["java","-jar","/app/app.jar"]
