package com.noctiva.noxway.community.domain.model.aggregates;

import java.time.Instant;
import java.util.UUID;

public record ServiceVote(
    UUID id, UUID serviceId, UUID accountId, int direction, Instant createdAt, long version) {
  public static ServiceVote create(UUID serviceId, UUID accountId, int direction) {
    return new ServiceVote(UUID.randomUUID(), serviceId, accountId, direction, Instant.now(), 0);
  }

  public ServiceVote withServiceId(UUID value) {
    return new ServiceVote(id, value, accountId, direction, createdAt, version);
  }

  public ServiceVote withAccountId(UUID value) {
    return new ServiceVote(id, serviceId, value, direction, createdAt, version);
  }

  public ServiceVote withDirection(int value) {
    return new ServiceVote(id, serviceId, accountId, value, createdAt, version);
  }
}
