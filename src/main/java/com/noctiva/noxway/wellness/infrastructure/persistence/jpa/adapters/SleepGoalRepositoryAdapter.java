package com.noctiva.noxway.wellness.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.wellness.domain.model.aggregates.SleepGoal;
import com.noctiva.noxway.wellness.domain.repositories.SleepGoalRepository;
import com.noctiva.noxway.wellness.infrastructure.persistence.jpa.entities.SleepGoalJpaEntity;
import com.noctiva.noxway.wellness.infrastructure.persistence.jpa.mappers.SleepGoalPersistenceMapper;
import com.noctiva.noxway.wellness.infrastructure.persistence.jpa.repositories.SleepGoalJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class SleepGoalRepositoryAdapter implements SleepGoalRepository {
  private final SleepGoalJpaRepository jpa;

  public SleepGoalRepositoryAdapter(SleepGoalJpaRepository jpa) {
    this.jpa = jpa;
  }

  public Optional<SleepGoal> findById(UUID id) {
    return jpa.findById(id).map(SleepGoalPersistenceMapper::toDomain);
  }

  public java.util.List<SleepGoal> findAll() {
    return jpa.findAll().stream().map(SleepGoalPersistenceMapper::toDomain).toList();
  }

  public SleepGoal save(SleepGoal a) {
    var e = jpa.findById(a.id()).orElseGet(SleepGoalJpaEntity::new);
    if (e.id != null && e.version != a.version())
      throw new org.springframework.orm.ObjectOptimisticLockingFailureException(
          SleepGoal.class, a.id());
    SleepGoalPersistenceMapper.copy(a, e);
    return SleepGoalPersistenceMapper.toDomain(jpa.saveAndFlush(e));
  }

  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  public java.util.List<SleepGoal> findByWorkerId(UUID value) {
    return jpa.findByWorkerId(value).stream().map(SleepGoalPersistenceMapper::toDomain).toList();
  }
}
