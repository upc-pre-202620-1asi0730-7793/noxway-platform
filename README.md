# Noxway Platform (Backend)

Backend REST de **Noxway** para trabajadores de turnos diurnos y nocturnos y sus contactos de confianza, construido sobre Java 21, Spring Boot 3.5.7, Maven, Spring Data JPA, Flyway y OpenAPI.

Sigue la arquitectura hexagonal / DDD de 4 capas (`domain`, `application`, `infrastructure`, `interfaces/rest`), CQRS explícito (`commands`, `queries`, `handlers`), ACL entre contextos (`*ContextFacade` / `*ExternalService`), concurrencia optimista (`OptimisticJpaSaveSupport`) y respuestas de error RFC 9457 (`ProblemDetail` con i18n ES/EN).

## Arquitectura y Bounded Contexts

| Contexto | Paquete | Responsabilidad |
|---|---|---|
| IAM (Estructurado) | `iam` | Contratos de autenticación (`/api/v1/auth/*`), estado de seguridad y agregados `AuthSession` / `PasswordReset` |
| Profiles | `profiles` | Perfil del trabajador/contacto, directorio, preferencias de notificación y `TrustLink` |
| Commuting | `commuting` | Trayectos activos, telemetría GPS, demoras, incidentes SOS y detección de retrasos |
| Community | `community` | Puntos de servicio 24h, votación de confianza, zonas de riesgo, avisos y calificación de rutas |
| Wellness | `wellness` | Registro de descanso, metas diarias (`SleepGoal`) y resumen semanal por zona horaria |
| Subscriptions | `subscriptions` | Planes (`ESSENTIAL`, `SENTINEL_PRO`, `FAMILY_CREW`), pagos offline, beneficios, grupo familiar y referidos |
| Moderation | `moderation` | Cola de moderación comunitaria (`APPROVED` / `REJECTED`) y auditoría persistida |

> `shared` centraliza utilidades transversales (`DomainError`, `ApplicationError`, `ActorContextResolver`, `ProblemDetailFromApplicationErrorAssembler`, `OptimisticJpaSaveSupport`).

## IAM Estructurado (Sin Implementación Activa)

Por diseño actual, la autenticación y bloqueo por JWT están **intencionalmente desactivados y estructurados** para permitir probar todos los flujos de negocio de punta a punta:

- `app.security.enabled=false` (`NoSecurityConfiguration` permite acceso abierto).
- `ActorContextResolver` resuelve el actor mediante el encabezado HTTP `X-Actor-Id` (por defecto `00000000-0000-0000-0000-000000000101`).
- `GET /api/v1/auth/status` devuelve `mode: "STRUCTURED_PENDING_IMPLEMENTATION"` y `securityEnabled: false`.
- `POST /api/v1/auth/sign-in`, `sign-up`, `sign-out` y `password-reset` devuelven `501 Not Implemented` con formato RFC 9457 `ProblemDetail`.

### Personas Semilla (`BootstrapData`)

- `00000000-0000-0000-0000-000000000101` — Jorge Luis Huamán (`WORKER`, turno `NIGHT 22:00-06:00`)
- `00000000-0000-0000-0000-000000000102` — Carlos Mendoza (`WORKER`, propietario de plan `FAMILY_CREW`)
- `00000000-0000-0000-0000-000000000201` — Rosa Elena Paredes Morales (`TRUSTED_CONTACT`)
- `00000000-0000-0000-0000-000000000202` — Miguel Ángel Quispe (`TRUSTED_CONTACT`)
- `00000000-0000-0000-0000-000000000901` — Noxway Administrator (`ADMIN`)

## Ejecución Local y Documentación API

```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
```

- API Base: `http://localhost:8080/api/v1`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Healthcheck: `http://localhost:8080/actuator/health`

## Pruebas

```powershell
mvn test
```

Las pruebas automatizadas verifican ciclo de vida de agregados, reglas temporales/horario, adaptadores JPA con `OptimisticJpaSaveSupport`, manejo global RFC 9457 (incluyendo i18n ES/EN) e integración completa de los endpoints REST contra Spring Boot + Flyway + H2.
