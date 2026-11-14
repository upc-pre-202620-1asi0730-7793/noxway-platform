package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities;

import com.noctiva.noxway.shared.infrastructure.persistence.jpa.entities.AuditableUuidPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "referral_rewards", schema = "subscription_schema")
public class ReferralRewardPersistenceEntity extends AuditableUuidPersistenceEntity {

  @Column(name = "worker_id", nullable = false)
  private UUID workerId;

  @Column(name = "referral_use_id", nullable = false)
  private UUID referralUseId;

  @Column(name = "days", nullable = false)
  private int days;

  @Column(name = "status", nullable = false)
  private String status;

  public ReferralRewardPersistenceEntity() {}

  public UUID getWorkerId() {
    return workerId;
  }

  public void setWorkerId(UUID workerId) {
    this.workerId = workerId;
  }

  public UUID getReferralUseId() {
    return referralUseId;
  }

  public void setReferralUseId(UUID referralUseId) {
    this.referralUseId = referralUseId;
  }

  public int getDays() {
    return days;
  }

  public void setDays(int days) {
    this.days = days;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
