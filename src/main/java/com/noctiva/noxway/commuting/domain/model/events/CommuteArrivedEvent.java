package com.noctiva.noxway.commuting.domain.model.events;

import java.time.Instant;
import java.util.UUID;

public record CommuteArrivedEvent(
    UUID commuteId, UUID workerId, Instant arrivalTime, Instant occurredAt) {}
