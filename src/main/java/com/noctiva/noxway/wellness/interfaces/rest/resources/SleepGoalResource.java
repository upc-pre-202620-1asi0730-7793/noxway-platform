package com.noctiva.noxway.wellness.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record SleepGoalResource(UUID id, UUID workerId, int targetMinutes, Instant createdAt) {}
