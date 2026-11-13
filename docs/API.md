# API REST de Noxway

Base: `/api/v1`. Con `app.security.enabled=false` (modo abierto actual con IAM diferido), los endpoints usan la cabecera opcional `X-Actor-Id` (por defecto `00000000-0000-0000-0000-000000000101`).

## IAM (estructurado y diferido)

| Método | Ruta | Estado |
|---|---|---|
| GET | `/api/v1/auth/status` | `200 OK` (informa `STRUCTURED_PENDING_IMPLEMENTATION` cuando seguridad está deshabilitada) |
| POST | `/api/v1/auth/sign-in` | `501 Not Implemented` |
| POST | `/api/v1/auth/sign-up` | `501 Not Implemented` |
| POST | `/api/v1/auth/sign-out` | `501 Not Implemented` |
| POST | `/api/v1/auth/password-reset` | `501 Not Implemented` |

## Endpoints operativos

| Método | Ruta |
|---|---|
| GET | `/api/v1/accounts/me` |
| PUT | `/api/v1/accounts/me` |
| GET | `/api/v1/accounts/directory` |
| POST | `/api/v1/accounts` |
| GET | `/api/v1/accounts/{id}` |
| GET | `/api/v1/accounts/me/notification-preferences` |
| PUT | `/api/v1/accounts/me/notification-preferences` |
| POST | `/api/v1/trust-links` |
| GET | `/api/v1/trust-links` |
| POST | `/api/v1/trust-links/{id}/accept` |
| POST | `/api/v1/trust-links/{id}/reject` |
| DELETE | `/api/v1/trust-links/{id}` |
| POST | `/api/v1/commutes` |
| GET | `/api/v1/commutes` |
| GET | `/api/v1/commutes/mine` |
| GET | `/api/v1/commutes/{id}` |
| POST | `/api/v1/commutes/{id}/telemetry` |
| GET | `/api/v1/commutes/{id}/telemetry` |
| POST | `/api/v1/commutes/{id}/delay` |
| POST | `/api/v1/commutes/{id}/arrive` |
| POST | `/api/v1/commutes/{id}/incidents` |
| GET | `/api/v1/commutes/{id}/incidents` |
| POST | `/api/v1/incidents/{id}/resolve` |
| GET | `/api/v1/companion/live` |
| GET | `/api/v1/companion/alerts` |
| GET | `/api/v1/alerts/deliveries` |
| POST | `/api/v1/alerts/deliveries/{id}/retry` |
| POST | `/api/v1/alerts/deliveries/dispatch` |
| POST | `/api/v1/community/services` |
| PUT | `/api/v1/community/services/{id}` |
| GET | `/api/v1/community/services` |
| GET | `/api/v1/community/services/mine` |
| GET | `/api/v1/community/services/{id}` |
| PUT | `/api/v1/community/services/{id}/vote` |
| POST | `/api/v1/community/risk-zones` |
| GET | `/api/v1/community/risk-zones` |
| POST | `/api/v1/community/risk-zones/{id}/resolve` |
| POST | `/api/v1/community/route-ratings` |
| GET | `/api/v1/community/route-ratings/mine` |
| POST | `/api/v1/community/posts` |
| GET | `/api/v1/community/posts` |
| DELETE | `/api/v1/community/posts/{id}` |
| GET | `/api/v1/moderation/reports` |
| POST | `/api/v1/moderation/reports` |
| POST | `/api/v1/moderation/reports/{id}/decision` |
| GET | `/api/v1/moderation/reports/{id}/actions` |
| GET | `/api/v1/plans` |
| POST | `/api/v1/subscriptions` |
| GET | `/api/v1/subscriptions` |
| POST | `/api/v1/subscriptions/{id}/cancel` |
| POST | `/api/v1/subscriptions/{id}/payments` |
| GET | `/api/v1/payments` |
| POST | `/api/v1/payments/{id}/confirm-offline` |
| POST | `/api/v1/payments/{id}/reject` |
| GET | `/api/v1/benefits` |
| POST | `/api/v1/benefits` |
| POST | `/api/v1/benefits/{id}/claims` |
| GET | `/api/v1/benefit-claims` |
| GET | `/api/v1/referral-rewards` |
| POST | `/api/v1/referral-rewards/{id}/redeem` |
| GET | `/api/v1/referral-code` |
| POST | `/api/v1/referrals` |
| POST | `/api/v1/subscriptions/{id}/members` |
| GET | `/api/v1/subscriptions/{id}/members` |
| GET | `/api/v1/subscription-invitations` |
| POST | `/api/v1/subscription-invitations/{id}/accept` |
| POST | `/api/v1/subscription-invitations/{id}/reject` |
| DELETE | `/api/v1/subscription-members/{id}` |
| POST | `/api/v1/wellness/rest-sessions` |
| GET | `/api/v1/wellness/rest-sessions` |
| DELETE | `/api/v1/wellness/rest-sessions/{id}` |
| PUT | `/api/v1/wellness/goal` |
| GET | `/api/v1/wellness/goal` |
| GET | `/api/v1/wellness/summary` |

## Flujos recomendados (modo abierto con `X-Actor-Id`)

1. Consulta `/api/v1/accounts/directory` para listar las cuentas sembradas (`0101` trabajador, `0102` trabajador crew, `0201` y `0202` contactos, `0901` admin) o crea una nueva con `POST /api/v1/accounts`.
2. El trabajador crea `/trust-links`; el contacto (`X-Actor-Id` del contacto) acepta `/trust-links/{id}/accept`.
3. El trabajador inicia `/commutes`, registra `/commutes/{id}/telemetry` y confirma `/commutes/{id}/arrive`. Un contacto ajeno o revocado no puede consultar `/companion/live`.
4. Propón un servicio con `/community/services`. El administrador consulta `/moderation/reports` y aprueba el reporte correspondiente; luego vota con `direction` en `{-1, 0, 1}` (`0` retira el voto).
5. Registra `/wellness/rest-sessions` con fechas completas ISO-8601; consulta `/wellness/summary` y configura meta en `/wellness/goal`.
6. Elige `/plans`, solicita `/subscriptions` y `/subscriptions/{id}/payments`. El administrador (`0901`) concilia usando `/payments/{id}/confirm-offline` con una referencia de recibo única.
7. Usa `/subscription-invitations` para consentir grupos familiares y `/referral-rewards` para canjear crédito disponible.

## Errores (RFC 9457 Problem Details)

- `400`: formato o campos inválidos.
- `403`: acceso a recurso ajeno o rol insuficiente.
- `404`: recurso inexistente o no publicado.
- `409`: duplicado o actualización concurrente optimista.
- `422`: regla de negocio incumplida.
- `501`: contrato IAM diferido hasta activar `app.security.enabled=true`.
- `503`: proveedor de correo/alertas no disponible.

Los filtros `page` y `size` usan página base cero (tamaño 1–100). Las respuestas paginadas incluyen `items`, `page`, `size`, `total`. Consulta los esquemas en Swagger (`/swagger-ui.html`).

## Invitaciones: contratos diferentes

- `POST /api/v1/trust-links`: `{ "contactEmail": "rosa.paredes@noxway.pe" }` (contacto de confianza).
- `POST /api/v1/subscriptions/{id}/members`: `{ "workerEmail": "jorge.huaman@noxway.pe" }` (trabajador miembro de una suscripción familiar activa).
