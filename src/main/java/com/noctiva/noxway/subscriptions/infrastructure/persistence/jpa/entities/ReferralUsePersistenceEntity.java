package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities;

import com.noctiva.noxway.shared.infrastructure.persistence.jpa.entities.AuditableUuidPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "referral_uses", schema = "subscription_schema")
public class ReferralUsePersistenceEntity extends AuditableUuidPersistenceEntity {

  @Column(name = "referral_id", nullable = false)
  private UUID referralId;

  @Column(name = "referred_worker_id", nullable = false)
  private UUID referredWorkerId;

  public ReferralUsePersistenceEntity() {}

  public UUID getReferralId() {
    return referralId;
  }

  public void setReferralId(UUID referralId) {
    this.referralId = referralId;
  }

  public UUID getReferredWorkerId() {
    return referredWorkerId;
  }

  public void setReferredWorkerId(UUID referredWorkerId) {
    this.referredWorkerId = referredWorkerId;
  }
}
