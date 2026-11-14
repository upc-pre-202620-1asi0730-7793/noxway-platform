# Architecture Decision Records (ADRs) — Noxway Platform

Este documento registra las Decisiones de Arquitectura (ADRs) de **Noxway Platform** siguiendo el formato de Michael Nygard.

---

## Índice

- [ADR-001: Monolito Modular con Arquitectura de 4 Capas y Domain-Driven Design (DDD)](#adr-001-monolito-modular-con-arquitectura-de-4-capas-y-domain-driven-design-ddd)
- [ADR-002: Segregación de Responsabilidades de Comandos y Consultas (CQRS)](#adr-002-segregación-de-responsabilidades-de-comandos-y-consultas-cqrs)
- [ADR-003: Anti-Corruption Layer (ACL) mediante Context Facades](#adr-003-anti-corruption-layer-acl-mediante-context-facades)
- [ADR-004: Desacoplamiento Estricto entre Agregados de Dominio y Entidades JPA](#adr-004-desacoplamiento-estricto-entre-agregados-de-dominio-y-entidades-jpa)
- [ADR-005: Manejo Funcional de Resultados con Tipo Sellado `Result<T, E>`](#adr-005-manejo-funcional-de-resultados-con-tipo-sellado-resultt-e)
- [ADR-006: Adopción de RFC 9457 Problem Details Internacionalizado (ES/EN)](#adr-006-adopción-de-rfc-9457-problem-details-internacionalizado-esen)
- [ADR-007: Aislamiento Físico en 6 Esquemas de Base de Datos con Flyway (`V1`–`V5`)](#adr-007-aislamiento-físico-en-6-esquemas-de-base-de-datos-con-flyway-v1v5)
- [ADR-008: Control de Concurrencia Optimista Determinista (`OptimisticJpaSaveSupport`)](#adr-008-control-de-concurrencia-optimista-determinista-optimisticjpasavesupport)
- [ADR-009: IAM Estructurado y Resolución de Actor vía `X-Actor-Id`](#adr-009-iam-estructurado-y-resolución-de-actor-vía-x-actor-id)
- [ADR-010: Avisos Comunitarios Geolocalizados e Integración con OpenStreetMap](#adr-010-avisos-comunitarios-geolocalizados-e-integración-con-openstreetmap)

---

## ADR-001: Monolito Modular con Arquitectura de 4 Capas y Domain-Driven Design (DDD)
- **Estado**: Aceptado
- **Contexto**: La plataforma abarca múltiples subdominios (Perfiles, Trayectos y Alertas SOS, Mapa 24h y Comunidad, Bienestar, Suscripciones y Moderación) que requieren límites claros sin la complejidad operativa de microservicios distribuidos.
- **Decisión**: Estructurar el backend como un **Monolito Modular** dividido en 7 Bounded Contexts (`iam`, `profiles`, `commuting`, `community`, `wellness`, `subscriptions`, `moderation`) más `shared`, cada uno con 4 capas (`domain`, `application`, `infrastructure`, `interfaces`).
- **Consecuencias**: Alta cohesión por dominio, pruebas unitarias rápidas sin base de datos para reglas de negocio y bajo costo operativo.

## ADR-002: Segregación de Responsabilidades de Comandos y Consultas (CQRS)
- **Estado**: Aceptado
- **Contexto**: Las operaciones de escritura requieren validaciones de invariantes y bloqueo optimista, mientras que las vistas del frontend requieren proyecciones enriquecidas (ej. `ServicePointView` con estado abierto en tiempo real, distancia en km y voto del usuario).
- **Decisión**: Separar las operaciones en `*CommandService` (mutaciones con `record` commands) y `*QueryService` (lecturas con `record` queries y vistas de proyección).
- **Consecuencias**: Controladores REST delgados y consultas enriquecidas sin contaminar los agregados de dominio.

## ADR-003: Anti-Corruption Layer (ACL) mediante Context Facades
- **Estado**: Aceptado
- **Contexto**: Los contextos necesitan colaborar (por ejemplo, `community` necesita el nombre del autor desde `profiles` y verificar si un trayecto terminó en `commuting`).
- **Decisión**: Prohibir el acceso directo a repositorios de otros contextos y exponer contratos explícitos en `interfaces.acl` (`IamContextFacade`, `ProfileDirectoryFacade`, `SubscriptionAllowanceFacade`, `CommuteVerificationFacade`, `ModerationQueueFacade`, `CommunityTargetGovernanceFacade`).
- **Consecuencias**: Bajo acoplamiento entre módulos y trazabilidad explícita de dependencias inter-contexto.

## ADR-004: Desacoplamiento Estricto entre Agregados de Dominio y Entidades JPA
- **Estado**: Aceptado
- **Contexto**: Anotar los agregados de dominio con `@Entity` de JPA expone constructores vacíos y setters públicos que rompen la encapsulación de invariantes.
- **Decisión**: Mantener los agregados en `domain.model.aggregates` como clases Java puras con métodos de negocio y factorías `create(...)` / `reconstitute(...)`, mapeándolos hacia `*PersistenceEntity` mediante `*PersistenceAssembler`.
- **Consecuencias**: Invariantes de dominio protegidas en tiempo de compilación y persistencia desacoplada.

## ADR-005: Manejo Funcional de Resultados con Tipo Sellado `Result<T, E>`
- **Estado**: Aceptado
- **Contexto**: Usar excepciones para errores de negocio esperados oculta el flujo de control en las firmas de los servicios.
- **Decisión**: Utilizar la interfaz sellada `Result<T, ApplicationError>` en todos los `CommandService` y `QueryService`, transformándola en respuestas HTTP mediante `ResponseEntityAssembler`.
- **Consecuencias**: Contratos de aplicación explícitos y tipados.

## ADR-006: Adopción de RFC 9457 Problem Details Internacionalizado (ES/EN)
- **Estado**: Aceptado
- **Contexto**: El frontend requiere un contrato uniforme de errores que soporte español e inglés según la cabecera `Accept-Language`.
- **Decisión**: Centralizar las respuestas de error en `ProblemDetailFromApplicationErrorAssembler` y `GlobalExceptionHandler` devolviendo `org.springframework.http.ProblemDetail` (RFC 9457) con `type`, `title`, `status`, `detail` localizado y `errors` de validación.
- **Consecuencias**: Manejo de errores uniforme en todos los servicios Angular mediante `extractProblemMessage`.

## ADR-007: Aislamiento Físico en 6 Esquemas de Base de Datos con Flyway (`V1`–`V5`)
- **Estado**: Aceptado
- **Contexto**: Agrupar todas las tablas en un único esquema dificulta visualizar la propiedad de datos de cada Bounded Context.
- **Decisión**: Gestionar la evolución del esquema mediante migraciones Flyway (`V1` a `V5`) organizadas en 6 esquemas (`users_network_schema`, `commute_incident_schema`, `community_data_schema`, `wellness_schema`, `subscription_schema`, `moderation_schema`) compatibles con PostgreSQL 16 y H2 (`MODE=PostgreSQL`).
- **Consecuencias**: 29 tablas claramente segmentadas por contexto de negocio y arranque local automático en `./data/noxway`.

## ADR-008: Control de Concurrencia Optimista Determinista (`OptimisticJpaSaveSupport`)
- **Estado**: Aceptado
- **Contexto**: Al separar agregados de dominio de entidades JPA, es necesario garantizar que las actualizaciones concurrentes con una versión obsoleta disparen `ObjectOptimisticLockingFailureException` (`409 Conflict`).
- **Decisión**: Implementar `OptimisticJpaSaveSupport`, que verifica el campo `@Version` antes de sincronizar el estado y fuerza `saveAndFlush`.
- **Consecuencias**: Prevención confiable de sobrescrituras perdidas en trayectos, pagos offline y vínculos de confianza.

## ADR-009: IAM Estructurado y Resolución de Actor vía `X-Actor-Id`
- **Estado**: Aceptado
- **Contexto**: Se requiere probar todos los flujos de negocio de extremo a extremo sin bloqueo por pantalla de login mientras se mantiene la estructura completa de IAM.
- **Decisión**: Configurar `app.security.enabled=false` por defecto, resolver el actor activo con `ActorContextResolver` (`X-Actor-Id`) y responder `501 Not Implemented` (RFC 9457) en `/api/v1/auth/sign-*`.
- **Consecuencias**: Navegación fluida en el frontend y pruebas integrales reproducibles para todos los roles.

## ADR-010: Avisos Comunitarios Geolocalizados e Integración con OpenStreetMap
- **Estado**: Aceptado
- **Contexto**: Los trabajadores necesitan publicar avisos comunitarios categorizados (*Aviso de seguridad*, *Zona de riesgo*, *Farmacia*, *Tienda 24 horas*, *Grifo*) con coordenadas marcadas en OpenStreetMap y poder saltar directamente desde el aviso hacia el `Mapa 24h`.
- **Decisión**: Añadir la migración `V5__community_post_category_location.sql` incorporando `category`, `latitude` y `longitude` a `community_posts`, validando coordenadas completas en el agregado `CommunityPost`.
- **Consecuencias**: Integración bidireccional entre el tablón de `Comunidad` y el `Mapa 24h`.
