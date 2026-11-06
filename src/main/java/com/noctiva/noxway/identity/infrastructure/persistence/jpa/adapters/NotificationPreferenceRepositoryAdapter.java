package com.noctiva.noxway.identity.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.identity.domain.model.aggregates.NotificationPreference;
import com.noctiva.noxway.identity.domain.repositories.NotificationPreferenceRepository;
import com.noctiva.noxway.identity.infrastructure.persistence.jpa.entities.NotificationPreferenceJpaEntity;
import com.noctiva.noxway.identity.infrastructure.persistence.jpa.mappers.NotificationPreferencePersistenceMapper;
import com.noctiva.noxway.identity.infrastructure.persistence.jpa.repositories.NotificationPreferenceJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class NotificationPreferenceRepositoryAdapter implements NotificationPreferenceRepository {
  private final NotificationPreferenceJpaRepository jpa;

  public NotificationPreferenceRepositoryAdapter(NotificationPreferenceJpaRepository jpa) {
    this.jpa = jpa;
  }

  public Optional<NotificationPreference> findById(UUID id) {
    return jpa.findById(id).map(NotificationPreferencePersistenceMapper::toDomain);
  }

  public java.util.List<NotificationPreference> findAll() {
    return jpa.findAll().stream().map(NotificationPreferencePersistenceMapper::toDomain).toList();
  }

  public NotificationPreference save(NotificationPreference a) {
    var e = jpa.findById(a.id()).orElseGet(NotificationPreferenceJpaEntity::new);
    if (e.id != null && e.version != a.version())
      throw new org.springframework.orm.ObjectOptimisticLockingFailureException(
          NotificationPreference.class, a.id());
    NotificationPreferencePersistenceMapper.copy(a, e);
    return NotificationPreferencePersistenceMapper.toDomain(jpa.saveAndFlush(e));
  }

  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  public java.util.List<NotificationPreference> findByAccountId(UUID value) {
    return jpa.findByAccountId(value).stream()
        .map(NotificationPreferencePersistenceMapper::toDomain)
        .toList();
  }
}
