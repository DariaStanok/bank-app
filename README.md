# Bank-App v2.0 --- Kubernetes-Native Microservice Banking System

Bank-App v2.0 is a modular microservice banking platform implemented
with **Java 21**, **Spring Boot 3.3.3**, and **Spring Security 6**,
fully migrated to a **Kubernetes-native infrastructure** using Helm,
Ingress-NGINX, ConfigMaps/Secrets, StatefulSets, and CI/CD via Jenkins.

Each service is packaged as an **executable JAR** and deployed as a
**Kubernetes Deployment**.\
Service-to-service communication uses **REST + OAuth2 client-credentials
flow**.\
All persistent storage is provided by **PostgreSQL StatefulSets**.

------------------------------------------------------------------------

## Architecture Overview

### Key Features

-   Kubernetes workloads managed via **Helm** (umbrella chart +
    per-service charts)
-   Service discovery handled by **Kubernetes Services** (DNS names:
    `accounts`, `cash`, etc.)
-   **Ingress-NGINX** serves as the API Gateway
-   External configuration via **ConfigMaps** and **Secrets**
-   Built-in **OAuth2 Authorization Server**
-   Fully automated CI/CD pipeline using **Jenkins**

### Removed Components from v1.0

The following are **no longer used**: - Spring Cloud Config Server\
- Eureka Discovery Service\
- Spring Cloud Gateway\
- Consul / ZooKeeper

All their responsibilities are now handled by **native Kubernetes
features**.

------------------------------------------------------------------------

## Microservices Overview

### **Auth Service**

-   OAuth2 Authorization Server\
-   Supports Authorization Code & Client Credentials flows\
-   Issues JWT tokens for internal services and front-end users

### **Front-UI Service**

-   Spring MVC + Thymeleaf\
-   Provides UI for login, registration, dashboard, transfers

### **Accounts Service**

-   Manages user profiles\
-   Maintains bank accounts (per currency)\
-   Validates domain rules

### **Cash Service**

-   Handles deposits and withdrawals\
-   Integrates with Accounts & Notifications

### **Transfer Service**

-   Manages P2P transfers\
-   Uses Exchange, Blocker, and Notifications services\
-   Supports complex transactional logic

### **Notifications Service**

-   Stores and retrieves system & user notifications

### **Exchange Service**

-   Provides FX conversion rates

### **Exchange-Generation Service**

-   Periodically updates FX rates for Exchange Service

### **Blocker Service**

-   Fraud and suspicious operations checker

------------------------------------------------------------------------

## Databases (PostgreSQL)

Each microservice with persistence has its own database:

-   `postgres-accounts`
-   `postgres-cash`
-   `postgres-transfer`
-   `postgres-notifications`

Each DB is deployed as a **StatefulSet**, migrations run via **Flyway**.

------------------------------------------------------------------------

## Kubernetes Deployment

### Namespaces

-   `dev`
-   `test`
-   `prod`

Each with its own values, secrets, and configurations.

### Ingress Routing

  Path         Service
  ------------ ------------------
  `/ui/**`     front-ui-service
  `/auth/**`   auth-service

Internal backend APIs resolve via DNS:

    http://accounts:8080
    http://cash:8080
    http://transfer:8080
    http://notifications:8080
    http://exchange:8080
    http://blocker:8080

### Shared Infrastructure

-   Ingress controller: **ingress-nginx**
-   Registry secret: **regcred**
-   Helm library chart: **service-template**

------------------------------------------------------------------------

## Configuration Management

### Secrets

Environment variables stored in Kubernetes Secrets: - `POSTGRES_USER`,
`POSTGRES_PASSWORD` - `OAUTH2_CLIENT_ID`, `OAUTH2_CLIENT_SECRET` -
`SPRING_SECURITY_OAUTH2_AUTHORIZATIONSERVER_ISSUER_URI` -
`SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI`

### ConfigMaps (Examples)

#### Exchange Service

-   `EXCHANGE_SCALE`
-   `EXCHANGE_ROUNDING_MODE`
-   `EXCHANGE_SUPPORTED`

#### Blocker Service

-   `BLOCKER_THRESHOLD`
-   `BLOCKER_DENY_PERCENT`

#### Exchange-Generation

-   `EXGEN_SUPPORTED`
-   `EXGEN_FIXED_RATE_MS`
-   `EXGEN_DRIFT_PCT`
-   `EXGEN_EXCHANGE_SERVICE_ID`
-   `INITIAL_TO_RUB_USD`
-   `INITIAL_TO_RUB_CNY`

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
      _service-template/

### Umbrella Deployment

    helm upgrade --install bank-app-dev charts/umbrella   -n dev   -f charts/umbrella/values-dev.yaml

### Helm Tests

    helm test <release> -n <namespace>

------------------------------------------------------------------------

## CI/CD Pipeline (Jenkins)

### Pipeline Steps

1.  Checkout Git repository\
2.  Maven build & unit tests\
3.  Docker build for microservices\
4.  Push images to registry\
5.  Deploy via Helm to `dev/test/prod`\
6.  Run Helm tests

### Credentials

-   Docker Hub: `dockerhub-creds`
-   Kubernetes: kubeconfig available on Jenkins node

------------------------------------------------------------------------

## Testing

### Unit & Integration tests

-   JUnit 5\
-   Spring Boot Test\
-   Flyway migrations validated automatically

### Helm Tests

    helm test <release> -n dev

------------------------------------------------------------------------

## Local Development (Docker Compose)

Legacy local mode:

    docker compose up -d

### Access:

-   Front-UI → http://localhost:8088\
-   Auth → http://localhost:8080\
-   PostgreSQL → localhost:5432

------------------------------------------------------------------------

## How to Deploy to Minikube

### 1. Start Minikube

``` bash
minikube start --memory=6g --cpus=4
```

### 2. Install Ingress-NGINX

``` bash
minikube addons enable ingress
```

### 3. Create namespaces

``` bash
kubectl create ns dev
kubectl create ns test
kubectl create ns prod
```

### 4. Create registry secret

``` bash
kubectl create secret docker-registry regcred ...
```

### 5. Deploy PostgreSQL StatefulSets

### 6. Build & push Docker images

### 7. Deploy umbrella chart

``` bash
helm upgrade --install bank-app-dev charts/umbrella -n dev
```

### 8. Run tests

``` bash
helm test bank-app-dev -n dev
```

------------------------------------------------------------------------