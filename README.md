## Bank-App v1.0 — Modular Microservice Banking System (Concise)

This is an educational multi-service banking platform built with **Java 21**, **Spring Boot 3.3.3**, and **Spring Cloud 2023.0.3**.  
It demonstrates modern cloud-native architecture with centralized configuration, service discovery, secure inter-service communication, and user authentication.

Each service runs as an **executable JAR** and communicates via **REST + OAuth2 (client-credentials)** through a unified **API Gateway**.  
The system is fully Dockerized and connected through **Spring Cloud Eureka** and **Config Server** (native backend).

---

## Modules

| Module | Responsibilities | Tech Stack |
|:--------|:-----------------|:------------|
| **Front-UI Service** | Web UI (Spring MVC + Thymeleaf) for users: registration, login/logout, dashboard, transfers, balance management. | Spring MVC, Thymeleaf, OAuth2 Client |
| **API Gateway** | Single entry point for all HTTP traffic; routes to services, aggregates Swagger, handles internal OAuth2. | Spring Cloud Gateway |
| **Auth Service** | OAuth2 Authorization Server (OpenID Connect); issues tokens for users and internal clients. | Spring Security OAuth2 Authorization Server |
| **Accounts Service** | User and account management; profile updates, account creation, per-currency accounts. | Spring Boot, JPA, PostgreSQL |
| **Cash Service** | Handles deposits and withdrawals, enforces balance rules, interacts with Accounts. | Spring Boot, JPA, PostgreSQL |
| **Transfer Service** | Manages inter-account transfers, transactional integrity, currency checks. | Spring Boot, JPA, PostgreSQL |
| **Notifications Service** | Stores and exposes user notifications and system events. | Spring Boot, JPA, PostgreSQL |
| **Discovery Service** | Eureka Server — service registry for all modules. | Spring Cloud Netflix Eureka Server |
| **Config Server** | Centralized configuration via `config-repo/` (native filesystem). | Spring Cloud Config Server |

---

## Technologies

- **Language / Runtime**: Java 21  
- **Frameworks**: Spring Boot 3.3.3 · Spring Cloud 2023.0.3 · Spring Security 6 · Springdoc OpenAPI 2.5  
- **Persistence**: PostgreSQL (schema-per-service)  
- **Build System**: Maven (multi-module)  
- **Discovery & Config**: Eureka + Config Server (native)  
- **API Gateway**: Spring Cloud Gateway (Netty)  
- **Auth**: OAuth2 / OIDC Authorization Server  
- **Testing**: JUnit 5 · Testcontainers · Spring Boot Test  
- **Containerization**: Docker (multi-service `docker-compose`)  
- **Mapping / Utils**: ModelMapper · Lombok  

---

## Public API Summary

### Gateway (API Aggregation)
| Route | Description |
|:------|:-------------|
| `/api/v1/users/**` → Accounts Service | User & account operations |
| `/api/v1/cash/**` → Cash Service | Deposits / withdrawals |
| `/api/v1/transfers/**` → Transfer Service | Transfers between accounts |
| `/api/v1/notifications/**` → Notifications Service | Notifications and system messages |
| `/auth/**` → Auth Service | OAuth2 endpoints |
| `/ui/**` → Front-UI | User-facing web pages |

### OAuth2 Endpoints (Auth Service)
| Endpoint | Description |
|:----------|:-------------|
| `/auth/oauth2/token` | Issue token (client credentials grant) |
| `/auth/.well-known/openid-configuration` | OIDC metadata |
| `/auth/authorize` / `/auth/token` | Authorization Code flow for Front-UI |

---

## Configuration (env vars / placeholders)

| Variable | Purpose |
|:----------|:---------|
| `POSTGRES_USER` / `POSTGRES_PASSWORD` | Shared DB credentials |
| `AUTH_ISSUER` | Issuer URI for JWT (e.g. `http://localhost:8080/auth`) |
| `GATEWAY_CLIENT_SECRET` | Secret for Gateway OAuth2 client |
| `SPRING_CLOUD_CONFIG_URI` | Config Server endpoint (default `http://config-server:8888`) |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | Eureka registry endpoint |

---

## Database Setup

- Single PostgreSQL instance with separate schemas per service:  
  `accounts`, `cash`, `transfer`, `notifications`.  
- Schema initialization via `src/main/resources/db/init_<service>_schema.sql`.
- Root DB and schemas are created automatically through  
  `init/init_main_schema.sql` mounted to `/docker-entrypoint-initdb.d` in Postgres.

---

## Docker Deployment

All modules are built and run via a unified `docker-compose.yml`:

```bash
docker compose build        # Build all service images
docker compose up -d        # Start full system
docker compose ps           # Check running containers
docker compose logs -f      # Follow logs
```

Key exposed ports:

| Service | Port |
|:---------|:------|
| Config Server | 8888 |
| Discovery (Eureka) | 8761 |
| API Gateway | 8080 |
| Front-UI | 8088 |
| Auth Service | 8090 |
| PostgreSQL | 5432 |

After startup:
- Config Server → [http://localhost:8888/actuator/health](http://localhost:8888/actuator/health)
- Eureka Dashboard → [http://localhost:8761](http://localhost:8761)
- Swagger UI (aggregated) → [http://localhost:8080/swagger-ui](http://localhost:8080/swagger-ui)

---

## Development Notes

- Each service uses **Config Server** and **Eureka** for bootstrapping.  
  No local `application.yml` duplication needed — use `config-repo/`.
- OAuth2 flows:
  - Internal services → client credentials.
  - Front-UI users → authorization code + PKCE.
- Database schema initialization occurs automatically on container start.
- Common enums and DTOs are located in `platform-contracts/`.
- Notifications client starter and exception starter are shared libraries (`platform-*`).

---

## Testing

- **Unit / Integration**: JUnit 5 + Spring Boot Test.  
- **Containers**: PostgreSQL via Testcontainers (per service).  
- **E2E tests** (optional): can validate flows via Gateway using generated tokens.  
- Run all tests:
  ```bash
  mvn test
  ```
