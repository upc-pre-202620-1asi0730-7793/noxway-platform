# Validación realizada

- Java 21+ y Maven Wrapper (`.\mvnw.cmd test` / `./mvnw clean verify`): compilación y 22 pruebas de dominio, adaptadores JPA, manejo RFC 9457 e integración REST sobre H2 en modo PostgreSQL sin fallos ni errores.
- Arranque HTTP local con perfil `local`: `/actuator/health` devuelve `UP` y Swagger / OpenAPI 3 disponible en `/swagger-ui.html` y `/v3/api-docs` (`docs/openapi.json`).
- Frontend Angular conectado mediante proxy `/api/v1` hacia Spring Boot (`X-Actor-Id` activo, OpenStreetMap + OpenFreeMap integrados y respuestas RFC 9457 consumidas en UI).
- IAM mantenido estructurado y diferido por decisión de entrega (`app.security.enabled=false`, `/api/v1/auth/status` responde `200` y `/api/v1/auth/sign-*` responde `501 Not Implemented`).
- Migraciones Flyway (`V1`–`V5`, 6 esquemas y 29 tablas) preparadas para PostgreSQL 16 y H2 local.
