package com.noctiva.noxway.iam.domain.repositories;

import com.noctiva.noxway.iam.domain.model.aggregates.PasswordReset;
import com.noctiva.noxway.shared.domain.DomainRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for password reset tokens.
 */
public interface PasswordResetRepository extends DomainRepository<PasswordReset> {
  Optional<PasswordReset> findByTokenHash(String tokenHash);

  List<PasswordReset> findByAccountId(UUID accountId);
}
