package com.noctiva.noxway.community.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.community.infrastructure.persistence.jpa.entities.ServicePointJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicePointJpaRepository extends JpaRepository<ServicePointJpaEntity, UUID> {
  java.util.List<ServicePointJpaEntity> findByReportedBy(UUID value);

  java.util.List<ServicePointJpaEntity> findByStatus(String value);
}
