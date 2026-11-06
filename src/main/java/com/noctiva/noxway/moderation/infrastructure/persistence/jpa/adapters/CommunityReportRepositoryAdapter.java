package com.noctiva.noxway.moderation.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.moderation.domain.model.aggregates.CommunityReport;
import com.noctiva.noxway.moderation.domain.repositories.CommunityReportRepository;
import com.noctiva.noxway.moderation.infrastructure.persistence.jpa.entities.CommunityReportJpaEntity;
import com.noctiva.noxway.moderation.infrastructure.persistence.jpa.mappers.CommunityReportPersistenceMapper;
import com.noctiva.noxway.moderation.infrastructure.persistence.jpa.repositories.CommunityReportJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class CommunityReportRepositoryAdapter implements CommunityReportRepository {
  private final CommunityReportJpaRepository jpa;

  public CommunityReportRepositoryAdapter(CommunityReportJpaRepository jpa) {
    this.jpa = jpa;
  }

  public Optional<CommunityReport> findById(UUID id) {
    return jpa.findById(id).map(CommunityReportPersistenceMapper::toDomain);
  }

  public java.util.List<CommunityReport> findAll() {
    return jpa.findAll().stream().map(CommunityReportPersistenceMapper::toDomain).toList();
  }

  public CommunityReport save(CommunityReport a) {
    var e = jpa.findById(a.id()).orElseGet(CommunityReportJpaEntity::new);
    if (e.id != null && e.version != a.version())
      throw new org.springframework.orm.ObjectOptimisticLockingFailureException(
          CommunityReport.class, a.id());
    CommunityReportPersistenceMapper.copy(a, e);
    return CommunityReportPersistenceMapper.toDomain(jpa.saveAndFlush(e));
  }

  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  public java.util.List<CommunityReport> findByReportedBy(UUID value) {
    return jpa.findByReportedBy(value).stream()
        .map(CommunityReportPersistenceMapper::toDomain)
        .toList();
  }

  public java.util.List<CommunityReport> findByTargetId(UUID value) {
    return jpa.findByTargetId(value).stream()
        .map(CommunityReportPersistenceMapper::toDomain)
        .toList();
  }

  public java.util.List<CommunityReport> findByStatus(String value) {
    return jpa.findByStatus(value).stream()
        .map(CommunityReportPersistenceMapper::toDomain)
        .toList();
  }
}
