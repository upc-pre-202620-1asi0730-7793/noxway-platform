package com.noctiva.noxway.shared.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Persistable;

/**
 * Base JPA mapped superclass sharing primary key UUID, creation timestamp, optimistic version lock,
 * and explicit new-vs-existing persistence state for domain-assigned UUID identifiers.
 */
@MappedSuperclass
public abstract class AuditableUuidPersistenceEntity implements Persistable<UUID> {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Version
  private long version;

  @Transient
  private boolean newEntity = true;

  protected AuditableUuidPersistenceEntity() {}

  protected void initializeIdentity(UUID id, Instant createdAt, long version) {
    this.id = id;
    this.createdAt = createdAt;
    this.version = version;
  }

  @Override
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public long getVersion() {
    return version;
  }

  public void setVersion(long version) {
    this.version = version;
  }

  @Override
  @Transient
  public boolean isNew() {
    return newEntity;
  }

  public void markExisting() {
    this.newEntity = false;
  }

  @PostLoad
  @PostPersist
  protected void markNotNew() {
    this.newEntity = false;
  }
}
