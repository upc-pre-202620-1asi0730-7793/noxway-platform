package com.noctiva.noxway.community.domain.repositories;

import com.noctiva.noxway.community.domain.model.aggregates.ServiceVote;
import com.noctiva.noxway.shared.domain.DomainRepository;
import java.util.UUID;

public interface ServiceVoteRepository extends DomainRepository<ServiceVote> {
  java.util.List<ServiceVote> findByServiceId(UUID value);

  java.util.List<ServiceVote> findByAccountId(UUID value);
}
