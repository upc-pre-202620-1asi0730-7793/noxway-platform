package com.noctiva.noxway.subscriptions.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record CollectiveBenefitResource(
    UUID id, String name, String description, String planCode, boolean active, Instant createdAt) {}
