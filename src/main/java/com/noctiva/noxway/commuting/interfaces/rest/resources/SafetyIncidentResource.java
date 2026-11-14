package com.noctiva.noxway.commuting.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record SafetyIncidentResource(
    UUID id,
    UUID commuteId,
    UUID workerId,
    String type,
    String description,
    String status,
    Instant reportedAt,
    Instant createdAt) {}
