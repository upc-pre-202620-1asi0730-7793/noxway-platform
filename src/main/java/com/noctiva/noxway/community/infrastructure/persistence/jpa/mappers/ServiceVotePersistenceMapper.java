package com.noctiva.noxway.community.infrastructure.persistence.jpa.mappers;

import com.noctiva.noxway.community.domain.model.aggregates.ServiceVote;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.entities.ServiceVoteJpaEntity;

public final class ServiceVotePersistenceMapper {
  private ServiceVotePersistenceMapper() {}

  public static ServiceVote toDomain(ServiceVoteJpaEntity e) {
    return new ServiceVote(e.id, e.serviceId, e.accountId, e.direction, e.createdAt, e.version);
  }

  public static void copy(ServiceVote a, ServiceVoteJpaEntity e) {
    e.id = a.id();
    e.serviceId = a.serviceId();
    e.accountId = a.accountId();
    e.direction = a.direction();
    e.createdAt = a.createdAt();
  }
}
