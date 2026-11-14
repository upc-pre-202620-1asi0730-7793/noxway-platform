# Noxway Platform — Backend REST API

**Noxway Platform** es la API RESTful de **Noxway** para trabajadores de turnos nocturnos y diurnos y su red de contactos de confianza, construida bajo los principios de **Domain-Driven Design (DDD)**, **Arquitectura Hexagonal / 4 Capas** y **CQRS**. Desarrollada con **Java 21**, **Spring Boot 3.5.7**, **Spring Data JPA**, **Flyway** y **OpenAPI 3**, gestiona desplazamientos monitoreados con telemetría GPS e incidentes SOS, directorio colaborativo 24h con OpenStreetMap, avisos comunitarios geolocalizados, recuperación de sueño, suscripciones con pagos offline y moderación.

---

## Tech Stack

- **Lenguaje:** Java 21
- **Framework:** Spring Boot 3.5.7 (`spring-boot-starter-web`, `data-jpa`, `validation`, `security`, `mail`, `actuator`)
- **Base de Datos:** PostgreSQL 16 (Producción / CI) y H2 Database en modo PostgreSQL persistido en disco (`./data/noxway` en perfil `local`)
- **Migraciones SQL:** Flyway (`V1` a `V5`) con **6 esquemas de dominio** y **29 tablas**
- **Estándar de Errores API:** RFC 9457 Problem Details for HTTP APIs con internacionalización (`es` / `en`)
- **Concurrencia:** Control de bloqueo optimista determinista (`OptimisticJpaSaveSupport` + `@Version`)
- **Documentación:** SpringDoc OpenAPI 2.8.13 (Swagger UI) & Structurizr C4 DSL / PlantUML

---

## Requisitos Previos

- **Java JDK 21+**
- **Maven 3.9+** (o usar el wrapper incluido `.\mvnw.cmd` / `./mvnw`)
- **PostgreSQL 16+** (opcional; el perfil `local` utiliza H2 embebido en archivo sin requerir instalaciones externas)

---

## Estructura del Proyecto y Bounded Contexts

El proyecto está organizado como un **Monolito Modular** en `src/main/java/com/noctiva/noxway/`:

```text
src/main/java/com/noctiva/noxway/
├── iam/            # Resolución de actor (X-Actor-Id), estado de seguridad y contratos /api/v1/auth/*
├── profiles/       # Perfiles (UserAccount), directorio, preferencias de notificación y TrustLink
├── commuting/      # Trayectos activos (Commute), pings GPS (TelemetryPing), SOS (SafetyIncident) y alertas
├── community/      # Puntos 24h (ServicePoint), horarios, votos, zonas de riesgo, calificaciones y avisos con mapa
├── wellness/       # Sesiones de descanso (RestSession), metas diarias (SleepGoal) y resumen semanal
├── subscriptions/  # Planes, suscripciones, pagos offline, beneficios colectivos, grupo familiar y referidos
├── moderation/     # Cola de moderación comunitaria (ModerationReport) y auditoría (ModerationAction)
└── shared/         # Shared Kernel (AbstractDomainAggregateRoot, Result<T,E>, RFC 9457 ProblemDetail, i18n)
```

Cada Bounded Context sigue estrictamente la arquitectura de **4 capas**:
- `domain`: Agregados puros (sin anotaciones JPA), objetos de valor, reglas de negocio e interfaces de repositorio.
- `application`: Servicios de comando (`*CommandService`), servicios de consulta (`*QueryService`), `record` commands/queries e implementaciones internas.
- `infrastructure`: Entidades de persistencia JPA (`*PersistenceEntity`), repositorios Spring Data, adaptadores (`*PersistenceAssembler`) y correo/seguridad.
- `interfaces`: Controladores REST (`/api/v1/*`), recursos DTO (`record`), ensambladores REST y fachadas ACL (`*Facade`).

---

## Esquemas de Base de Datos (Flyway `V1` – `V5`)

| Esquema | N.º Tablas | Tablas Principales |
|---|---|---|
| `users_network_schema` | 5 | `user_accounts`, `trust_links`, `notification_preferences`, `auth_sessions`, `password_reset_tokens` |
| `commute_incident_schema` | 4 | `commutes`, `telemetry_pings`, `safety_incidents`, `alert_notifications` |
| `community_data_schema` | 6 | `service_points`, `service_opening_intervals`, `service_votes`, `risk_zones`, `route_ratings`, `community_posts` |
| `wellness_schema` | 2 | `rest_sessions`, `sleep_goals` |
| `subscription_schema` | 9 | `subscription_plans`, `subscriptions`, `subscription_members`, `subscription_payments`, `collective_benefits`, `benefit_claims`, `referral_codes`, `referral_redemptions`, `referral_rewards` |
| `moderation_schema` | 2 | `moderation_reports`, `moderation_actions` |

