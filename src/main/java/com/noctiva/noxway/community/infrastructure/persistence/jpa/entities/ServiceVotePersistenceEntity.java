package com.noctiva.noxway.community.infrastructure.persistence.jpa.entities;

import com.noctiva.noxway.shared.infrastructure.persistence.jpa.entities.AuditableUuidPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "service_votes", schema = "community_data_schema")
public class ServiceVotePersistenceEntity extends AuditableUuidPersistenceEntity {

  @Column(name = "service_id", nullable = false)
  private UUID serviceId;

  @Column(name = "account_id", nullable = false)
  private UUID accountId;

  @Column(name = "direction", nullable = false)
  private int direction;

  public ServiceVotePersistenceEntity() {}

  public UUID getServiceId() {
    return serviceId;
  }

  public void setServiceId(UUID serviceId) {
    this.serviceId = serviceId;
  }

  public UUID getAccountId() {
    return accountId;
  }

  public void setAccountId(UUID accountId) {
    this.accountId = accountId;
  }

  public int getDirection() {
    return direction;
  }

  public void setDirection(int direction) {
    this.direction = direction;
  }
}
