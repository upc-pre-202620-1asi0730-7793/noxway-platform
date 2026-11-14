package com.noctiva.noxway.subscriptions.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record SubscriptionResource(
    UUID id,
    UUID workerId,
    UUID planId,
    String planCode,
    String planName,
    BigDecimal monthlyPrice,
    int maxContacts,
    int maxWorkers,
    String status,
    boolean owner,
    Instant startsAt,
    Instant endsAt,
    Instant createdAt) {}
