package com.noctiva.noxway.profiles.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.profiles.infrastructure.persistence.jpa.entities.TrustLinkPersistenceEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrustLinkPersistenceRepository
    extends JpaRepository<TrustLinkPersistenceEntity, UUID> {
  List<TrustLinkPersistenceEntity> findByWorkerId(UUID workerId);

  List<TrustLinkPersistenceEntity> findByContactId(UUID contactId);

  Optional<TrustLinkPersistenceEntity> findByWorkerIdAndContactId(UUID workerId, UUID contactId);

  boolean existsByWorkerIdAndContactIdAndStatus(UUID workerId, UUID contactId, String status);
}
