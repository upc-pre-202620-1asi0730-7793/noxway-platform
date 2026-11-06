package com.noctiva.noxway.identity.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.identity.domain.model.aggregates.TrustLink;
import com.noctiva.noxway.identity.domain.repositories.TrustLinkRepository;
import com.noctiva.noxway.identity.infrastructure.persistence.jpa.entities.TrustLinkJpaEntity;
import com.noctiva.noxway.identity.infrastructure.persistence.jpa.mappers.TrustLinkPersistenceMapper;
import com.noctiva.noxway.identity.infrastructure.persistence.jpa.repositories.TrustLinkJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class TrustLinkRepositoryAdapter implements TrustLinkRepository {
  private final TrustLinkJpaRepository jpa;

  public TrustLinkRepositoryAdapter(TrustLinkJpaRepository jpa) {
    this.jpa = jpa;
  }

  public Optional<TrustLink> findById(UUID id) {
    return jpa.findById(id).map(TrustLinkPersistenceMapper::toDomain);
  }

  public java.util.List<TrustLink> findAll() {
    return jpa.findAll().stream().map(TrustLinkPersistenceMapper::toDomain).toList();
  }

  public TrustLink save(TrustLink a) {
    var e = jpa.findById(a.id()).orElseGet(TrustLinkJpaEntity::new);
    if (e.id != null && e.version != a.version())
      throw new org.springframework.orm.ObjectOptimisticLockingFailureException(
          TrustLink.class, a.id());
    TrustLinkPersistenceMapper.copy(a, e);
    return TrustLinkPersistenceMapper.toDomain(jpa.saveAndFlush(e));
  }

  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  public java.util.List<TrustLink> findByWorkerId(UUID value) {
    return jpa.findByWorkerId(value).stream().map(TrustLinkPersistenceMapper::toDomain).toList();
  }

  public java.util.List<TrustLink> findByContactId(UUID value) {
    return jpa.findByContactId(value).stream().map(TrustLinkPersistenceMapper::toDomain).toList();
  }

  public java.util.List<TrustLink> findByStatus(String value) {
    return jpa.findByStatus(value).stream().map(TrustLinkPersistenceMapper::toDomain).toList();
  }
}
