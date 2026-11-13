package com.noctiva.noxway.wellness.domain.model.events;

import java.time.Instant;
import java.util.UUID;

public record RestSessionLoggedEvent(
    UUID sessionId, UUID workerId, long durationMinutes, int quality, Instant occurredAt) {}
