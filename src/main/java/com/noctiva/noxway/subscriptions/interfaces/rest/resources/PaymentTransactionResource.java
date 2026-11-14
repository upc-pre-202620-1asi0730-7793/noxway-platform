package com.noctiva.noxway.subscriptions.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentTransactionResource(
    UUID id,
    UUID subscriptionId,
    UUID workerId,
    String workerName,
    String planCode,
    BigDecimal amount,
    String currency,
    String reference,
    String status,
    Instant createdAt) {}
