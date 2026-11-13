package com.noctiva.noxway.moderation.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record ModerationActionResource(
    UUID id,
    UUID reportId,
    UUID moderatorId,
    String moderatorName,
    String decision,
    String notes,
    Instant createdAt) {}
