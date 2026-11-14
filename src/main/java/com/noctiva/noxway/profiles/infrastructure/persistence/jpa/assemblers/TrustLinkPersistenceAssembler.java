package com.noctiva.noxway.profiles.infrastructure.persistence.jpa.assemblers;

import com.noctiva.noxway.profiles.domain.model.aggregates.TrustLink;
import com.noctiva.noxway.profiles.infrastructure.persistence.jpa.entities.TrustLinkPersistenceEntity;

public final class TrustLinkPersistenceAssembler {
  private TrustLinkPersistenceAssembler() {}

  public static TrustLink toDomain(TrustLinkPersistenceEntity e) {
    return TrustLink.reconstitute(
        e.getId(),
        e.getWorkerId(),
        e.getContactId(),
        e.getStatus(),
        e.getAcceptedAt(),
        e.getCreatedAt(),
        e.getVersion());
  }

  public static TrustLinkPersistenceEntity toPersistence(TrustLink l) {
    var e = new TrustLinkPersistenceEntity();
    e.setId(l.getId());
    e.setWorkerId(l.getWorkerId());
    e.setContactId(l.getContactId());
    e.setStatus(l.getStatus());
    e.setAcceptedAt(l.getAcceptedAt());
    e.setCreatedAt(l.getCreatedAt());
    e.setVersion(l.getVersion());
    return e;
  }
}
