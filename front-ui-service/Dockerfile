FROM eclipse-temurin:21-jdk AS build
WORKDIR /wrk

COPY . .

RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

RUN ./mvnw -q -DskipTests -pl front-ui-service -am clean package


FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /wrk/front-ui-service/target/front-ui-service-*.jar /app/app.jar

RUN useradd -r -u 1001 appuser && chown -R appuser /app
USER appuser

ENV TZ=UTC \
    SPRING_PROFILES_ACTIVE=default \
    JAVA_TOOL_OPTIONS="-XX:+UseZGC -XX:+HeapDumpOnOutOfMemoryError -XX:+ExitOnOutOfMemoryError"

ENTRYPOINT ["java","-jar","/app/app.jar"]
