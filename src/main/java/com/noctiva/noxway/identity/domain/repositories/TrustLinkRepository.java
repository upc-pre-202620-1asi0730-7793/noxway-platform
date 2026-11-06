package com.noctiva.noxway.identity.domain.repositories;

import com.noctiva.noxway.identity.domain.model.aggregates.TrustLink;
import com.noctiva.noxway.shared.domain.DomainRepository;
import java.util.UUID;

public interface TrustLinkRepository extends DomainRepository<TrustLink> {
  java.util.List<TrustLink> findByWorkerId(UUID value);

  java.util.List<TrustLink> findByContactId(UUID value);

  java.util.List<TrustLink> findByStatus(String value);
}
