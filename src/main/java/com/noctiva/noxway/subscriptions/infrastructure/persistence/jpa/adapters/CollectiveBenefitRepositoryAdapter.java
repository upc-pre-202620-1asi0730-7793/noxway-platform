package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.subscriptions.domain.model.aggregates.CollectiveBenefit;
import com.noctiva.noxway.subscriptions.domain.repositories.CollectiveBenefitRepository;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.CollectiveBenefitJpaEntity;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.mappers.CollectiveBenefitPersistenceMapper;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories.CollectiveBenefitJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class CollectiveBenefitRepositoryAdapter implements CollectiveBenefitRepository {
  private final CollectiveBenefitJpaRepository jpa;

  public CollectiveBenefitRepositoryAdapter(CollectiveBenefitJpaRepository jpa) {
    this.jpa = jpa;
  }

  public Optional<CollectiveBenefit> findById(UUID id) {
    return jpa.findById(id).map(CollectiveBenefitPersistenceMapper::toDomain);
  }

  public java.util.List<CollectiveBenefit> findAll() {
    return jpa.findAll().stream().map(CollectiveBenefitPersistenceMapper::toDomain).toList();
  }

  public CollectiveBenefit save(CollectiveBenefit a) {
    var e = jpa.findById(a.id()).orElseGet(CollectiveBenefitJpaEntity::new);
    if (e.id != null && e.version != a.version())
      throw new org.springframework.orm.ObjectOptimisticLockingFailureException(
          CollectiveBenefit.class, a.id());
    CollectiveBenefitPersistenceMapper.copy(a, e);
    return CollectiveBenefitPersistenceMapper.toDomain(jpa.saveAndFlush(e));
  }

  public void delete(UUID id) {
    jpa.deleteById(id);
  }
}
