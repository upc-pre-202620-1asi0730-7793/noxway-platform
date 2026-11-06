package com.noctiva.noxway.community.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.community.domain.model.aggregates.CommunityPost;
import com.noctiva.noxway.community.domain.repositories.CommunityPostRepository;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.entities.CommunityPostJpaEntity;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.mappers.CommunityPostPersistenceMapper;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.repositories.CommunityPostJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class CommunityPostRepositoryAdapter implements CommunityPostRepository {
  private final CommunityPostJpaRepository jpa;

  public CommunityPostRepositoryAdapter(CommunityPostJpaRepository jpa) {
    this.jpa = jpa;
  }

  public Optional<CommunityPost> findById(UUID id) {
    return jpa.findById(id).map(CommunityPostPersistenceMapper::toDomain);
  }

  public java.util.List<CommunityPost> findAll() {
    return jpa.findAll().stream().map(CommunityPostPersistenceMapper::toDomain).toList();
  }

  public CommunityPost save(CommunityPost a) {
    var e = jpa.findById(a.id()).orElseGet(CommunityPostJpaEntity::new);
    if (e.id != null && e.version != a.version())
      throw new org.springframework.orm.ObjectOptimisticLockingFailureException(
          CommunityPost.class, a.id());
    CommunityPostPersistenceMapper.copy(a, e);
    return CommunityPostPersistenceMapper.toDomain(jpa.saveAndFlush(e));
  }

  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  public java.util.List<CommunityPost> findByAuthorId(UUID value) {
    return jpa.findByAuthorId(value).stream()
        .map(CommunityPostPersistenceMapper::toDomain)
        .toList();
  }
}
