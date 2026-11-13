package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities;

import com.noctiva.noxway.shared.infrastructure.persistence.jpa.entities.AuditableUuidPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "referral_codes", schema = "subscription_schema")
public class ReferralCodePersistenceEntity extends AuditableUuidPersistenceEntity {

  @Column(name = "worker_id", nullable = false)
  private UUID workerId;

  @Column(name = "code", nullable = false, unique = true)
  private String code;

  public ReferralCodePersistenceEntity() {}

  public UUID getWorkerId() {
    return workerId;
  }

  public void setWorkerId(UUID workerId) {
    this.workerId = workerId;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }
}
