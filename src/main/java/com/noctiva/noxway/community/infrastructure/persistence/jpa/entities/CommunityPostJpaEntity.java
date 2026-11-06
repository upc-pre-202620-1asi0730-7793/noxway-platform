package com.noctiva.noxway.community.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "community_posts", schema = "community_data_schema")
public class CommunityPostJpaEntity {
  @Id
  @Column(name = "id", nullable = false)
  public UUID id;

  @Column(name = "author_id", nullable = false)
  public UUID authorId;

  @Column(name = "title", nullable = false)
  public String title;

  @Column(name = "content", nullable = false)
  public String content;

  @Column(name = "district", nullable = false)
  public String district;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Version public long version;
}
