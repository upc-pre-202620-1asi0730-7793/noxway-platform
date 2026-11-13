package com.noctiva.noxway.community.infrastructure.persistence.jpa.entities;

import com.noctiva.noxway.shared.infrastructure.persistence.jpa.entities.AuditableUuidPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "community_posts", schema = "community_data_schema")
public class CommunityPostPersistenceEntity extends AuditableUuidPersistenceEntity {

  @Column(name = "author_id", nullable = false)
  private UUID authorId;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "content", nullable = false)
  private String content;

  @Column(name = "district", nullable = false)
  private String district;

  public CommunityPostPersistenceEntity() {}

  public UUID getAuthorId() {
    return authorId;
  }

  public void setAuthorId(UUID authorId) {
    this.authorId = authorId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getDistrict() {
    return district;
  }

  public void setDistrict(String district) {
    this.district = district;
  }
}
