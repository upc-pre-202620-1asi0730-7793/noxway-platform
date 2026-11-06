package com.noctiva.noxway.community.domain.model.aggregates;

import java.time.Instant;
import java.util.UUID;

public record CommunityPost(
    UUID id,
    UUID authorId,
    String title,
    String content,
    String district,
    Instant createdAt,
    long version) {
  public static CommunityPost create(UUID authorId, String title, String content, String district) {
    return new CommunityPost(
        UUID.randomUUID(), authorId, title, content, district, Instant.now(), 0);
  }

  public CommunityPost withAuthorId(UUID value) {
    return new CommunityPost(id, value, title, content, district, createdAt, version);
  }

  public CommunityPost withTitle(String value) {
    return new CommunityPost(id, authorId, value, content, district, createdAt, version);
  }

  public CommunityPost withContent(String value) {
    return new CommunityPost(id, authorId, title, value, district, createdAt, version);
  }

  public CommunityPost withDistrict(String value) {
    return new CommunityPost(id, authorId, title, content, value, createdAt, version);
  }
}
