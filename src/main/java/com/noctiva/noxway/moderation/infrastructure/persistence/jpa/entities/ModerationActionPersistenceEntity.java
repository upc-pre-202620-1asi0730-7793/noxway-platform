package com.noctiva.noxway.moderation.infrastructure.persistence.jpa.entities;

import com.noctiva.noxway.shared.infrastructure.persistence.jpa.entities.AuditableUuidPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "moderation_actions", schema = "moderation_schema")
public class ModerationActionPersistenceEntity extends AuditableUuidPersistenceEntity {

  @Column(name = "report_id", nullable = false)
  private UUID reportId;

  @Column(name = "moderator_id", nullable = false)
  private UUID moderatorId;

  @Column(name = "decision", nullable = false)
  private String decision;

  @Column(name = "notes", nullable = false)
  private String notes;

  public ModerationActionPersistenceEntity() {}

  public UUID getReportId() {
    return reportId;
  }

  public void setReportId(UUID reportId) {
    this.reportId = reportId;
  }

  public UUID getModeratorId() {
    return moderatorId;
  }

  public void setModeratorId(UUID moderatorId) {
    this.moderatorId = moderatorId;
  }

  public String getDecision() {
    return decision;
  }

  public void setDecision(String decision) {
    this.decision = decision;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }
}
