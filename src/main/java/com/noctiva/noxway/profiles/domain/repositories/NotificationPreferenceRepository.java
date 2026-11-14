package com.noctiva.noxway.profiles.domain.repositories;

import com.noctiva.noxway.profiles.domain.model.aggregates.NotificationPreference;
import com.noctiva.noxway.shared.domain.DomainRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationPreferenceRepository
    extends DomainRepository<NotificationPreference> {
  List<NotificationPreference> findByAccountId(UUID accountId);

  Optional<NotificationPreference> findByAccountIdAndChannel(UUID accountId, String channel);
}
