package com.noctiva.noxway.subscriptions.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record ReferralUseResource(
    UUID id, UUID referralId, UUID referredWorkerId, Instant createdAt) {}
