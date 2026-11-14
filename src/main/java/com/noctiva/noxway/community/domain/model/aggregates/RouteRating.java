package com.noctiva.noxway.community.domain.model.aggregates;

import static com.noctiva.noxway.shared.domain.Rules.require;

import com.noctiva.noxway.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class RouteRating extends AbstractDomainAggregateRoot<RouteRating> {

  private final UUID id;
  private final UUID commuteId;
  private final UUID workerId;
  private final int score;
  private final String comments;
  private final Instant createdAt;
  private final long version;

  private RouteRating(
      UUID id,
      UUID commuteId,
      UUID workerId,
      int score,
      String comments,
      Instant createdAt,
      long version) {
    this.id = id;
    this.commuteId = commuteId;
    this.workerId = workerId;
    this.score = score;
    this.comments = comments;
    this.createdAt = createdAt;
    this.version = version;
  }

  public static RouteRating create(UUID commuteId, UUID workerId, int score, String comments) {
    Objects.requireNonNull(commuteId);
    Objects.requireNonNull(workerId);
    require(score >= 1 && score <= 5, "Score must be between 1 and 5");
    return new RouteRating(
        UUID.randomUUID(),
        commuteId,
        workerId,
        score,
        comments == null ? "" : comments.trim(),
        Instant.now(),
        0L);
  }

  public static RouteRating reconstitute(
      UUID id,
      UUID commuteId,
      UUID workerId,
      int score,
      String comments,
      Instant createdAt,
      long version) {
    return new RouteRating(id, commuteId, workerId, score, comments, createdAt, version);
  }

  public UUID getId() {
    return id;
  }

  public UUID getCommuteId() {
    return commuteId;
  }

  public UUID getWorkerId() {
    return workerId;
  }

  public int getScore() {
    return score;
  }

  public String getComments() {
    return comments;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public long getVersion() {
    return version;
  }
}
