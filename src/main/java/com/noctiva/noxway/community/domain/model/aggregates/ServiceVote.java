package com.noctiva.noxway.community.domain.model.aggregates;

import static com.noctiva.noxway.shared.domain.Rules.require;

import com.noctiva.noxway.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import java.time.Instant;
import java.util.UUID;

public class ServiceVote extends AbstractDomainAggregateRoot<ServiceVote> {

  private final UUID id;
  private final UUID serviceId;
  private final UUID accountId;
  private int direction;
  private final Instant createdAt;
  private final long version;

  private ServiceVote(
      UUID id, UUID serviceId, UUID accountId, int direction, Instant createdAt, long version) {
    this.id = id;
    this.serviceId = serviceId;
    this.accountId = accountId;
    this.direction = direction;
    this.createdAt = createdAt;
    this.version = version;
  }

  public static ServiceVote create(UUID serviceId, UUID accountId, int direction) {
    require(direction == -1 || direction == 1, "Vote must be -1 or 1");
    return new ServiceVote(UUID.randomUUID(), serviceId, accountId, direction, Instant.now(), 0L);
  }

  public static ServiceVote reconstitute(
      UUID id, UUID serviceId, UUID accountId, int direction, Instant createdAt, long version) {
    return new ServiceVote(id, serviceId, accountId, direction, createdAt, version);
  }

  public void changeDirection(int direction) {
    require(direction == -1 || direction == 1, "Vote must be -1 or 1");
    this.direction = direction;
  }

  public UUID getId() {
    return id;
  }

  public UUID getServiceId() {
    return serviceId;
  }

  public UUID getAccountId() {
    return accountId;
  }

  public int getDirection() {
    return direction;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public long getVersion() {
    return version;
  }
}
