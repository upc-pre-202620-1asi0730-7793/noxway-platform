package com.noctiva.noxway.wellness.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record RestSessionResource(
    UUID id,
    UUID workerId,
    Instant startTime,
    Instant endTime,
    long durationMinutes,
    int quality,
    String notes,
    Instant createdAt) {}
