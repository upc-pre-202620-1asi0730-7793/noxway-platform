package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.SubscriptionMemberJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionMemberJpaRepository
    extends JpaRepository<SubscriptionMemberJpaEntity, UUID> {
  java.util.List<SubscriptionMemberJpaEntity> findBySubscriptionId(UUID value);

  java.util.List<SubscriptionMemberJpaEntity> findByWorkerId(UUID value);

  java.util.List<SubscriptionMemberJpaEntity> findByStatus(String value);
}
