package com.noctiva.noxway.moderation.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.moderation.domain.model.aggregates.ModerationAction;
import com.noctiva.noxway.moderation.domain.repositories.ModerationActionRepository;
import com.noctiva.noxway.moderation.infrastructure.persistence.jpa.entities.ModerationActionJpaEntity;
import com.noctiva.noxway.moderation.infrastructure.persistence.jpa.mappers.ModerationActionPersistenceMapper;
import com.noctiva.noxway.moderation.infrastructure.persistence.jpa.repositories.ModerationActionJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ModerationActionRepositoryAdapter implements ModerationActionRepository {
  private final ModerationActionJpaRepository jpa;

  public ModerationActionRepositoryAdapter(ModerationActionJpaRepository jpa) {
    this.jpa = jpa;
  }

  public Optional<ModerationAction> findById(UUID id) {
    return jpa.findById(id).map(ModerationActionPersistenceMapper::toDomain);
  }

  public java.util.List<ModerationAction> findAll() {
    return jpa.findAll().stream().map(ModerationActionPersistenceMapper::toDomain).toList();
  }

  public ModerationAction save(ModerationAction a) {
    var e = jpa.findById(a.id()).orElseGet(ModerationActionJpaEntity::new);
    if (e.id != null && e.version != a.version())
      throw new org.springframework.orm.ObjectOptimisticLockingFailureException(
          ModerationAction.class, a.id());
    ModerationActionPersistenceMapper.copy(a, e);
    return ModerationActionPersistenceMapper.toDomain(jpa.saveAndFlush(e));
  }

  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  public java.util.List<ModerationAction> findByReportId(UUID value) {
    return jpa.findByReportId(value).stream()
        .map(ModerationActionPersistenceMapper::toDomain)
        .toList();
  }

  public java.util.List<ModerationAction> findByModeratorId(UUID value) {
    return jpa.findByModeratorId(value).stream()
        .map(ModerationActionPersistenceMapper::toDomain)
        .toList();
  }
}
