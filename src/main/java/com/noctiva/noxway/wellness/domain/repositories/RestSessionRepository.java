package com.noctiva.noxway.wellness.domain.repositories;

import com.noctiva.noxway.shared.domain.DomainRepository;
import com.noctiva.noxway.wellness.domain.model.aggregates.RestSession;
import java.util.UUID;

public interface RestSessionRepository extends DomainRepository<RestSession> {
  java.util.List<RestSession> findByWorkerId(UUID value);
}
