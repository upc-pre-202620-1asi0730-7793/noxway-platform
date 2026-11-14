package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.SubscriptionMemberPersistenceEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionMemberPersistenceRepository
    extends JpaRepository<SubscriptionMemberPersistenceEntity, UUID> {
  List<SubscriptionMemberPersistenceEntity> findBySubscriptionId(UUID value);

  List<SubscriptionMemberPersistenceEntity> findByWorkerId(UUID value);

  List<SubscriptionMemberPersistenceEntity> findByStatus(String value);
}
