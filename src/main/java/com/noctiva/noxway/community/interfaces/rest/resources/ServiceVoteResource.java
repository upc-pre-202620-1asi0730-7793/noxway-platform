package com.noctiva.noxway.community.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record ServiceVoteResource(
    UUID id, UUID serviceId, UUID accountId, int direction, Instant createdAt) {}
