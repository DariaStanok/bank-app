FROM eclipse-temurin:21-jdk AS build
WORKDIR /wrk

COPY ./pom.xml ./pom.xml
COPY ./discovery-service/pom.xml ./discovery-service/pom.xml

COPY .mvn/ .mvn/
COPY mvnw .
RUN chmod +x mvnw && ./mvnw -q -DskipTests dependency:go-offline

COPY ./discovery-service/src ./discovery-service/src

RUN ./mvnw -q -DskipTests -pl discovery-service -am package

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /wrk/discovery-service/target/*.jar /app/app.jar

RUN useradd -r -u 1001 appuser && chown -R appuser /app
USER appuser

ENV TZ=UTC \
    SPRING_PROFILES_ACTIVE=default \
    JAVA_TOOL_OPTIONS="-XX:+UseZGC -XX:+HeapDumpOnOutOfMemoryError -XX:+ExitOnOutOfMemoryError"

ENTRYPOINT ["java","-jar","/app/app.jar"]
