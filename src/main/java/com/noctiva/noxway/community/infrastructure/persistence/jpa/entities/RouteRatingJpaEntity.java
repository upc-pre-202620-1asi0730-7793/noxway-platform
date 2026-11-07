package com.noctiva.noxway.community.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "route_ratings")
public class RouteRatingJpaEntity {
  @Id
  @Column(name = "id", nullable = false)
  public UUID id;

  @Column(name = "commute_id", nullable = false)
  public UUID commuteId;

  @Column(name = "worker_id", nullable = false)
  public UUID workerId;

  @Column(name = "score", nullable = false)
  public int score;

  @Column(name = "comments", nullable = false, length = 1000)
  public String comments;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Version public long version;
}
