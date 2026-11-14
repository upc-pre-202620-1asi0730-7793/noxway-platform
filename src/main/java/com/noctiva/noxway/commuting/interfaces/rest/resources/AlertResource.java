package com.noctiva.noxway.commuting.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record AlertResource(
    UUID id,
    UUID commuteId,
    UUID incidentId,
    UUID workerId,
    String workerName,
    UUID contactId,
    String channel,
    String status,
    String message,
    Instant deliveredAt,
    Instant createdAt) {}
