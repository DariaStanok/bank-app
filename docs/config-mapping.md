# config-mapping
# 1. Общая концепция переноса

### Было:
- Spring Cloud Config Server
- Spring Cloud Gateway (lb://)
- Eureka Discovery
- sql.init
- Локальные `application.yml`
- docker-compose.env

### Стало:
- Helm values.yaml → источник конфигов
- ConfigMap → обычные настройки сервисов
- Secrets → пароли, логины, чувствительные данные
- Deployment env → передача переменных в контейнер
- Flyway → миграции V1__*.sql
- K8s Gateway API + HTTPRoute → маршрутизация
- Никакого Config Server и Eureka

---

# 2. ENV-файл (источник чувствительных значений)

```env
POSTGRES_USER=user
POSTGRES_PASSWORD=password
POSTGRES_DB=bank

GATEWAY_CLIENT_SECRET=gateway-secret
AUTH_ISSUER=http://localhost:8080/auth

# Mapping по каждому сервису

## accounts-service  
Порт: `8081`

### Старые ключи (Config Server / локальный YAML)

- spring.datasource.url=jdbc:postgresql://postgres:5432/bank?currentSchema=accounts  
- spring.datasource.username=${POSTGRES_USER}  
- spring.datasource.password=${POSTGRES_PASSWORD}  
- spring.sql.init.mode=always  
- spring.sql.init.schema-locations=classpath:db/init_accounts_schema.sql  
- spring.jpa.hibernate.ddl-auto=none  
- spring.security.oauth2.resourceserver.jwt.issuer-uri=${AUTH_ISSUER}  
- management.endpoints.web.exposure.include=health,info  

### Новая схема (Helm → K8s → env/ConfigMap/Secret)

##НЕсекретное (env / ConfigMap):

-SPRING_DATASOURCE_URL
-SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI
-MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE
-SPRING_PROFILES_ACTIVE (если нужно)
-server.port → через containerPort и service.port = 8081

##Секретное (Secret):

-SPRING_DATASOURCE_USERNAME
-SPRING_DATASOURCE_PASSWORD
-OAUTH2_CLIENT_ID 
-OAUTH2_CLIENT_SECRET
 
## Flyway:
- db/migration/V1__init_accounts_schema.sql

---

## cash-service  
Порт: `8085`

### Старые ключи
- datasource.url=jdbc:postgresql://postgres:5432/bank?currentSchema=cash  
- datasource.username/password  
- sql.init.mode/schema-locations  
- jpa.hibernate.ddl-auto  
- issuer-uri  

### Новая схема
- Secret: datasource URL/USER/PASS  
- ConfigMap: issuer-uri  
- Flyway: V1__init_cash_schema.sql  


---

## transfer-service  
Порт: `8083`

### Старые ключи
- datasource.url=...currentSchema=transfer  
- issuer-uri  

### Новая схема
- Secret: datasource  
- ConfigMap: issuer-uri  
- Flyway: V1__init_transfer_schema.sql  


---

## notifications-service  
Порт: `8087`

### Старые ключи
- datasource.url=...currentSchema=notifications  
- datasource.username/password  
- sql.init  
- jpa.hibernate.ddl-auto  
- issuer-uri  

### Новая схема
- Secret: datasource  
- ConfigMap: issuer-uri  
- Flyway: V1__init_notifications_schema.sql  


---

## front-ui-service  
Порт: `8088`

### Старые ключи
- spring.security.oauth2.client.registration.front-ui.client-id  
- spring.security.oauth2.client.registration.front-ui.scope  
- spring.security.oauth2.client.provider.as.issuer-uri  
- server.forward-headers-strategy  

### Новая схема
**Secrets:**
- FRONT_UI_CLIENT_ID  
- FRONT_UI_CLIENT_SECRET  

**ConfigMap:**
- AUTH_ISSUER  
- FORWARD_HEADERS_STRATEGY  


---

## auth-service  
Порт: `8090`

### Старые ключи
- authorization-server.issuer=${AUTH_ISSUER}  
- management endpoints exposure  

### Новая схема
- ConfigMap: AUTH_ISSUER  


---

## blocker-service  
Порт: `8086`

### Старые ключи
- blocker.threshold=10000.00  
- blocker.deny-percent=5  

### Новая схема
- ConfigMap: BLOCKER_THRESHOLD  
- ConfigMap: BLOCKER_DENY_PERCENT  


---

## exchange-service  
Порт: `8084`

### Старые ключи
- exchange.scale=6  
- exchange.rounding-mode=HALF_UP  
- exchange.supported=[RUB, USD, CNY]  

### Новая схема
ConfigMap:
- EXCHANGE_SCALE  
- EXCHANGE_ROUNDING_MODE  
- EXCHANGE_SUPPORTED  


---

## exchange-generation-service  
Порт: `8082`

### Старые ключи
- exgen.supported  
- exgen.fixed-rate-ms  
- exgen.drift-pct  
- exgen.exchange-service-id  
- exgen.initial-to-rub.USD  
- exgen.initial-to-rub.CNY  

### Новая схема
ConfigMap:
- EXGEN_SUPPORTED  
- EXGEN_FIXED_RATE_MS  
- EXGEN_DRIFT_PCT  
- EXGEN_EXCHANGE_SERVICE_ID  
- INITIAL_TO_RUB_USD  
- INITIAL_TO_RUB_CNY  

