# Kubernetes-Native Microservice Banking System with Apache Kafka

**Bank-App v3.0**

Bank-App v3.0 is a production-grade microservice banking platform built
with\
**Java 21**, **Spring Boot 3.3.3**, **Spring Security 6**, fully adapted
for\
**Kubernetes-native** deployment and extended with two Apache Kafka
streams:

-   **bank.notifications** --- system notification events\
-   **bank.exchange-rates** --- real-time FX rate updates

Each microservice is packaged as an **executable JAR**, deployed as a\
**Kubernetes Deployment**, and uses **PostgreSQL StatefulSets**,
**Ingress-NGINX**,\
**Helm**, **Kafka**, and **Jenkins CI/CD**.

------------------------------------------------------------------------

## Architecture Overview

### Key Features

-   Pure **Kubernetes-native architecture**\
    (no Spring Cloud Discovery / Gateway / Config Server)
-   **Apache Kafka** as the transport for:
    -   Notifications (Accounts / Cash / Transfer → Notifications)
    -   FX rates (Exchange-Generator → Exchange)
-   **Helm-based** deployment (umbrella chart + per-service charts)
-   Fully automated **CI/CD via Jenkins**
-   **OAuth2 Authorization** via dedicated Auth-Service
-   **Isolated data stores**: PostgreSQL StatefulSet per service

------------------------------------------------------------------------

## Microservices Overview

### Auth Service

-   OAuth2 Authorization Server\
-   Client Credentials flow for internal services\
-   Authorization Code flow for Front-UI\
-   Issues JWT tokens

### Front-UI Service

Thymeleaf-based UI: - Login / Logout\
- Dashboard\
- Transfers\
- Account settings

### Accounts Service

-   Users / Profiles / Accounts\
-   CRUD with domain rules\
-   Kafka producer (notifications)

### Cash Service

-   Deposits / Withdrawals\
-   Idempotency\
-   Coordination with Accounts\
-   Optional Blocker integration\
-   Kafka producer (notifications)

### Transfer Service

-   Money transfers\
-   FX conversion via Exchange\
-   Blocker checks\
-   Idempotency\
-   Kafka producer (notifications)

### Notifications Service

-   Kafka consumer (`bank.notifications`)\
-   Stores notifications in PostgreSQL\
-   REST API to fetch recent notifications

### Exchange Service

-   FX conversion support\
-   Kafka consumer (`bank.exchange-rates`)\
-   Stores latest FX rates in memory

### Exchange-Generator Service

-   Generates FX rates & volatility\
-   Publishes updates every N milliseconds\
-   Kafka producer (`bank.exchange-rates`)

### Blocker Service

-   Anti-fraud / suspicious-operation checks\
-   REST client invoked by Cash/Transfer

------------------------------------------------------------------------

## Kafka Integration

### Topics

  -----------------------------------------------------------------------------------
  Topic                     Description          Producer(s)          Consumer(s)
  ------------------------- -------------------- -------------------- ---------------
  **bank.notifications**    system notifications Accounts, Cash,      Notifications
                                                 Transfer             

  **bank.exchange-rates**   FX rate updates      Exchange-Generator   Exchange
  -----------------------------------------------------------------------------------

### Shared Kafka ENV variables

    APP_KAFKA_BOOTSTRAP_SERVERS=infra-kafka.default.svc.cluster.local:9092
    APP_KAFKA_CONSUMER_GROUP_ID=<service-specific>
    APP_KAFKA_NOTIFICATIONS_TOPIC=bank.notifications
    APP_KAFKA_EXCHANGE_RATES_TOPIC=bank.exchange-rates

------------------------------------------------------------------------

## Databases (PostgreSQL)

Each service uses a dedicated StatefulSet:

-   `postgres-accounts`
-   `postgres-cash`
-   `postgres-transfer`
-   `postgres-notifications`

Migrations are handled by **Flyway**.

------------------------------------------------------------------------

## Kubernetes Deployment

### Namespaces

-   `dev`
-   `test`
-   `prod`

### Ingress Routing

  Path         Service
  ------------ --------------
  `/ui/**`     front-ui
  `/auth/**`   auth-service

Backend services accessible via DNS:

    http://accounts:8080
    http://cash:8080
    http://transfer:8080
    http://notifications:8080
    http://exchange:8080
    http://blocker:8080

------------------------------------------------------------------------

## Shared Infrastructure

-   **Ingress-NGINX**
-   `regcred` --- Docker registry pull secret\
-   **Helm library chart: `service-template`**
-   **Kafka (Bitnami chart)**

------------------------------------------------------------------------

## Configuration Management

### Secrets

-   `OAUTH2_CLIENT_ID`, `OAUTH2_CLIENT_SECRET`
-   PostgreSQL credentials
-   Kafka Bootstrap servers
-   `EXGEN_SEED_SECRET_BASE64` (FX generation seed)

### Example ConfigMaps

#### Exchange Service

    EXCHANGE_SCALE
    EXCHANGE_ROUNDING_MODE
    EXCHANGE_SUPPORTED

#### Exchange-Generator

    EXGEN_SUPPORTED
    EXGEN_FIXED_RATE_MS
    EXGEN_DRIFT_PCT
    INITIAL_TO_RUB_USD
    INITIAL_TO_RUB_CNY

------------------------------------------------------------------------

## Helm Charts

### Directory Structure

    charts/
      umbrella/
      accounts/
      auth/
      cash/
      transfer/
      notifications/
      exchange/
      exchange-generator/
      blocker/
      front-ui/
      infra-kafka/
      _service-template/

### Deploy Umbrella

``` bash
helm upgrade --install bank-app-dev charts/umbrella \
  -n dev \
  -f charts/umbrella/values-dev.yaml
```

------------------------------------------------------------------------

## CI/CD Pipeline (Jenkins)

### Stages

1.  Checkout\
2.  Maven build\
3.  Docker build for all services\
4.  Docker push\
5.  Helm deploy
    -   Kafka\
    -   Umbrella\
6.  (Optional) Helm tests

### Required Credentials

-   Docker Hub credentials\
-   Kubeconfig\
-   Optional GitHub token

------------------------------------------------------------------------

## Testing

### Unit & Integration Tests

-   JUnit 5\
-   Spring Boot Test\
-   Flyway validation\
-   WebTestClient / MockMvc\
-   Optional: spring-kafka-test

### Helm Tests

``` bash
helm test bank-app-dev -n dev
```

------------------------------------------------------------------------

## Local Development (Docker Compose)

Legacy (for development only):

``` bash
docker compose up -d
```

Access: - Front-UI → http://localhost:8088\
- Auth → http://localhost:8080\
- PostgreSQL → localhost:5432

------------------------------------------------------------------------

## Deployment to Minikube

### 1. Start cluster

``` bash
minikube start --memory=6g --cpus=4
```

### 2. Enable ingress

``` bash
minikube addons enable ingress
```

### 3. Create namespaces

``` bash
kubectl create ns dev
kubectl create ns test
kubectl create ns prod
```

### 4. Add registry secret

``` bash
kubectl create secret docker-registry regcred ...
```

### 5--7. Deploy PostgreSQL, build/push images, deploy umbrella

``` bash
helm upgrade --install bank-app-dev charts/umbrella -n dev
```

### 8. Run tests

``` bash
helm test bank-app-dev -n dev
```
