package com.noctiva.noxway.moderation.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.moderation.domain.model.aggregates.CommunityReport;
import com.noctiva.noxway.moderation.domain.repositories.CommunityReportRepository;
import com.noctiva.noxway.moderation.infrastructure.persistence.jpa.assemblers.ModerationPersistenceAssembler;
import com.noctiva.noxway.moderation.infrastructure.persistence.jpa.repositories.CommunityReportPersistenceRepository;
import com.noctiva.noxway.shared.infrastructure.events.DomainEventPublisher;
import com.noctiva.noxway.shared.infrastructure.persistence.jpa.adapters.OptimisticJpaSaveSupport;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class CommunityReportRepositoryImpl implements CommunityReportRepository {

  private final CommunityReportPersistenceRepository jpa;
  private final DomainEventPublisher events;

  public CommunityReportRepositoryImpl(
      CommunityReportPersistenceRepository jpa, DomainEventPublisher events) {
    this.jpa = jpa;
    this.events = events;
  }

  @Override
  public Optional<CommunityReport> findById(UUID id) {
    return jpa.findById(id).map(ModerationPersistenceAssembler::toDomain);
  }

  @Override
  public List<CommunityReport> findAll() {
    return jpa.findAll().stream().map(ModerationPersistenceAssembler::toDomain).toList();
  }

  @Override
  public CommunityReport save(CommunityReport aggregate) {
    return OptimisticJpaSaveSupport.save(
        jpa,
        aggregate.getId(),
        aggregate.getVersion(),
        aggregate,
        ModerationPersistenceAssembler::toPersistence,
        ModerationPersistenceAssembler::toDomain,
        events,
        CommunityReport.class);
  }

  @Override
  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  @Override
  public List<CommunityReport> findByReportedBy(UUID value) {
    return jpa.findByReportedBy(value).stream()
        .map(ModerationPersistenceAssembler::toDomain)
        .toList();
  }

  @Override
  public List<CommunityReport> findByTargetId(UUID value) {
    return jpa.findByTargetId(value).stream()
        .map(ModerationPersistenceAssembler::toDomain)
        .toList();
  }

  @Override
  public List<CommunityReport> findByStatus(String value) {
    return jpa.findByStatus(value).stream().map(ModerationPersistenceAssembler::toDomain).toList();
  }
}
