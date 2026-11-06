# Validación realizada

- Java 21 y Maven: compilación y JAR ejecutable correctos.
- 15 pruebas de dominio e integración: sin errores ni fallos, sobre H2 en modo PostgreSQL.
- Arranque HTTP del JAR: `/actuator/health` devuelve `UP`.
- OpenAPI generado desde la aplicación: 77 operaciones; contrato en `openapi.json`.
- PostgreSQL preparado mediante migraciones y CI; no ejecutado en este entorno.
- Pendientes externos: conexión del frontend, despliegue y credenciales de proveedores.
