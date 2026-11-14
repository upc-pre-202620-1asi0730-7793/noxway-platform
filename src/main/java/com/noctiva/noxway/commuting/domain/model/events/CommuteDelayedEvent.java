package com.noctiva.noxway.commuting.domain.model.events;

import java.time.Instant;
import java.util.UUID;

public record CommuteDelayedEvent(
    UUID commuteId, UUID workerId, int minutes, Instant delayUntil, Instant occurredAt) {}
