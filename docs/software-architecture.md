# Noxway Platform — Arquitectura de Software (C4, DDD & Esquemas de Base de Datos)

> La definición formal en Structurizr DSL se encuentra en [software-architecture.dsl](software-architecture.dsl). Este documento presenta las vistas arquitectónicas C4, el diseño en 4 capas DDD/CQRS y la distribución física de las 29 tablas en 6 esquemas.

---

## 1. Diagrama de Contexto del Sistema (C4 Nivel 1)

```mermaid
flowchart LR
  Worker["👷 Trabajador Nocturno / Diurno"]
  Companion["🛡️ Contacto de Confianza (Companion)"]
  Admin["⚖️ Moderador / Administrador"]

  subgraph Noxway["Ecosistema Noxway"]
    Platform["Noxway Platform (SPA Angular + API Spring Boot)"]
  end

  OSM["🗺️ OpenStreetMap & OpenFreeMap\n(Tiles Raster + PBF Vectoriales 24h)"]
  Mail["📧 Proveedor SMTP / Mensajería\n(Email / SMS / WhatsApp)"]
  Payment["💳 Canales Offline\n(Yape / Plin / Agente)"]

  Worker -->|Gestiona trayectos, mapa 24h, avisos y descanso| Platform
  Companion -->|Monitorea GPS en vivo y atiende alertas SOS| Platform
  Admin -->|Modera reportes y concilia pagos offline| Platform
  Platform -->|Carga mapas y POIs metropolitanos| OSM
  Platform -->|Despacha notificaciones de alerta| Mail
  Worker -->|Genera comprobante de pago| Payment
```

---

## 2. Diagrama de Contenedores (C4 Nivel 2)

```mermaid
flowchart LR
  subgraph Clients["Clientes Web"]
    SPA["🌐 Noxway Frontend SPA\n(Angular 21, Signals, Leaflet, OpenFreeMap PBF)"]
  end

  subgraph Backend["Noxway Backend"]
    API["⚙️ Noxway Platform REST API\n(Java 21, Spring Boot 3.5.7, OpenAPI 3, RFC 9457)"]
    DB[("🗄️ Base de Datos Relacional\nPostgreSQL 16 / H2 File Mode\n6 Schemas · 29 Tablas (Flyway V1-V5)")]
  end

  OSM["🗺️ OpenStreetMap + OpenFreeMap"]
  SMTP["📧 Servidor SMTP / Cola de Alertas"]

  SPA -->|JSON / HTTPS /api/v1\nX-Actor-Id + Accept-Language| API
  SPA -->|HTTPS Tiles & PBF Vector Tiles| OSM
  API -->|Spring Data JPA + Flyway JDBC| DB
  API -->|Despacho de alertas multicanal| SMTP
```

---

## 3. Diagrama de Componentes y Bounded Contexts (C4 Nivel 3)

Cada Bounded Context aplica estrictamente las **4 capas de DDD** (`domain`, `application`, `infrastructure`, `interfaces`) y se comunica con otros contextos únicamente mediante **Facades ACL (`*Facade`)**:

```mermaid
flowchart TB
  subgraph Interfaces["Capa Interfaces (REST Controllers & ACL Facades)"]
    AuthCtrl["AuthController\n/api/v1/auth/*"]
    AccCtrl["AccountsController & TrustLinksController\n/api/v1/accounts, /trust-links"]
    CommCtrl["CommutingController\n/api/v1/commutes, /companion, /alerts"]
    MapCtrl["CommunityController\n/api/v1/community/*"]
    ModCtrl["ModerationController\n/api/v1/moderation/*"]
    WellCtrl["WellnessController\n/api/v1/wellness/*"]
    SubCtrl["SubscriptionsController\n/api/v1/plans, /subscriptions, /payments, /benefits"]
  end

  subgraph Application["Capa Aplicación (CQRS: CommandService & QueryService)"]
    IAM["iam\nIamContextFacade"]
    PRO["profiles\nProfileDirectoryFacade"]
    COM["commuting\nCommuteVerificationFacade"]
    CMY["community\nCommunityTargetGovernanceFacade"]
    MOD["moderation\nModerationQueueFacade"]
    WEL["wellness\nWellnessCommand/QueryService"]
    SUB["subscriptions\nSubscriptionAllowanceFacade"]
  end

  subgraph Shared["Shared Kernel (com.noctiva.noxway.shared)"]
    SH["AbstractDomainAggregateRoot · Result<T,E>\nOptimisticJpaSaveSupport · RFC 9457 ProblemDetail i18n"]
  end

  PRO -->|Valida cupo de contactos| SUB
  COM -->|Consulta red de confianza| PRO
  CMY -->|Enriquece autor y verifica trayecto| PRO
  CMY -->|Verifica trayecto ARRIVED| COM
  CMY -->|Encola revisión| MOD
  MOD -->|Aprueba/Rechaza punto o riesgo| CMY
```

---

## 4. Distribución de Esquemas y Tablas en Base de Datos (Flyway `V1` – `V5`)

La base de datos evita tablas monolíticas dividiendo las **29 tablas** en **6 esquemas de dominio** más el historial de Flyway:

| Esquema | Tablas (`29` en total) | Propósito |
|---|---|---|
| `users_network_schema` (5) | `user_accounts`, `trust_links`, `notification_preferences`, `auth_sessions`, `password_reset_tokens` | Cuentas de trabajadores/contactos/admins, vínculos de confianza, preferencias por canal y sesiones IAM |
| `commute_incident_schema` (4) | `commutes`, `telemetry_pings`, `safety_incidents`, `alert_notifications` | Trayectos activos/históricos, pings GPS con velocidad, incidentes SOS y cola de despacho multicanal |
| `community_data_schema` (6) | `service_points`, `service_opening_intervals`, `service_votes`, `risk_zones`, `route_ratings`, `community_posts` | Locales 24h, horarios por día de semana, votos `+1`/`-1`, zonas de riesgo, calificación de rutas y avisos con categoría y coordenadas OSM |
| `wellness_schema` (2) | `rest_sessions`, `sleep_goals` | Bitácora de sueño sin solapamientos y meta diaria personalizada (`240`–`720` min) |
| `subscription_schema` (9) | `subscription_plans`, `subscriptions`, `subscription_members`, `subscription_payments`, `collective_benefits`, `benefit_claims`, `referral_codes`, `referral_redemptions`, `referral_rewards` | Catálogo de planes, membresías, grupo familiar `FAMILY_CREW`, pagos offline con recibo único, cupones y referidos |
| `moderation_schema` (2) | `moderation_reports`, `moderation_actions` | Cola de moderación y auditoría inmutable de decisiones administrativas |
| `public` (1) | `flyway_schema_history` | Control de versiones de migraciones SQL (`V1` a `V5`) |
