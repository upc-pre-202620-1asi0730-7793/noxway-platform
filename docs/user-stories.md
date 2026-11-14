# Noxway Platform — Historias de Usuario y Technical Stories (REST API)

## Convenciones Generales
- **Ruta base**: `/api/v1`
- **Formato de carga útil**: `Content-Type: application/json`
- **Contexto de actor**: Cabecera `X-Actor-Id` (con fallback automático al trabajador principal `00000000-0000-0000-0000-000000000101` cuando `app.security.enabled=false`).
- **Internacionalización**: Cabecera `Accept-Language` (`es` o `en`).
- **Estándar de errores**: Todas las respuestas de error (`4xx` y `5xx`) cumplen **RFC 9457 Problem Details for HTTP APIs**.

---

## 1. Historias Técnicas de Arquitectura (Cross-Cutting)

### TS-ARCH001 — Resolución de Actor y Estado Estructurado de IAM
**Como** desarrollador frontend, **quiero** identificar al actor activo mediante la cabecera `X-Actor-Id` y consultar el estado del módulo IAM en `/api/v1/auth/status`, **para** operar los portales de Trabajador, Contacto de Confianza y Administrador sin bloqueos mientras el módulo JWT permanece diferido.

**Criterios de aceptación:**
- **Escenario: Consulta de estado IAM**
  - **Dado** que `app.security.enabled=false`
  - **Cuando** el cliente envía `GET /api/v1/auth/status`
  - **Entonces** el API responde `200 OK` con `securityEnabled: false`, `mode: "STRUCTURED_PENDING_IMPLEMENTATION"` y `defaultWorkerId`.
- **Escenario: Contrato IAM diferido**
  - **Dado** que el cliente invoca `POST /api/v1/auth/sign-in`, `sign-up`, `sign-out` o `password-reset`
  - **Cuando** `app.security.enabled=false`
  - **Entonces** el API responde `501 Not Implemented` con un cuerpo RFC 9457 `ProblemDetail` localizado según `Accept-Language`.

---

### TS-ARCH002 — Manejo Estandarizado de Errores con RFC 9457 Problem Details
**Como** desarrollador frontend, **quiero** que todos los errores del API sigan el estándar RFC 9457 Problem Details **para** mostrar mensajes claros y localizados (`es`/`en`) en la interfaz.

| Código HTTP | `type` URI | Caso de Uso |
|---|---|---|
| `400 Bad Request` | `https://api.noxway.pe/problems/validation-error` | Campos requeridos faltantes o formato inválido (`errors` por campo) |
| `403 Forbidden` | `https://api.noxway.pe/problems/forbidden` | Intento de modificar un recurso de otro trabajador o acción exclusiva de `ADMIN`/`MODERATOR` |
| `404 Not Found` | `https://api.noxway.pe/problems/not-found` | Recurso inexistente o punto de servicio aún no aprobado |
| `409 Conflict` | `https://api.noxway.pe/problems/conflict` | Trayecto activo duplicado, recibo de pago offline repetido o conflicto de versión optimista |
| `422 Unprocessable Entity` | `https://api.noxway.pe/problems/rule-violation` | Violación de regla de dominio (ej. solapamiento de descanso, límite de contactos del plan superado) |
| `501 Not Implemented` | `https://api.noxway.pe/problems/not-implemented` | Endpoint de autenticación diferido |

---

## 2. Perfiles y Red de Contactos de Confianza (`profiles`)

### US-PRO001 — Gestión de Perfil y Turno Laboral
**Como** trabajador nocturno, **quiero** consultar y actualizar mi nombre, teléfono, tipo de turno (`NIGHT`, `DAY`, `ROTATING`), horario de jornada y zona horaria mediante `GET/PUT /api/v1/accounts/me`, **para** mantener sincronizada mi ficha operativa.

**Criterios de aceptación:**
- **Dado** un trabajador activo identificado por `X-Actor-Id`
- **Cuando** envía `PUT /api/v1/accounts/me` con `fullName`, `phone`, `shiftType`, `shiftStart`, `shiftEnd` y `timeZone`
- **Entonces** el API actualiza la cuenta y responde `200 OK` con el perfil actualizado.

