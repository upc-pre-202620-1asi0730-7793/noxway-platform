package com.noctiva.noxway.identity.infrastructure.persistence.jpa.mappers;

import com.noctiva.noxway.identity.domain.model.aggregates.TrustLink;
import com.noctiva.noxway.identity.infrastructure.persistence.jpa.entities.TrustLinkJpaEntity;

public final class TrustLinkPersistenceMapper {
  private TrustLinkPersistenceMapper() {}

  public static TrustLink toDomain(TrustLinkJpaEntity e) {
    return new TrustLink(
        e.id, e.workerId, e.contactId, e.status, e.acceptedAt, e.createdAt, e.version);
  }

  public static void copy(TrustLink a, TrustLinkJpaEntity e) {
    e.id = a.id();
    e.workerId = a.workerId();
    e.contactId = a.contactId();
    e.status = a.status();
    e.acceptedAt = a.acceptedAt();
    e.createdAt = a.createdAt();
  }
}
