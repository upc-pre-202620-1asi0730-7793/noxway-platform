package com.noctiva.noxway.community.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.community.domain.model.aggregates.ServiceVote;
import com.noctiva.noxway.community.domain.repositories.ServiceVoteRepository;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.entities.ServiceVoteJpaEntity;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.mappers.ServiceVotePersistenceMapper;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.repositories.ServiceVoteJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ServiceVoteRepositoryAdapter implements ServiceVoteRepository {
  private final ServiceVoteJpaRepository jpa;

  public ServiceVoteRepositoryAdapter(ServiceVoteJpaRepository jpa) {
    this.jpa = jpa;
  }

  public Optional<ServiceVote> findById(UUID id) {
    return jpa.findById(id).map(ServiceVotePersistenceMapper::toDomain);
  }

  public java.util.List<ServiceVote> findAll() {
    return jpa.findAll().stream().map(ServiceVotePersistenceMapper::toDomain).toList();
  }

  public ServiceVote save(ServiceVote a) {
    var e = jpa.findById(a.id()).orElseGet(ServiceVoteJpaEntity::new);
    if (e.id != null && e.version != a.version())
      throw new org.springframework.orm.ObjectOptimisticLockingFailureException(
          ServiceVote.class, a.id());
    ServiceVotePersistenceMapper.copy(a, e);
    return ServiceVotePersistenceMapper.toDomain(jpa.saveAndFlush(e));
  }

  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  public java.util.List<ServiceVote> findByServiceId(UUID value) {
    return jpa.findByServiceId(value).stream().map(ServiceVotePersistenceMapper::toDomain).toList();
  }

  public java.util.List<ServiceVote> findByAccountId(UUID value) {
    return jpa.findByAccountId(value).stream().map(ServiceVotePersistenceMapper::toDomain).toList();
  }
}
