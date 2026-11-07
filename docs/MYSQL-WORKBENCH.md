# Noxway con MySQL Workbench en Windows

## Preparación

Necesitas MySQL Server 8.0.16+ (recomendado 8.4 LTS), Workbench y JDK 21. PostgreSQL/pgAdmin no se usan en esta versión. No necesitas Docker.

1. Abre Workbench y tu conexión local. Verifica host y puerto (habitualmente localhost:3306).
2. Abre una pestaña SQL y ejecuta `docs/sql/create-database.sql` con el rayo. Crea solo la base, no las tablas.
3. Para una primera comprobación local puedes usar tu cuenta root; para uso compartido o despliegue usa una cuenta exclusiva con permisos sobre noxway. Nunca publiques su contraseña.
4. En IntelliJ abre la carpeta que contiene pom.xml, selecciona JDK 21 en Project SDK y Maven Runner. Ejecuta Maven → Lifecycle → clean y después verify.

## Ejecutar en PowerShell

Detén solo el backend anterior con Ctrl+C. Entra en la carpeta noxway-platform (donde están pom.xml y target). Configura estas variables en la misma terminal donde arrancarás:

```powershell
Remove-Item Env:SPRING_PROFILES_ACTIVE -ErrorAction SilentlyContinue
$env:DB_URL = "jdbc:mysql://localhost:3306/noxway?connectionTimeZone=UTC&forceConnectionTimeZoneToSession=true"
$env:DB_USERNAME = "root"
$cred = Get-Credential -UserName $env:DB_USERNAME -Message "Contraseña de tu usuario MySQL"
$env:DB_PASSWORD = $cred.GetNetworkCredential().Password
& "$env:USERPROFILE\.jdks\temurin-21.0.13\bin\java.exe" -jar .\target\noxway-platform-1.0.0.jar
```

Ajusta la ruta del JDK si tu carpeta tiene otra actualización de Java 21. No añadas `--spring.profiles.active=local`: ese perfil selecciona H2. No dejes variables SPRING_DATASOURCE_URL/USERNAME/PASSWORD de pruebas anteriores, porque pueden sobreescribir DB_URL/USERNAME/PASSWORD.

Si tu servidor local exige recuperación de la clave pública y aparece `Public Key Retrieval is not allowed`, puedes añadir `&allowPublicKeyRetrieval=true` a DB_URL exclusivamente para tu servidor local de confianza. Para servidores remotos configura TLS con verificación del certificado; no deshabilites TLS ni permitas recuperación de claves indiscriminadamente.

En IntelliJ también puedes usar una configuración Application de NoxwayApplication con esas mismas variables y sin perfil local. No subas credenciales ni configuraciones privadas al repositorio.

## Comprobar la base real

1. El log debe indicar MySQL; Flyway aplica cuatro migraciones y debe aparecer Started NoxwayApplication.
2. En Workbench actualiza Schemas y despliega noxway → Tables. Se crean 28 tablas de negocio y flyway_schema_history.
3. Abre http://localhost:8080/swagger-ui/index.html. Registra un trabajador de prueba, inicia sesión, autoriza con accessToken y crea/consulta un trayecto.
4. En Workbench comprueba los registros:

```sql
USE noxway;
SELECT version, description, success FROM flyway_schema_history ORDER BY installed_rank;
SELECT BIN_TO_UUID(id) AS id, email, role, shift_type FROM accounts;
SELECT BIN_TO_UUID(id) AS id, origin, destination, status FROM commutes;
```

No consultes ni compartas hashes o tokens. BINARY(16) guarda los UUID sin reordenar bytes; BIN_TO_UUID(id) permite leerlos. Las fechas se guardan en UTC con microsegundos.

5. Detén y vuelve a iniciar solo el backend, manteniendo MySQL Server activo. Confirma que puedes iniciar sesión y consultar los registros anteriores.

## Ejecutar las pruebas sobre MySQL

Crea una base aparte para evitar mezclar pruebas con tus datos:

```sql
CREATE DATABASE IF NOT EXISTS noxway_test
  CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
```

En la configuración Maven de IntelliJ para verify establece estas variables, con tu usuario y contraseña privados:

```text
TEST_DB_URL=jdbc:mysql://localhost:3306/noxway_test?connectionTimeZone=UTC&forceConnectionTimeZoneToSession=true
TEST_DB_DRIVER=com.mysql.cj.jdbc.Driver
TEST_DB_USERNAME=tu_usuario
TEST_DB_PASSWORD=tu_contraseña
```

Ejecuta verify con JDK 21. Las pruebas crean datos ficticios solo en la base indicada. Sin TEST_DB_URL se ejecutan contra H2 en memoria; BUILD SUCCESS en H2 no demuestra compatibilidad MySQL.

## Datos anteriores

La base MySQL empieza vacía. No importamos automáticamente PostgreSQL ni la base H2 anterior. Conserva la carpeta data y tus copias de PostgreSQL. El nuevo perfil local usa data/noxway-mysql-preview para no alterar la base H2 antigua. No uses Flyway repair ni borres historiales para forzar compatibilidad entre motores.
