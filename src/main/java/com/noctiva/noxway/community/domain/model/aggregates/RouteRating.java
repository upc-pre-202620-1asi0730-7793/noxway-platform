package com.noctiva.noxway.community.domain.model.aggregates;

import java.time.Instant;
import java.util.UUID;

public record RouteRating(
    UUID id,
    UUID commuteId,
    UUID workerId,
    int score,
    String comments,
    Instant createdAt,
    long version) {
  public static RouteRating create(UUID commuteId, UUID workerId, int score, String comments) {
    return new RouteRating(
        UUID.randomUUID(), commuteId, workerId, score, comments, Instant.now(), 0);
  }

  public RouteRating withCommuteId(UUID value) {
    return new RouteRating(id, value, workerId, score, comments, createdAt, version);
  }

  public RouteRating withWorkerId(UUID value) {
    return new RouteRating(id, commuteId, value, score, comments, createdAt, version);
  }

  public RouteRating withScore(int value) {
    return new RouteRating(id, commuteId, workerId, value, comments, createdAt, version);
  }

  public RouteRating withComments(String value) {
    return new RouteRating(id, commuteId, workerId, score, value, createdAt, version);
  }
}