---

## Ejecución Local y Acceso a Herramientas

### 1) Iniciar el servidor en perfil `local`

En **Windows PowerShell** (recuerda colocar comillas en el argumento `-D` para que PowerShell no separe los puntos):

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

En **macOS / Linux / Git Bash**:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### 2) Enlaces Locales Disponibles (`http://localhost:8080`)

- **Swagger UI (Documentación interactiva):** `http://localhost:8080/swagger-ui.html`
- **OpenAPI 3 JSON:** `http://localhost:8080/v3/api-docs`
- **Healthcheck Actuator:** `http://localhost:8080/actuator/health`
- **Consola Web de Base de Datos H2:** `http://localhost:8080/h2-console`
  - **Driver Class:** `org.h2.Driver`
  - **JDBC URL:** `jdbc:h2:file:./data/noxway;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;AUTO_SERVER=TRUE`
  - **User Name:** `sa` *(sin contraseña)*

---

## Superficie del API (`/api/v1`)

| Módulo | Endpoints Principales |
|---|---|
| **IAM** | `GET /api/v1/auth/status`, `POST /api/v1/auth/sign-in`, `sign-up`, `sign-out`, `password-reset` |
| **Profiles & Trust Network** | `GET/PUT /api/v1/accounts/me`, `GET /api/v1/accounts/directory`, `GET/PUT /api/v1/accounts/me/notification-preferences`, `GET/POST /api/v1/trust-links`, `POST /api/v1/trust-links/{id}/accept\|reject`, `DELETE /api/v1/trust-links/{id}` |
| **Commuting & Companion** | `GET/POST /api/v1/commutes`, `GET /api/v1/commutes/mine`, `GET/POST /api/v1/commutes/{id}/telemetry`, `POST /api/v1/commutes/{id}/delay`, `POST /api/v1/commutes/{id}/arrive`, `GET/POST /api/v1/commutes/{id}/incidents`, `POST /api/v1/incidents/{id}/resolve`, `GET /api/v1/companion/live`, `GET /api/v1/companion/alerts`, `GET/POST /api/v1/alerts/deliveries*` |
| **Community & 24h Map** | `GET/POST/PUT /api/v1/community/services*`, `PUT /api/v1/community/services/{id}/vote`, `GET/POST /api/v1/community/risk-zones*`, `GET/POST /api/v1/community/route-ratings*`, `GET/POST/DELETE /api/v1/community/posts*` |
| **Moderation** | `GET/POST /api/v1/moderation/reports`, `POST /api/v1/moderation/reports/{id}/decision`, `GET /api/v1/moderation/reports/{id}/actions` |
| **Wellness & Sleep** | `GET/POST/DELETE /api/v1/wellness/rest-sessions*`, `GET/PUT /api/v1/wellness/goal`, `GET /api/v1/wellness/summary` |
| **Subscriptions & Benefits** | `GET /api/v1/plans`, `GET/POST /api/v1/subscriptions*`, `GET/POST /api/v1/payments*`, `GET/POST /api/v1/benefits*`, `GET /api/v1/benefit-claims`, `GET/POST /api/v1/referral-code`, `/referrals`, `/referral-rewards*`, `/subscription-invitations*` |

---

## Ejecución de Pruebas Automatizadas

```powershell
.\mvnw.cmd -B test
```

Ejecuta **22 pruebas automatizadas** (`AggregateLifecycleTest`, `DomainRulesTest`, `GlobalExceptionHandlerTest`, `RepositoryAdaptersTest` y `NoxwayPlatformIntegrationTest`) validando reglas de dominio, bloqueo optimista, internacionalización RFC 9457 (`es`/`en`) y flujos completos sobre Spring Boot + Flyway + H2.

---

## Documentación Técnica (`docs/`)

Consulta la carpeta `docs/` para las especificaciones arquitectónicas y de diseño:
- [Diagrama de Clases (PlantUML)](docs/class-diagram.puml) y [Vista Modular Mermaid](docs/class-diagram.md)
- [Arquitectura de Software (Structurizr C4 DSL)](docs/software-architecture.dsl) y [Guía C4 + Esquemas BD](docs/software-architecture.md)
- [Historias de Usuario y Technical Stories (API)](docs/user-stories.md)
- [Architecture Decision Records (ADRs)](docs/adrs.md)
- [Referencia Rápida de Endpoints REST](docs/API.md)
- [Guía de Comprobación del Backend](docs/COMPROBAR-BACKEND.md)
- [Especificación OpenAPI 3.0](docs/openapi.json) y [Colección Postman](docs/noxway.postman_collection.json)
