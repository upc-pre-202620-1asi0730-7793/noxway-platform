package com.noctiva.noxway.subscriptions.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record SubscriptionInvitationResource(
    UUID id,
    UUID subscriptionId,
    UUID workerId,
    String planCode,
    String planName,
    String ownerName,
    String subscriptionStatus,
    String status,
    Instant createdAt) {}
