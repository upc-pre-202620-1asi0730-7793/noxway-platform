package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.CollectiveBenefitPersistenceEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectiveBenefitPersistenceRepository
    extends JpaRepository<CollectiveBenefitPersistenceEntity, UUID> {}
