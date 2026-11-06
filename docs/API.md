# API REST de Noxway

Base: `/api/v1`. Todos los endpoints requieren Bearer salvo registro, login, recuperación, Swagger y salud.

| Método | Ruta |
|---|---|
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
| GET | `/api/v1/admin/alert-deliveries` |
| POST | `/api/v1/admin/alert-deliveries/{id}/retry` |
| POST | `/api/v1/commutes` |
| GET | `/api/v1/commutes` |
| GET | `/api/v1/workers/{id}/commutes` |
| GET | `/api/v1/commutes/{id}` |
| POST | `/api/v1/commutes/{id}/arrival` |
| POST | `/api/v1/commutes/{id}/cancel` |
| POST | `/api/v1/commutes/{id}/delay` |
| POST | `/api/v1/commutes/{id}/telemetry` |
| GET | `/api/v1/commutes/{id}/telemetry` |
| POST | `/api/v1/commutes/{id}/incidents` |
| GET | `/api/v1/commutes/{id}/incidents` |
| POST | `/api/v1/incidents/{id}/resolve` |
| GET | `/api/v1/alerts` |
| POST | `/api/v1/alerts/{id}/read` |
| POST | `/api/v1/admin/staff` |
| POST | `/api/v1/auth/register` |
| POST | `/api/v1/auth/login` |
| POST | `/api/v1/auth/logout` |
| GET | `/api/v1/accounts/me` |
| PUT | `/api/v1/accounts/me` |
| PUT | `/api/v1/accounts/me/password` |
| POST | `/api/v1/trust-links` |
| GET | `/api/v1/trust-links` |
| POST | `/api/v1/trust-links/{id}/accept` |
| POST | `/api/v1/trust-links/{id}/reject` |
| DELETE | `/api/v1/trust-links/{id}` |
| GET | `/api/v1/accounts/me/notification-preferences` |
| PUT | `/api/v1/accounts/me/notification-preferences` |
| POST | `/api/v1/auth/password-reset` |
| POST | `/api/v1/auth/password-reset/confirm` |
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
| GET | `/api/v1/wellness/summary` |

## Flujos recomendados

1. Registra trabajador y contacto, inicia sesión con cada uno y conserva sus tokens por separado.
2. El trabajador crea `/trust-links`; el contacto acepta `/trust-links/{id}/accept`.
3. El trabajador inicia `/commutes`, registra telemetría y confirma `/commutes/{id}/arrival`. Un contacto ajeno o revocado no puede consultar.
4. Propón un servicio con `/community/services`. El administrador consulta `/moderation/reports` y aprueba el reporte correspondiente. Consulta disponibilidad con `at=...&openOnly=true`.
5. Registra `/wellness/rest-sessions` con fechas completas; consulta `/wellness/summary`.
6. Elige `/plans`, solicita `/subscriptions` y `/subscriptions/{id}/payments`. El administrador concilia usando `/payments/{id}/confirm-offline` con una referencia de recibo única.
7. Usa `/subscription-invitations` para consentir grupos y `/referral-rewards` para canjear crédito disponible.

## Errores

400: formato o campos inválidos. 401: token ausente/inválido/expirado. 403: acceso a recurso ajeno o rol insuficiente. 404: recurso inexistente o no publicado. 409: duplicado o actualización concurrente. 422: regla de negocio incumplida. 429: límite de autenticación. 503: proveedor de correo no configurado o no disponible.

Los filtros `page` y `size` usan página base cero, tamaño 1–100. Las respuestas paginadas incluyen `items`, `page`, `size`, `total`. Consulta los esquemas de cuerpo en Swagger.

## Invitaciones: contratos diferentes

- `POST /api/v1/trust-links`: `{ "contactEmail": "contact@example.com" }` (contacto de confianza).
- `POST /api/v1/subscriptions/{id}/members`: `{ "workerEmail": "worker@example.com" }` (trabajador miembro de una suscripción familiar).

El segundo flujo requiere una suscripción familiar activa. Una suscripción `PENDING_PAYMENT` no permite invitar miembros; primero debe activarse por conciliación administrativa de un pago offline. No se deben intercambiar los nombres de campo. Swagger usa esquemas independientes para ambos contratos.
