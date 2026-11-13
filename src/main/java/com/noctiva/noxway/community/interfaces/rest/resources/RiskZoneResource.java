package com.noctiva.noxway.community.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record RiskZoneResource(
    UUID id,
    UUID reportedBy,
    String reporterName,
    String description,
    String riskLevel,
    double latitude,
    double longitude,
    String status,
    Instant createdAt) {}
