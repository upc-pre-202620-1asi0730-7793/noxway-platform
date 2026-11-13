# Cómo comprobar el backend

## 1. Pruebas automáticas

Con JDK 21+, abre la terminal en la raíz del proyecto.

Windows PowerShell:

```powershell
.\mvnw.cmd test
```

macOS/Linux:

```bash
./mvnw clean verify
```

El resultado esperado es `BUILD SUCCESS` y 21 pruebas sin fallos ni errores (`AggregateLifecycleTest`, `DomainRulesTest`, `GlobalExceptionHandlerTest`, `RepositoryAdaptersTest` y `NoxwayPlatformIntegrationTest`) ejecutadas con Spring Boot, Flyway y repositorios reales sobre H2 en modo PostgreSQL.

## 2. Arranque local

Ejecuta `NoxwayApplication` con perfil `local`:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

Visita `http://localhost:8080/actuator/health`: debe mostrar `{"status":"UP"}`. La base H2 local persiste en `data/` y `BootstrapData` siembra planes, beneficios, cuentas de prueba y datos iniciales idempotentes.

## 3. Prueba manual con Swagger o Postman

Abre `http://localhost:8080/swagger-ui.html`. En el estado actual (`app.security.enabled=false`), las peticiones operativas identifican al actor usando la cabecera `X-Actor-Id` (por defecto `00000000-0000-0000-0000-000000000101`).

1. Verifica `GET /api/v1/auth/status`: responde `200` indicando modo `STRUCTURED_PENDING_IMPLEMENTATION`. Los endpoints `/api/v1/auth/sign-*` responden `501` mientras IAM permanezca diferido.
2. Consulta `GET /api/v1/accounts/directory` para ver las cuentas sembradas (`0101`, `0102`, `0201`, `0202`, `0901`).
3. Crea un trayecto con `POST /api/v1/commutes` (ETA futuro), envía telemetría a `/api/v1/commutes/{id}/telemetry` y confirma llegada con `/api/v1/commutes/{id}/arrive`.
4. Propón un punto de servicio en `POST /api/v1/community/services`, apruébalo cambiando `X-Actor-Id` a Admin (`...0901`) en `/api/v1/moderation/reports/{id}/decision`, y vota `+1`, `0` (retirar) o `-1`.
5. Solicita una suscripción en `POST /api/v1/subscriptions` y concilia el pago offline como Admin (`...0901`) en `/api/v1/payments/{id}/confirm-offline` usando una referencia única.
6. Importa `docs/noxway.postman_collection.json` en Postman para probar estos flujos.

## 4. Arquitectura de referencia

La solución sigue la arquitectura de 4 capas (`domain`, `application`, `infrastructure`, `interfaces`), CQRS ligero (`CommandService`/`QueryService`), ensambladores REST/JPA dedicados, mapeo JPA aislado de agregados de dominio, bloqueo optimista vía `OptimisticJpaSaveSupport`, migraciones Flyway (`V1`–`V4`) y manejo global de errores RFC 9457 i18n.
