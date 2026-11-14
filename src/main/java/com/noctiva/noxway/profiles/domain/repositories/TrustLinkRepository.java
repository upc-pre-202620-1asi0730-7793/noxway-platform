package com.noctiva.noxway.profiles.domain.repositories;

import com.noctiva.noxway.profiles.domain.model.aggregates.TrustLink;
import com.noctiva.noxway.shared.domain.DomainRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrustLinkRepository extends DomainRepository<TrustLink> {
  List<TrustLink> findByWorkerId(UUID workerId);

  List<TrustLink> findByContactId(UUID contactId);

  Optional<TrustLink> findByWorkerIdAndContactId(UUID workerId, UUID contactId);

  boolean existsByWorkerIdAndContactIdAndStatus(UUID workerId, UUID contactId, String status);
}
