FROM eclipse-temurin:21-jdk AS build
WORKDIR /wrk

COPY ./pom.xml ./pom.xml
COPY ./accounts-service/pom.xml ./accounts-service/pom.xml

COPY .mvn/ .mvn/
COPY mvnw .
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

COPY ./accounts-service/src ./accounts-service/src

COPY ./platform-contracts/pom.xml ./platform-contracts/pom.xml
COPY ./platform-contracts/src ./platform-contracts/src
COPY ./platform-notifications-client-starter/pom.xml ./platform-notifications-client-starter/pom.xml
COPY ./platform-notifications-client-starter/src ./platform-notifications-client-starter/src

RUN ./mvnw -q -DskipTests -pl accounts-service -am clean package

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /wrk/accounts-service/target/accounts-service-*.jar /app/app.jar

RUN useradd -r -u 1001 appuser && chown -R appuser /app
USER appuser

ENV TZ=UTC JAVA_TOOL_OPTIONS="-XX:+UseZGC -XX:+HeapDumpOnOutOfMemoryError -XX:+ExitOnOutOfMemoryError" 

ENTRYPOINT ["java","-jar","/app/app.jar"]
