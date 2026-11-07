package com.noctiva.noxway.community.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "community_posts")
public class CommunityPostJpaEntity {
  @Id
  @Column(name = "id", nullable = false)
  public UUID id;

  @Column(name = "author_id", nullable = false)
  public UUID authorId;

  @Column(name = "title", nullable = false, length = 150)
  public String title;

  @Column(name = "content", nullable = false, length = 2000)
  public String content;

  @Column(name = "district", nullable = false, length = 100)
  public String district;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Version public long version;
}
