package com.noctiva.noxway.community.domain.model.aggregates;

import static com.noctiva.noxway.shared.domain.Rules.require;

import com.noctiva.noxway.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class CommunityPost extends AbstractDomainAggregateRoot<CommunityPost> {

  private final UUID id;
  private final UUID authorId;
  private final String title;
  private final String content;
  private final String district;
  private final Instant createdAt;
  private final long version;

  private CommunityPost(
      UUID id,
      UUID authorId,
      String title,
      String content,
      String district,
      Instant createdAt,
      long version) {
    this.id = id;
    this.authorId = authorId;
    this.title = title;
    this.content = content;
    this.district = district;
    this.createdAt = createdAt;
    this.version = version;
  }

  public static CommunityPost create(UUID authorId, String title, String content, String district) {
    Objects.requireNonNull(authorId);
    require(title != null && !title.isBlank(), "Post title is required");
    require(content != null && !content.isBlank(), "Post content is required");
    require(district != null && !district.isBlank(), "District is required");
    return new CommunityPost(
        UUID.randomUUID(),
        authorId,
        title.trim(),
        content.trim(),
        district.trim(),
        Instant.now(),
        0L);
  }

  public static CommunityPost reconstitute(
      UUID id,
      UUID authorId,
      String title,
      String content,
      String district,
      Instant createdAt,
      long version) {
    return new CommunityPost(id, authorId, title, content, district, createdAt, version);
  }

  public UUID getId() {
    return id;
  }

  public UUID getAuthorId() {
    return authorId;
  }

  public String getTitle() {
    return title;
  }

  public String getContent() {
    return content;
  }

  public String getDistrict() {
    return district;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public long getVersion() {
    return version;
  }
}
