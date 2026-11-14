package com.noctiva.noxway.moderation.domain.model.events;

import java.time.Instant;
import java.util.UUID;

public record ReportDecidedEvent(
    UUID reportId, String targetType, UUID targetId, boolean approved, Instant occurredAt) {}