### US-PRO002 — Invitación y Ciclo de Vida de Contactos de Confianza (`TrustLink`)
**Como** trabajador nocturno, **quiero** invitar a un contacto de confianza registrado por correo electrónico (`POST /api/v1/trust-links`) respetando el cupo de mi plan de suscripción, **para** que pueda monitorear mis traslados en vivo.

**Criterios de aceptación:**
- **Escenario: Invitación exitosa dentro del límite del plan**
  - **Dado** que el trabajador tiene cupo disponible en su plan (`ESSENTIAL`: 1 contacto, `SENTINEL_PRO`/`FAMILY_CREW`: hasta 5)
  - **Cuando** envía `POST /api/v1/trust-links` con `{ "contactEmail": "rosa.paredes@noxway.pe" }`
  - **Entonces** el API crea el vínculo en estado `PENDING` y responde `201 Created`.
- **Escenario: Límite del plan alcanzado**
  - **Dado** que el trabajador en plan `ESSENTIAL` ya posee 1 vínculo activo
  - **Cuando** intenta invitar a un segundo contacto
  - **Entonces** el API rechaza la solicitud con `422 Unprocessable Entity`.
- **Escenario: Aceptación por el contacto**
  - **Dado** un vínculo en estado `PENDING`
  - **Cuando** el contacto destinatario envía `POST /api/v1/trust-links/{id}/accept`
  - **Entonces** el vínculo pasa a estado `ACTIVE` y habilita el monitoreo en `/api/v1/companion/live`.

---

## 3. Trayectos Seguros, Telemetría GPS y Companion Portal (`commuting`)

### US-COM001 — Iniciar Trayecto Nocturno, Registrar Ping GPS y Confirmar Llegada
**Como** trabajador nocturno, **quiero** iniciar un trayecto (`POST /api/v1/commutes`), enviar pings GPS desde el mapa interactivo (`POST /api/v1/commutes/{id}/telemetry`), reportar demoras (`POST /api/v1/commutes/{id}/delay`) y confirmar mi llegada segura (`POST /api/v1/commutes/{id}/arrive`), **para** que mis contactos conozcan mi ubicación y estado en tiempo real.

**Criterios de aceptación:**
- **Escenario: Un solo trayecto activo por trabajador**
  - **Dado** que el trabajador ya tiene un trayecto en estado `ACTIVE` o `DELAYED`
  - **Cuando** envía `POST /api/v1/commutes` para abrir otro simultáneamente
  - **Entonces** el API responde `409 Conflict`.
- **Escenario: Registro de telemetría GPS**
  - **Dado** un trayecto activo del trabajador
  - **Cuando** envía `POST /api/v1/commutes/{id}/telemetry` con `latitude`, `longitude`, `speed` y `recordedAt`
  - **Entonces** el API responde `201 Created` y expone el último ping en `GET /api/v1/companion/live`.

### US-COM002 — Emisión de Incidentes SOS y Monitoreo en Vivo del Companion
**Como** contacto de confianza activo, **quiero** consultar `GET /api/v1/companion/live` y `GET /api/v1/companion/alerts` **para** visualizar el trayecto en curso, la última coordenada GPS en OpenStreetMap y resolver incidentes SOS abiertos.

**Criterios de aceptación:**
- **Dado** que el trabajador dispara `POST /api/v1/commutes/{id}/incidents` con `type: "SOS"`
- **Cuando** el contacto vinculado consulta `GET /api/v1/companion/live` y `GET /api/v1/companion/alerts`
- **Entonces** visualiza el incidente `OPEN`, las coordenadas del último ping GPS y las alertas generadas para sus canales habilitados.

---

## 4. Mapa Comunitario 24h, Avisos con Ubicación y Moderación (`community` & `moderation`)

### US-MAP001 — Exploración de Locales 24h, Zonas de Riesgo y Votación de Confianza
**Como** trabajador nocturno, **quiero** explorar en el mapa interactivo OpenStreetMap + OpenFreeMap las farmacias, supermercados, grifos, hospitales, comisarías, puntos comunitarios 24h y zonas de riesgo (`GET /api/v1/community/services` y `GET /api/v1/community/risk-zones`), y votar `+1`, `0` o `-1` (`PUT /api/v1/community/services/{id}/vote`), **para** planificar paradas seguras durante mi turno.

