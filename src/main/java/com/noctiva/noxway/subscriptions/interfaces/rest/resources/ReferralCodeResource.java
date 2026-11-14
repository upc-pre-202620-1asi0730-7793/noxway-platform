package com.noctiva.noxway.subscriptions.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record ReferralCodeResource(UUID id, UUID workerId, String code, Instant createdAt) {}
