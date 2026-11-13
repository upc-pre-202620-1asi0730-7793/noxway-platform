package com.noctiva.noxway.commuting.infrastructure.persistence.jpa.entities;

import com.noctiva.noxway.shared.infrastructure.persistence.jpa.entities.AuditableUuidPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "safety_incidents", schema = "commute_incident_schema")
public class SafetyIncidentPersistenceEntity extends AuditableUuidPersistenceEntity {

  @Column(name = "commute_id", nullable = false)
  private UUID commuteId;

  @Column(name = "worker_id", nullable = false)
  private UUID workerId;

  @Column(name = "type", nullable = false)
  private String type;

  @Column(name = "description", nullable = false)
  private String description;

  @Column(name = "status", nullable = false)
  private String status;

  @Column(name = "reported_at", nullable = false)
  private Instant reportedAt;

  public SafetyIncidentPersistenceEntity() {}

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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Instant getReportedAt() {
    return reportedAt;
  }

  public void setReportedAt(Instant reportedAt) {
    this.reportedAt = reportedAt;
  }
}
