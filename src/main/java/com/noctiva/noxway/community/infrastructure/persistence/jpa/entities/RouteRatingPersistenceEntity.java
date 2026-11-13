package com.noctiva.noxway.community.infrastructure.persistence.jpa.entities;

import com.noctiva.noxway.shared.infrastructure.persistence.jpa.entities.AuditableUuidPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "route_ratings", schema = "community_data_schema")
public class RouteRatingPersistenceEntity extends AuditableUuidPersistenceEntity {

  @Column(name = "commute_id", nullable = false)
  private UUID commuteId;

  @Column(name = "worker_id", nullable = false)
  private UUID workerId;

  @Column(name = "score", nullable = false)
  private int score;

  @Column(name = "comments", nullable = false)
  private String comments;

  public RouteRatingPersistenceEntity() {}

  public UUID getCommuteId() {
    return commuteId;
  }

  public void setCommuteId(UUID commuteId) {
    this.commuteId = commuteId;
  }

  public UUID getWorkerId() {
    return workerId;
  }

  public void setWorkerId(UUID workerId) {
    this.workerId = workerId;
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }
}
