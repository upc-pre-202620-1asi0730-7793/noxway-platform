package com.noctiva.noxway.profiles.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.profiles.infrastructure.persistence.jpa.entities.AccountPersistenceEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountPersistenceRepository
    extends JpaRepository<AccountPersistenceEntity, UUID> {
  Optional<AccountPersistenceEntity> findByEmail(String email);

  boolean existsByEmail(String email);

  List<AccountPersistenceEntity> findByActiveTrueOrderByCreatedAtAsc();
}
