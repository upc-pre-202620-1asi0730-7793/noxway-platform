# Noxway Platform

Backend REST de Noctiva para trabajadores de turnos diurnos/nocturnos y sus contactos de confianza. Java 21, Spring Boot 3.5.7, Maven, MySQL, Flyway y OpenAPI. Sigue el reporte de Noxway y los patrones de dominio independiente, repositorios, adaptadores y mappers de los ejemplos del curso.

## Ejecutar sin instalar MySQL

Requisitos: JDK 21 y acceso a Maven Central para descargar dependencias.

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

En Windows: `mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local`.
El perfil `local` usa H2 persistente en `data/noxway-mysql-preview` para probar; el perfil normal usa MySQL. No hay usuario ni contraseña de administrador por defecto. Si necesitas aprobar servicios o conciliar pagos, configura `ADMIN_EMAIL` y `ADMIN_PASSWORD` (12 caracteres mínimo, máximo 72 bytes UTF-8) **antes de iniciar**. El administrador solo se crea si no existe, nunca se eleva una cuenta pública ni se sobrescribe su contraseña al reiniciar.

- Swagger: http://localhost:8080/swagger-ui.html
- OpenAPI: http://localhost:8080/v3/api-docs
- Salud: http://localhost:8080/actuator/health
- API: http://localhost:8080/api/v1

## Ejecutar desde IntelliJ IDEA

1. Abre la carpeta del proyecto como proyecto Maven y carga sus dependencias.
2. Configura Project SDK y Maven Runner con JDK 21.
3. Crea una configuración Spring Boot para `com.noctiva.noxway.NoxwayApplication`.
4. En Active profiles escribe `local` (o agrega `--spring.profiles.active=local` en Program arguments).
5. Ejecuta y abre http://localhost:8080/swagger-ui.html.

El perfil local permite probar sin instalar MySQL. Para verificar los flujos sigue [la guía de comprobación](docs/COMPROBAR-BACKEND.md).

## Ejecutar con MySQL Server y MySQL Workbench

Instala MySQL Server 8.0.16 o posterior (8.4 LTS recomendado) y MySQL Workbench. Workbench administra el servidor; no reemplaza MySQL Server. No se necesita Docker ni pgAdmin.

1. Abre tu conexión local en Workbench y ejecuta `docs/sql/create-database.sql` para crear `noxway` con `utf8mb4`.
2. Configura en IntelliJ o PowerShell `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` con tu conexión real. Puerto local habitual: 3306. El usuario necesita permisos DDL y DML sobre `noxway` para las migraciones.
3. Ejecuta sin el perfil `local`: el perfil predeterminado usa MySQL. Flyway crea tablas, índices y restricciones; Hibernate valida el resultado.
4. En Workbench actualiza Schemas → noxway → Tables.

Guía paso a paso para Windows: [MYSQL-WORKBENCH.md](docs/MYSQL-WORKBENCH.md).

`.env.example` sirve de referencia; Spring Boot no carga `.env` automáticamente. Las contraseñas se configuran fuera del código.

## Bounded contexts

| Contexto | Paquete Java | Tablas principales en `noxway` |
|---|---|---|
| Identity & Network Management | identity | accounts, trust_links, auth_sessions |
| Safe Commute & Incident Management | commuting | commutes, telemetry_pings, safety_incidents, alerts |
| Community Intelligence | community | service_points, service_opening_intervals, community_posts |
| Sleep Health & Wellness | wellness | rest_sessions, sleep_goals |
| Subscriptions & Collective Benefits | subscriptions | plans, subscriptions, subscription_members, payment_transactions |
| Moderation & Governance | moderation | community_reports, moderation_actions |

`shared` contiene infraestructura común; no es un contexto de negocio. Cada contexto tiene `domain/model/aggregates`, `domain/repositories`, `application`, `infrastructure/persistence/jpa/{entities,repositories,mappers,adapters}` e `interfaces/rest`. Los comandos se encuentran en `application/commands`. Los DTO de entrada usan Bean Validation. Las reglas temporales están en objetos de dominio; los servicios de aplicación orquestan transacciones y permisos. No se copian errores de compilación ni nombres de paquetes incorrectos de los ejemplos.

