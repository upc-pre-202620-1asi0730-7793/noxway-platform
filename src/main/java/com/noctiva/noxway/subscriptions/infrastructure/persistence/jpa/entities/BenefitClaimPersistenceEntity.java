package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities;

import com.noctiva.noxway.shared.infrastructure.persistence.jpa.entities.AuditableUuidPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "benefit_claims", schema = "subscription_schema")
public class BenefitClaimPersistenceEntity extends AuditableUuidPersistenceEntity {

  @Column(name = "worker_id", nullable = false)
  private UUID workerId;

  @Column(name = "benefit_id", nullable = false)
  private UUID benefitId;

  @Column(name = "code", nullable = false)
  private String code;

  public BenefitClaimPersistenceEntity() {}

  public UUID getWorkerId() {
    return workerId;
  }

  public void setWorkerId(UUID workerId) {
    this.workerId = workerId;
  }

  public UUID getBenefitId() {
    return benefitId;
  }

  public void setBenefitId(UUID benefitId) {
    this.benefitId = benefitId;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }
}
