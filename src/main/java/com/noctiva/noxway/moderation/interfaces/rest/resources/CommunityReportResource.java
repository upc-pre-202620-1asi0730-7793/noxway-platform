package com.noctiva.noxway.moderation.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record CommunityReportResource(
    UUID id,
    UUID reportedBy,
    String reporterName,
    String targetType,
    UUID targetId,
    String targetTitle,
    String targetStatus,
    String reason,
    String status,
    Instant createdAt) {}
