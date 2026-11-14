package com.noctiva.noxway.profiles.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record TrustLinkResource(
    UUID id,
    UUID workerId,
    String workerName,
    String workerEmail,
    String workerPhone,
    String workerShiftType,
    UUID contactId,
    String contactName,
    String contactEmail,
    String contactPhone,
    String status,
    Instant acceptedAt,
    Instant createdAt) {}
