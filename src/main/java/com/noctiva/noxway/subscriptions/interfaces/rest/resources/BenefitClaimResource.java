package com.noctiva.noxway.subscriptions.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record BenefitClaimResource(
    UUID id,
    UUID workerId,
    UUID benefitId,
    String benefitName,
    String benefitDescription,
    String planCode,
    String code,
    Instant createdAt) {}
