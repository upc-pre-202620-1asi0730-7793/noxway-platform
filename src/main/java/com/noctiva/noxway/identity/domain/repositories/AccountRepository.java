package com.noctiva.noxway.identity.domain.repositories;

import com.noctiva.noxway.identity.domain.model.aggregates.Account;
import com.noctiva.noxway.shared.domain.DomainRepository;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends DomainRepository<Account> {
  Optional<Account> lockById(UUID id);

  java.util.List<Account> findByEmail(String value);
}
