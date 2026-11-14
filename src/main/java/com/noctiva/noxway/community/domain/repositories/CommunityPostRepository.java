package com.noctiva.noxway.community.domain.repositories;

import com.noctiva.noxway.community.domain.model.aggregates.CommunityPost;
import com.noctiva.noxway.shared.domain.DomainRepository;
import java.util.UUID;

public interface CommunityPostRepository extends DomainRepository<CommunityPost> {
  java.util.List<CommunityPost> findByAuthorId(UUID value);
}
