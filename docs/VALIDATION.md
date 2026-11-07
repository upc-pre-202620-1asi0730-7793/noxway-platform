# Validación de la versión MySQL

## Resultado ejecutado

- Java 21.0.13, Spring Boot 3.5.7, Maven 3.9.11.
- `clean verify`: 17 pruebas, 0 fallos, 0 errores, 0 omitidas sobre H2 en modo MySQL.
- `verify` contra MySQL Server 8.0.46 real: las mismas 17 pruebas pasaron. Driver MySQL Connector/J, Flyway y repositorios JPA reales; sin simular la base.
- Cuatro migraciones aplicadas correctamente; validación Hibernate del esquema correcta.
- Prueba nueva: UUID BINARY(16), columnas temporales TIMESTAMP JDBC/DATETIME(6), ida y vuelta de instantes UTC con microsegundos y textos Unicode largos en los campos de trayecto.
- JAR ejecutable conectado a MySQL: salud UP, registro 201, login 200, creación de trayecto 201.
- Reinicio del JAR manteniendo MySQL: cuenta, sesión y trayecto conservados; consultas autenticadas 200.
- OpenAPI actualizado desde el JAR: 77 operaciones. Esquemas separados para invitaciones de confianza y familiares.
- El flujo automatizado familiar confirma un pago ficticio como ADMIN, invita con workerEmail, acepta como miembro y comprueba restricciones al cancelar.

## Límites y pendientes

- MySQL 8.4 está configurado en CI, pero Actions no se ejecutó desde este entorno. La comprobación real descrita arriba usó 8.0.46.
- No se modificó el MySQL Server ni el IntelliJ del usuario: falta aplicar esta versión y su conexión en su computadora. Workbench es el cliente administrativo; la API se conecta directamente a MySQL Server.
- No se importaron datos anteriores de PostgreSQL o H2. Se usaron bases aisladas con cuentas ficticias.
- No se conectó ni desplegó el frontend; tampoco se configuraron proveedores externos reales.
- El reporte de arquitectura del proyecto debe alinearse posteriormente con la decisión MySQL si todavía describe PostgreSQL y sus seis esquemas físicos. Los seis bounded contexts de Java se conservan.
- Estas pruebas no son una auditoría exhaustiva de rendimiento, concurrencia o seguridad de producción.
