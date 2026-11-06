package com.noctiva.noxway.identity.domain.repositories;

import com.noctiva.noxway.identity.domain.model.aggregates.AuthSession;
import com.noctiva.noxway.shared.domain.DomainRepository;
import java.util.UUID;

public interface AuthSessionRepository extends DomainRepository<AuthSession> {
  java.util.List<AuthSession> findByAccountId(UUID value);

  java.util.List<AuthSession> findByTokenHash(String value);
}
