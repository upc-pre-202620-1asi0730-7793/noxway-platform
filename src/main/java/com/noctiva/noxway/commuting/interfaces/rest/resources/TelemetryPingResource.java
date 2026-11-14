package com.noctiva.noxway.commuting.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record TelemetryPingResource(
    UUID id, UUID commuteId, double latitude, double longitude, double speed, Instant recordedAt) {}
