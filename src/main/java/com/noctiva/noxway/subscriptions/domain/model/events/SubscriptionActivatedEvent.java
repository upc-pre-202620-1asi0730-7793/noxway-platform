package com.noctiva.noxway.subscriptions.domain.model.events;

import java.time.Instant;
import java.util.UUID;

public record SubscriptionActivatedEvent(
    UUID subscriptionId, UUID workerId, UUID planId, Instant occurredAt) {}
