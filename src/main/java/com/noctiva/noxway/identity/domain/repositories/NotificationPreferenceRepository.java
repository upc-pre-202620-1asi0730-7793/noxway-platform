package com.noctiva.noxway.identity.domain.repositories;

import com.noctiva.noxway.identity.domain.model.aggregates.NotificationPreference;
import com.noctiva.noxway.shared.domain.DomainRepository;
import java.util.UUID;

public interface NotificationPreferenceRepository extends DomainRepository<NotificationPreference> {
  java.util.List<NotificationPreference> findByAccountId(UUID value);
}
