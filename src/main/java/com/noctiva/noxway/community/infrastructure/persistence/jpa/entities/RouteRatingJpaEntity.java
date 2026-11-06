package com.noctiva.noxway.community.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "route_ratings", schema = "community_data_schema")
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

  @Column(name = "comments", nullable = false)
  public String comments;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Version public long version;
}