Los agregados de dominio no dependen de JPA. Las relaciones entre contextos conservan UUID lógicos y se validan mediante interfaces ACL; no hay asociaciones JPA cruzadas. En la base `noxway` hay restricciones, claves foráneas dentro de cada contexto y unicidad. Los seis contextos siguen separados en Java; no se crean seis bases MySQL. Las entidades usan control de versión optimista; operaciones por trabajador adquieren un bloqueo transaccional para proteger invariantes como un único trayecto activo. No se implementan microservicios: es un monolito modular.

## Funciones implementadas

- Registro WORKER o TRUSTED_CONTACT, inicio/cierre de sesión con tokens opacos persistidos como SHA-256, contraseña BCrypt, cambio y recuperación de contraseña, perfil y turnos DAY/NIGHT/ROTATING.
- Invitaciones de confianza, aceptación/rechazo/revocación por participantes, límite de contactos según plan, preferencias de canales y ventanas de prioridad locales.
- Inicio de trayecto, ETA y tolerancia, demora acumulada hasta 120 minutos, telemetría cronológica, llegada/cancelación, incidentes SOS/DELAY/DEVIATION/OTHER y resolución. Detección programada de llegada vencida sin afirmar que todo retraso sea una emergencia.
- Los contactos solo ven trayectos con vínculo aceptado vigente. La consulta de telemetría para contactos termina al cerrar el trayecto; el trabajador conserva su propio historial. No se exige que un trayecto se inicie de noche ni se cierra a medianoche.
- Alertas en la app y cola de entrega externa. SMTP configurable para correo; puente HTTPS configurable para SMS/WhatsApp, con `eventId` e `Idempotency-Key`. Sin proveedor, la alerta permanece `PENDING_PROVIDER`; no se marca enviada. Se suprime la entrega tras revocar consentimiento o desactivar el canal. La ventana de prioridad marca prioridad; no elimina avisos fuera de la ventana.
- Servicios propuestos/actualizados con intervalos semanales, zona horaria IANA, horarios de 24 horas o que cruzan medianoche, múltiples intervalos por día, consulta por categoría, disponibilidad, radio geográfico y momento de referencia. La ausencia de intervalos para un día significa cierre. Servicios pendientes no se publican hasta moderación. Votos individuales, riesgos, publicaciones y valoraciones de trayectos propios completados.
- Descanso persistente con inicio/fin absolutos, calidad, notas, rechazo de solapamientos y períodos inválidos; meta individual y resumen de siete días que reparte el descanso entre fechas en la zona elegida. Las sugerencias son informativas; registros ausentes no prueban falta de sueño.
- Planes Esencial, Centinela Pro y Cuadrilla Familiar, suscripciones, cancelación, solicitud de pago y conciliación administrativa de pagos **offline**. No se cobran tarjetas ni se atribuye un cobro a una pasarela inexistente. Una confirmación administrativa extiende el período 30 días. El plan gratuito no necesita pago.
- Grupos familiares con invitación y aceptación individual, capacidad por plan, retiro y revocación. Beneficios con requisitos de plan y canje único. Códigos de referidos: el primer pago confirmado de la persona referida genera 30 días de crédito para ambas personas; el crédito se canjea una sola vez sobre un plan de pago activo.
- Bandeja de moderación, aprobación/rechazo y auditoría persistida. Solo ADMIN/MODERATOR pueden moderar; solo ADMIN puede crear personal o confirmar pagos.

## Autenticación rápida

```http
POST /api/v1/auth/register
Content-Type: application/json

{"email":"worker@example.com","password":"A-long-password!2026","fullName":"Yam Cano","phone":"987654321","role":"WORKER","shiftType":"NIGHT","shiftStart":"22:00","shiftEnd":"06:00","timeZone":"America/Lima"}
```

Registra al contacto con `role: TRUSTED_CONTACT` y sin datos de turno. Ejecuta `POST /auth/login` con correo y contraseña. Copia `accessToken` en **Authorize** de Swagger como token Bearer. El token completo solo se devuelve al iniciar sesión. Los recursos de cuenta nunca incluyen el hash de contraseña. Cada usuario debe usar su propio token; no reutilices el token de un trabajador para aceptar una invitación como contacto.

Los horarios de turno son horas locales de referencia; en trayectos y descansos se envían instantes ISO-8601 con offset o `Z`. Ejemplo: `2026-11-06T22:00:00-05:00` → `2026-11-07T06:00:00-05:00`. Consulta las fechas futuras reales al iniciar un trayecto; un ETA pasado se rechaza.

