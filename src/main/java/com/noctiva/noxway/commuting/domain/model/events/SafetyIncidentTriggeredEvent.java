package com.noctiva.noxway.commuting.domain.model.events;

import java.time.Instant;
import java.util.UUID;

public record SafetyIncidentTriggeredEvent(
    UUID incidentId,
    UUID commuteId,
    UUID workerId,
    String type,
    String description,
    Instant occurredAt) {}
