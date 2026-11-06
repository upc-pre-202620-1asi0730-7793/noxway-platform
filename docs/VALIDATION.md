# Validación realizada

- Java 21 y Maven: compilación y JAR ejecutable correctos.
- 16 pruebas de dominio e integración: sin errores ni fallos, sobre H2 en modo PostgreSQL.
- Arranque HTTP del JAR: `/actuator/health` devuelve `UP`.
- OpenAPI generado desde la aplicación: 77 operaciones; contrato en `openapi.json`.
- PostgreSQL preparado mediante migraciones y CI; no ejecutado en este entorno.
- Pendientes externos: conexión del frontend, despliegue y credenciales de proveedores.

## Corrección de invitaciones familiares

Se reprodujo la colisión de los dos esquemas `Invite` mediante una prueba fallida. Ahora los contratos tienen nombres distintos (`InviteTrustedContact` y `InviteSubscriptionMember`). La prueba verifica campos obligatorios y referencias OpenAPI independientes. El flujo de integración usa el campo documentado para invitar un trabajador, confirma un pago ficticio como ADMIN, acepta la invitación como miembro y comprueba la revocación al cancelar.
