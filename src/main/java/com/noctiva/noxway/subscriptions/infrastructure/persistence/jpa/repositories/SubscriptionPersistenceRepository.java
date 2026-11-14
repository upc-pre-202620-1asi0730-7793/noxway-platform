package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.SubscriptionPersistenceEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionPersistenceRepository
    extends JpaRepository<SubscriptionPersistenceEntity, UUID> {
  List<SubscriptionPersistenceEntity> findByWorkerId(UUID value);

  List<SubscriptionPersistenceEntity> findByPlanId(UUID value);

  List<SubscriptionPersistenceEntity> findByStatus(String value);
}
