# Cómo comprobar el backend

## 1. Pruebas automáticas

Con JDK 21, abre la terminal en la raíz del proyecto.

Windows PowerShell:

```powershell
.\mvnw.cmd clean verify
```

macOS/Linux:

```bash
./mvnw clean verify
```

El resultado esperado es BUILD SUCCESS y 15 pruebas sin fallos ni errores. Incluyen reglas de dominio y llamadas HTTP mediante MockMvc con Spring, Flyway y repositorios reales sobre H2. No equivalen a probar PostgreSQL ni el frontend.

## 2. Arranque

Ejecuta NoxwayApplication desde IntelliJ con perfil `local`, según README. Visita http://localhost:8080/actuator/health: debe mostrar `{"status":"UP"}`. Esto comprueba el arranque; no demuestra por sí solo todos los flujos.

## 3. Prueba manual con Swagger

Abre http://localhost:8080/swagger-ui.html. En cada operación pulsa Try it out y después Execute.

1. Registra un WORKER con POST /api/v1/auth/register usando el JSON del README. Puedes usar shiftType DAY o NIGHT. Usa un correo de prueba nuevo.
2. Inicia sesión en POST /api/v1/auth/login con su correo y contraseña. Copia accessToken en Authorize (sin comillas).
3. Consulta tu cuenta y crea un trayecto usando los esquemas y rutas de Swagger. El ETA debe ser futuro. Guarda su id.
4. Registra ubicaciones durante el trayecto y marca llegada. Consulta el historial: debe reflejar el estado final.
5. Registra un descanso con fecha/hora inicial y final; consulta el resumen. Una hora final anterior al inicio debe ser rechazada.
6. Para verificar contactos, registra otra cuenta TRUSTED_CONTACT, envía una invitación desde el trabajador e inicia sesión como contacto para aceptarla. Cada cuenta usa su propio token. Comprueba acceso con consentimiento y denegación tras revocarlo.
7. Cierra sesión y comprueba que el token anterior ya no permite acceder a recursos protegidos.

Las rutas completas y flujos están en API.md. También puedes importar noxway.postman_collection.json en Postman. Los recursos protegidos sin token deben responder 401; los datos inválidos deben producir un error de validación, no éxito.

## 4. Persistencia y verificación pendiente

Detén y vuelve a iniciar la aplicación local desde la misma carpeta; los datos H2 se guardan en data/. Confirma que puedes iniciar sesión y consultar los registros anteriores. No subas esa carpeta al repositorio.

Antes de la entrega: repetir los flujos con PostgreSQL, conectar el frontend a /api/v1, probar ambos juntos, desplegar y recoger evidencias reales. Correo/SMS/WhatsApp requieren proveedores configurados; no se consideran enviados por el simple hecho de crear una alerta.

## Referencia de los ejemplos del curso

Se usaron los proyectos catch-up y learning-center compartidos como referencia de capas, dominio independiente de persistencia, repositorios, adaptadores, entidades JPA, mappers y controladores REST. Los bounded contexts y reglas se adaptaron al reporte de Noxway. No es una copia exacta: se añadieron seguridad, migraciones Flyway, pruebas y configuración H2/PostgreSQL; las versiones y decisiones técnicas tampoco son idénticas a todos los ejemplos. Los comandos de aplicación se agrupan por contexto y los servicios de aplicación reúnen consultas y comandos, en vez de reproducir cada clase del ejemplo.
