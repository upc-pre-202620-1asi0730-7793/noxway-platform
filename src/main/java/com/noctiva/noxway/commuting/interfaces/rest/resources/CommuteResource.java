package com.noctiva.noxway.commuting.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record CommuteResource(
    UUID id,
    UUID workerId,
    String origin,
    String destination,
    String transport,
    Instant startTime,
    Instant estimatedArrivalTime,
    int toleranceMinutes,
    String status,
    Instant arrivalTime,
    Instant delayUntil,
    Instant createdAt) {}
