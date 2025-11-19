# Bank-App v2.0 — Kubernetes-Native Microservice Banking System (Concise)

This is an educational multi-service banking platform built with **Java 21** and **Spring Boot 3.3.3**, now upgraded to a **Kubernetes-native** architecture.

Each service still runs as an executable JAR, but is deployed as a Kubernetes **Deployment** and communicates via REST + OAuth2 (client-credentials) inside the cluster.  
External access is handled by **Ingress + ingress-nginx**, configuration is managed through **environment variables, ConfigMaps and Secrets**, and each stateful component uses its own **PostgreSQL StatefulSet**.

---

## Modules

### Front-UI Service

- Web UI (Spring MVC + Thymeleaf) for users: registration, login/logout, dashboard, transfers, balance management.
- Authenticates users via OAuth2 Authorization Code flow.

### Auth Service

- OAuth2 Authorization Server (OpenID Connect).
- Issues tokens for users and internal service clients.

### Accounts Service

- User and account management.
- Profile updates, per-currency accounts.

### Cash Service

- Deposits, withdrawals, balance rules.
- Interacts with Accounts; sends notifications.

### Transfer Service

- Validates and executes transfers.
- Communicates with Accounts, Exchange, Blocker, Notifications.

### Notifications Service

- Stores and exposes system/user notifications.

### Exchange Service

- Provides currency conversions.

### Exchange Generation Service

- Generates FX rates and updates Exchange Service.

### Blocker Service

- Fraud / suspicious operation checker.

### Postgres-*

- StatefulSet PostgreSQL 15 instances for each schema: accounts, cash, transfer, notifications.

### Infrastructure Charts

- Namespaces, ingress-nginx, registry secret `regcred`.

---

## Technologies

- Java 21  
- Spring Boot 3.3.3  
- Spring Security 6  
- Springdoc OpenAPI 2.5  
- PostgreSQL 15  
- Maven (multi-module)  
- Kubernetes (Deployments, Services, StatefulSets, Ingress)  
- Helm (umbrella + per-service charts + shared template)  
- Ingress-NGINX  
- OAuth2 / OIDC Authorization Server  
- `platform-http-client-starter`  
- `platform-security-starter`  
- JUnit 5  
- Docker (Dockerfile per service)  

---

## Public API Summary

### Ingress Routes (external, dev)

- `/ui/**` → Front-UI Service  
- `/auth/**` → Auth Service  

### Backend Routes (internal only)

- `/api/v1/users/**` → Accounts Service  
- `/api/v1/cash/**` → Cash Service  
- `/api/v1/transfers/**` → Transfer Service  
- `/api/v1/notifications/**` → Notifications Service  
- `/api/v1/exchange/**` → Exchange Service  
- `/api/v1/blocker/**` → Blocker Service  

### OAuth2 Auth Service Endpoints

- `/auth/oauth2/token` (client credentials)  
- `/auth/.well-known/openid-configuration`  
- `/auth/authorize` and `/auth/token` (Authorization Code flow)  

---

## Configuration (Env, ConfigMap, Secret)

### Shared DB Credentials

- `POSTGRES_USER`  
- `POSTGRES_PASSWORD`  

### JWT / Auth

- `SPRING_SECURITY_OAUTH2_AUTHORIZATIONSERVER_ISSUER_URI`  
- `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI`  

### OAuth2 Client Credentials per Service

- `OAUTH2_CLIENT_ID`  
- `OAUTH2_CLIENT_SECRET`  

### Per-service Configs (ConfigMap examples)

**Blocker:**  
- `BLOCKER_THRESHOLD`  
- `BLOCKER_DENY_PERCENT`  

**Exchange:**  
- `EXCHANGE_SCALE`  
- `EXCHANGE_ROUNDING_MODE`  
- `EXCHANGE_SUPPORTED`  

**Exchange-Generator:**  
- `EXGEN_SUPPORTED`  
- `EXGEN_FIXED_RATE_MS`  
- `EXGEN_DRIFT_PCT`  
- `EXGEN_EXCHANGE_SERVICE_ID`  
- `INITIAL_TO_RUB_USD`  
- `INITIAL_TO_RUB_CNY`  

**General:**  
- `SPRING_PROFILES_ACTIVE`  
- `MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE`  

---

## Database Setup

### Kubernetes

- Four StatefulSets: `postgres-accounts`, `postgres-cash`, `postgres-transfer`, `postgres-notifications`.  
- Same database name: `bank`.  
- Flyway runs migrations automatically.

### Migrations

- `accounts-service/db/migration/V1__init_accounts_schema.sql`  
- `cash-service/db/migration/V1__init_cash_schema.sql`  
- `transfer-service/db/migration/V1__init_transfer_schema.sql`  
- `notifications-service/db/migration/V1__init_notifications_schema.sql`  

### Docker-compose (Legacy Local Mode)

- One PostgreSQL container with all four schemas.  
- Root DB and schemas can be initialized from `init/init_main_schema.sql`.  

---

## Kubernetes Deployment (Helm + Minikube)

1. Start Minikube.  
2. Create namespaces: `dev`, `test`, `prod`.  
3. Install ingress-nginx (chart: `infra-ingress-nginx`).  
4. Create private registry secret `regcred` (chart: `infra-registry`).  
5. Build and push images to Docker Hub.  
6. Deploy PostgreSQL StatefulSets (`postgres-*`).  
7. Deploy all application charts: `auth`, `accounts`, `cash`, `transfer`, `notifications`, `exchange`, `exgen`, `blocker`, `front-ui`.  
8. Ingress exposes only:
   - `/ui/**` → `front-ui`  
   - `/auth/**` → `auth-service`  

---

## Docker Deployment (Legacy Local Stack)

```bash
docker compose build
docker compose up -d
docker compose logs -f
```

- Front-UI → <http://localhost:8088>  
- Auth Service → <http://localhost:8080>  
- PostgreSQL → `localhost:5432`  

---

## Development Notes

- No Config Server, no Eureka.  
- All configuration via Helm (values → ConfigMap/Secret).  
- Discovery via Kubernetes DNS (`http://accounts:8080`).  
- HTTP clients configured by `platform-http-client-starter`.  
- Shared resource-server config via `platform-security-starter`.  
- Flyway replaces all SQL init scripts.  
- Gateway module removed in v2.0 (Ingress-NGINX acts as gateway).  

---

## Testing

```bash
mvn test
```

- JUnit 5 + Spring Boot Test.  
- Helm tests via:

```bash
helm test <release> -n <namespace>
```
