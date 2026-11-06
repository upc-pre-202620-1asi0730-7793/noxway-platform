package com.noctiva.noxway.community.infrastructure.persistence.jpa.mappers;

import com.noctiva.noxway.community.domain.model.aggregates.CommunityPost;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.entities.CommunityPostJpaEntity;

public final class CommunityPostPersistenceMapper {
  private CommunityPostPersistenceMapper() {}

  public static CommunityPost toDomain(CommunityPostJpaEntity e) {
    return new CommunityPost(
        e.id, e.authorId, e.title, e.content, e.district, e.createdAt, e.version);
  }

  public static void copy(CommunityPost a, CommunityPostJpaEntity e) {
    e.id = a.id();
    e.authorId = a.authorId();
    e.title = a.title();
    e.content = a.content();
    e.district = a.district();
    e.createdAt = a.createdAt();
  }
}
