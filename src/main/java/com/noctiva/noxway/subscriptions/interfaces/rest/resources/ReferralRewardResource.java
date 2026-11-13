package com.noctiva.noxway.subscriptions.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record ReferralRewardResource(
    UUID id, UUID workerId, UUID referralUseId, int days, String status, Instant createdAt) {}
