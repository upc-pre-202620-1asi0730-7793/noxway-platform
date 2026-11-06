package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.SubscriptionJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionJpaRepository extends JpaRepository<SubscriptionJpaEntity, UUID> {
  java.util.List<SubscriptionJpaEntity> findByWorkerId(UUID value);

  java.util.List<SubscriptionJpaEntity> findByPlanId(UUID value);

  java.util.List<SubscriptionJpaEntity> findByStatus(String value);
}
