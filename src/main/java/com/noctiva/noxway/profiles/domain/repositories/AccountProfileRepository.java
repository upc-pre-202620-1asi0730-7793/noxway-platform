package com.noctiva.noxway.profiles.domain.repositories;

import com.noctiva.noxway.profiles.domain.model.aggregates.AccountProfile;
import com.noctiva.noxway.shared.domain.DomainRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountProfileRepository extends DomainRepository<AccountProfile> {
  Optional<AccountProfile> findByEmail(String email);

  boolean existsByEmail(String email);

  List<AccountProfile> findByActiveTrueOrderByCreatedAtAsc();
}
