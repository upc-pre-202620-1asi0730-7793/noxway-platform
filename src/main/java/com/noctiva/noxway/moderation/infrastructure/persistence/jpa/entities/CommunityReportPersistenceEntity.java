package com.noctiva.noxway.moderation.infrastructure.persistence.jpa.entities;

import com.noctiva.noxway.shared.infrastructure.persistence.jpa.entities.AuditableUuidPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "community_reports", schema = "moderation_schema")
public class CommunityReportPersistenceEntity extends AuditableUuidPersistenceEntity {

  @Column(name = "reported_by", nullable = false)
  private UUID reportedBy;

  @Column(name = "target_type", nullable = false)
  private String targetType;

  @Column(name = "target_id", nullable = false)
  private UUID targetId;

  @Column(name = "reason", nullable = false)
  private String reason;

  @Column(name = "status", nullable = false)
  private String status;

  public CommunityReportPersistenceEntity() {}

  public UUID getReportedBy() {
    return reportedBy;
  }

  public void setReportedBy(UUID reportedBy) {
    this.reportedBy = reportedBy;
  }

  public String getTargetType() {
    return targetType;
  }

  public void setTargetType(String targetType) {
    this.targetType = targetType;
  }

  public UUID getTargetId() {
    return targetId;
  }

  public void setTargetId(UUID targetId) {
    this.targetId = targetId;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
