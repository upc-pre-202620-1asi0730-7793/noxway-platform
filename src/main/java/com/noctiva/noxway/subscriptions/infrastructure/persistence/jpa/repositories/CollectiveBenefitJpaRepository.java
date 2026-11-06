package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.CollectiveBenefitJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectiveBenefitJpaRepository
    extends JpaRepository<CollectiveBenefitJpaEntity, UUID> {}
