package com.noctiva.noxway.profiles.domain.model.events;

import java.time.Instant;
import java.util.UUID;

public record TrustLinkChangedEvent(
    UUID trustLinkId, UUID workerId, UUID contactId, String status, Instant occurredAt) {}
