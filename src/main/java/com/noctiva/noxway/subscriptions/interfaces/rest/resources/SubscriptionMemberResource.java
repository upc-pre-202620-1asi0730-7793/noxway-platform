package com.noctiva.noxway.subscriptions.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record SubscriptionMemberResource(
    UUID id,
    UUID subscriptionId,
    UUID workerId,
    String workerName,
    String workerEmail,
    String status,
    Instant createdAt) {}
