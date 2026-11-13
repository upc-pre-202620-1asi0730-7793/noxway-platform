package com.noctiva.noxway.community.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record RouteRatingResource(
    UUID id, UUID commuteId, UUID workerId, int score, String comments, Instant createdAt) {}
