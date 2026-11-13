package com.noctiva.noxway.profiles.domain.model.events;

import java.time.Instant;
import java.util.UUID;

public record ProfileUpdatedEvent(UUID accountId, String fullName, Instant occurredAt) {}
