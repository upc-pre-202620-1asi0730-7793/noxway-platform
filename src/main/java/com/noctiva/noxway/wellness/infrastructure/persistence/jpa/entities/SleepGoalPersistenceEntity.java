package com.noctiva.noxway.wellness.infrastructure.persistence.jpa.entities;

import com.noctiva.noxway.shared.infrastructure.persistence.jpa.entities.AuditableUuidPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "sleep_goals", schema = "wellness_schema")
public class SleepGoalPersistenceEntity extends AuditableUuidPersistenceEntity {

  @Column(name = "worker_id", nullable = false)
  private UUID workerId;

  @Column(name = "target_minutes", nullable = false)
  private int targetMinutes;

  public SleepGoalPersistenceEntity() {}

  public UUID getWorkerId() {
    return workerId;
  }

  public void setWorkerId(UUID workerId) {
    this.workerId = workerId;
  }

  public int getTargetMinutes() {
    return targetMinutes;
  }

  public void setTargetMinutes(int targetMinutes) {
    this.targetMinutes = targetMinutes;
  }
}