**Criterios de aceptación:**
- **Dado** un punto de servicio en estado `APPROVED`
- **Cuando** un usuario envía `PUT /api/v1/community/services/{id}/vote` con `{ "direction": 1 }`, `{ "direction": -1 }` o `{ "direction": 0 }` (retirar voto)
- **Entonces** el API recalcula `trustScore`, `upvotes`, `downvotes` y `myVote` y responde `200 OK`.

### US-MAP002 — Publicación de Avisos Comunitarios con Categoría y Punto en OpenStreetMap
**Como** trabajador nocturno, **quiero** publicar avisos en el tablón comunitario (`POST /api/v1/community/posts`) indicando categoría (*Aviso de seguridad*, *Zona de riesgo*, *Farmacia*, *Tienda 24 horas*, *Grifo*) y marcando la ubicación exacta en el mapa OpenStreetMap, **para** alertar a otros trabajadores y permitirles abrir el punto directamente en `Mapa 24h`.

**Criterios de aceptación:**
- **Escenario: Aviso con categoría y coordenadas válidas**
  - **Dado** un trabajador que marca un punto en el mapa interactivo del modal *Crear aviso*
  - **Cuando** envía `POST /api/v1/community/posts` con `title`, `content`, `district` (opcional, por defecto `"Lima"`), `category`, `latitude` y `longitude`
  - **Entonces** el API persiste el aviso, enriquece `authorName` y `authorRole`, y responde `201 Created`.
- **Escenario: Coordenadas incompletas rechazadas**
  - **Dado** que se envía `latitude` sin `longitude` (o viceversa)
  - **Cuando** el API valida la invariante geográfica
  - **Entonces** responde `422 Unprocessable Entity`.

### US-MOD001 — Moderación Administrativa de Puntos 24h y Zonas de Riesgo
**Como** moderador o administrador, **quiero** revisar la cola de reportes pendientes (`GET /api/v1/moderation/reports?status=PENDING`) y aprobarlos o rechazarlos (`POST /api/v1/moderation/reports/{id}/decision`), **para** asegurar la calidad de los datos publicados en el mapa 24h.

---

## 5. Bienestar y Recuperación de Sueño (`wellness`)

### US-WEL001 — Bitácora de Descanso sin Solapamientos y Meta Diaria
**Como** trabajador de turno nocturno, **quiero** registrar mis sesiones de sueño (`POST /api/v1/wellness/rest-sessions`), configurar mi meta diaria entre `240` y `720` minutos (`PUT /api/v1/wellness/goal`) y consultar mi resumen semanal (`GET /api/v1/wellness/summary`), **para** controlar mi déficit de sueño acumulado.

**Criterios de aceptación:**
- **Dado** que el trabajador ya tiene una sesión de descanso entre `08:00` y `14:00`
- **Cuando** intenta registrar otra sesión que se solapa con ese intervalo
- **Entonces** el API rechaza el registro con `422 Unprocessable Entity`.

---

## 6. Suscripciones, Pagos Offline, Grupo Familiar y Referidos (`subscriptions`)

### US-SUB001 — Suscripción a Planes, Conciliación de Pago Offline y Cupones
**Como** trabajador, **quiero** suscribirme a un plan (`POST /api/v1/subscriptions`), registrar mi orden de pago offline (`POST /api/v1/subscriptions/{id}/payments`) y canjear beneficios exclusivos de mi plan (`POST /api/v1/benefits/{id}/claims`), **para** ampliar mi red de contactos y acceder a descuentos nocturnos.

**Criterios de aceptación:**
- **Escenario: Referencia única de comprobante offline**
  - **Dado** un pago en estado `PENDING`
  - **Cuando** el administrador ejecuta `POST /api/v1/payments/{id}/confirm-offline` con `{ "reference": "YAPE-987654" }`
  - **Entonces** el pago pasa a `CONFIRMED`, la suscripción se activa (`ACTIVE`) por 30 días y, si fue referido, se acredita la recompensa al trabajador referente. Si otro pago intenta reutilizar la misma referencia `YAPE-987654`, el API responde `409 Conflict`.
