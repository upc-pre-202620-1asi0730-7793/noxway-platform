package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.subscriptions.domain.model.aggregates.Plan;
import com.noctiva.noxway.subscriptions.domain.repositories.PlanRepository;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.PlanJpaEntity;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.mappers.PlanPersistenceMapper;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories.PlanJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class PlanRepositoryAdapter implements PlanRepository {
  private final PlanJpaRepository jpa;

  public PlanRepositoryAdapter(PlanJpaRepository jpa) {
    this.jpa = jpa;
  }

  public Optional<Plan> findById(UUID id) {
    return jpa.findById(id).map(PlanPersistenceMapper::toDomain);
  }

  public java.util.List<Plan> findAll() {
    return jpa.findAll().stream().map(PlanPersistenceMapper::toDomain).toList();
  }

  public Plan save(Plan a) {
    var e = jpa.findById(a.id()).orElseGet(PlanJpaEntity::new);
    if (e.id != null && e.version != a.version())
      throw new org.springframework.orm.ObjectOptimisticLockingFailureException(Plan.class, a.id());
    PlanPersistenceMapper.copy(a, e);
    return PlanPersistenceMapper.toDomain(jpa.saveAndFlush(e));
  }

  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  public java.util.List<Plan> findByCode(String value) {
    return jpa.findByCode(value).stream().map(PlanPersistenceMapper::toDomain).toList();
  }
}
