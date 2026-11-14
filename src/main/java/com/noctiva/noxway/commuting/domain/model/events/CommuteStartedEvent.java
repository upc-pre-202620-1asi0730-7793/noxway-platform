package com.noctiva.noxway.commuting.domain.model.events;

import java.time.Instant;
import java.util.UUID;

public record CommuteStartedEvent(
    UUID commuteId, UUID workerId, String destination, Instant estimatedArrivalTime, Instant occurredAt) {}
