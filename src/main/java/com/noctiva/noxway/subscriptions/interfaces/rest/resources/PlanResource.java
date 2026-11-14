package com.noctiva.noxway.subscriptions.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PlanResource(
    UUID id,
    String code,
    String name,
    BigDecimal monthlyPrice,
    int maxContacts,
    int maxWorkers,
    boolean active,
    Instant createdAt) {}
