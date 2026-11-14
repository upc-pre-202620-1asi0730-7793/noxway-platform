package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities;

import com.noctiva.noxway.shared.infrastructure.persistence.jpa.entities.AuditableUuidPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "subscription_members", schema = "subscription_schema")
public class SubscriptionMemberPersistenceEntity extends AuditableUuidPersistenceEntity {

  @Column(name = "subscription_id", nullable = false)
  private UUID subscriptionId;

  @Column(name = "worker_id", nullable = false)
  private UUID workerId;

  @Column(name = "status", nullable = false)
  private String status;

  public SubscriptionMemberPersistenceEntity() {}

  public UUID getSubscriptionId() {
    return subscriptionId;
  }

  public void setSubscriptionId(UUID subscriptionId) {
    this.subscriptionId = subscriptionId;
  }

  public UUID getWorkerId() {
    return workerId;
  }

  public void setWorkerId(UUID workerId) {
    this.workerId = workerId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
