package com.noctiva.noxway.identity.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.identity.infrastructure.persistence.jpa.entities.AccountJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountJpaRepository extends JpaRepository<AccountJpaEntity, UUID> {
  @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
  @org.springframework.data.jpa.repository.Query(
      "select a from AccountJpaEntity a where a.id = :id")
  java.util.Optional<AccountJpaEntity> lockById(
      @org.springframework.data.repository.query.Param("id") UUID id);

  java.util.List<AccountJpaEntity> findByEmail(String value);
}
