package com.noctiva.noxway.identity.domain.repositories;

import com.noctiva.noxway.identity.domain.model.aggregates.PasswordReset;
import com.noctiva.noxway.shared.domain.DomainRepository;
import java.util.UUID;

public interface PasswordResetRepository extends DomainRepository<PasswordReset> {
  java.util.List<PasswordReset> findByAccountId(UUID value);

  java.util.List<PasswordReset> findByTokenHash(String value);
}