Consulta `docs/API.md` para recorrer los endpoints y `docs/noxway.postman_collection.json` para importar ejemplos en Postman.

## Proveedores y despliegue

- Recuperación por correo: configura `SPRING_MAIL_HOST`, `SPRING_MAIL_PORT`, `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD`, propiedades de autenticación/TLS y `NOXWAY_MAIL_FROM`. Sin SMTP, recuperación devuelve 503. El token expira en 30 minutos y se invalida tras uso; también se revocan sesiones al cambiar contraseña.
- WhatsApp/SMS: configura `NOXWAY_DELIVERY_WEBHOOK_URL` (HTTPS) y `NOXWAY_DELIVERY_WEBHOOK_SECRET`. El puente recibe JSON `{channel,recipient,message,eventId,priority}` y debe encargarse de la integración real del proveedor y respetar la clave de idempotencia. Un 2xx confirma aceptación por el puente, no lectura por el destinatario. Las fallas quedan registradas y se reintentan explícitamente desde el panel administrativo.
- Producción necesita MySQL persistente, HTTPS en el proxy de entrada, variables privadas y `CORS_ORIGINS` con los dominios concretos del frontend. El límite de solicitudes de autenticación es por proceso/IP; un despliegue con varias instancias debe añadir control en el gateway.
- Los envíos son de tipo outbox con entrega al menos una vez; el proveedor debe deduplicar por evento. SMTP no garantiza ausencia absoluta de duplicados si el proceso termina después de enviar y antes de confirmar la transacción.

## Pruebas

```bash
./mvnw clean verify
```

Las pruebas de integración arrancan Spring, Flyway y JPA contra H2 en modo MySQL, sin simular repositorios. Comprueban consentimiento, aislamiento de cuentas, revocación, autenticación, horarios, descanso, pagos, grupos, referidos y documentación OpenAPI. Las pruebas de dominio cubren el cambio de semana, intervalos nocturnos y cruces de fecha. SMTP se sustituye por un capturador **solo en la prueba de recuperación**, sin enviar correo real.

La suite de 17 pruebas pasó tanto en H2 como en MySQL 8.0.46 real. Se comprobó además arranque HTTP y persistencia tras reiniciar el JAR. El workflow también está preparado para ejecutar las mismas pruebas contra MySQL 8.4. El resultado de esa ejecución en GitHub depende de publicar el repositorio y ejecutar Actions; no se presenta como ejecutado desde este entorno.

## Integración con el frontend

Este backend introduce contratos versionados y autenticados. El Angular anterior usa endpoints de demostración (`/dashboard`, `/commute`, `/userProfile`, etc.) y formatos distintos. **No basta con cambiar su URL base**: hay que implementar autenticación, agregar el encabezado Bearer y adaptar cada servicio Angular a los recursos `/api/v1`. No se modificó ni desplegó automáticamente ese frontend.

## Alcance y límites

No se incluye envío real sin proveedores, cargo de tarjetas, integración contractual con serenazgo/seguros, geocodificación/ruteo externo, ni índices o consultas espaciales nativas. El filtro de radio usa distancia geodésica en aplicación. Los listados ofrecen paginación de respuesta, pero algunos cargan registros en memoria: antes de operar con grandes volúmenes deben convertirse en consultas paginadas del repositorio. Las ventanas de turno y las metas se configuran por usuario; no implican recomendaciones médicas.

No hay datos ficticios presentados como evidencia de despliegue ni aportes atribuidos a otros integrantes. El paquete contiene código y pruebas; las evidencias del Sprint deben recogerse cuando se ejecute y despliegue con las cuentas del equipo.

## Cambio desde la versión PostgreSQL/H2 anterior

Esta versión utiliza migraciones para una base MySQL nueva. No ejecutes estas migraciones sobre una base ya gestionada por la versión PostgreSQL. La carpeta H2 anterior `data/noxway` no se toca: el perfil local nuevo usa `data/noxway-mysql-preview`. Las cuentas y datos de PostgreSQL o H2 no se importan automáticamente a MySQL. Conserva tus copias; para migrar datos existentes se necesita una exportación/importación independiente. Los UUID de la API siguen siendo cadenas; se guardan como BINARY(16). Los instantes se almacenan como DATETIME(6) en UTC, conservando la zona horaria de negocio en sus columnas.
