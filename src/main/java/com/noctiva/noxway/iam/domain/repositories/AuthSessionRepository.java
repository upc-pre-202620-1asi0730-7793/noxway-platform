package com.noctiva.noxway.iam.domain.repositories;

import com.noctiva.noxway.iam.domain.model.aggregates.AuthSession;
import com.noctiva.noxway.shared.domain.DomainRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for IAM authentication sessions.
 */
public interface AuthSessionRepository extends DomainRepository<AuthSession> {
  Optional<AuthSession> findByTokenHash(String tokenHash);

  List<AuthSession> findByAccountId(UUID accountId);
}
