package com.noctiva.noxway.community.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.community.domain.model.aggregates.RiskZone;
import com.noctiva.noxway.community.domain.repositories.RiskZoneRepository;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.assemblers.CommunityPersistenceAssembler;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.repositories.RiskZonePersistenceRepository;
import com.noctiva.noxway.shared.infrastructure.events.DomainEventPublisher;
import com.noctiva.noxway.shared.infrastructure.persistence.jpa.adapters.OptimisticJpaSaveSupport;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class RiskZoneRepositoryImpl implements RiskZoneRepository {

  private final RiskZonePersistenceRepository jpa;
  private final DomainEventPublisher events;

  public RiskZoneRepositoryImpl(
      RiskZonePersistenceRepository jpa, DomainEventPublisher events) {
    this.jpa = jpa;
    this.events = events;
  }

  @Override
  public Optional<RiskZone> findById(UUID id) {
    return jpa.findById(id).map(CommunityPersistenceAssembler::toDomain);
  }

  @Override
  public List<RiskZone> findAll() {
    return jpa.findAll().stream().map(CommunityPersistenceAssembler::toDomain).toList();
  }

  @Override
  public RiskZone save(RiskZone aggregate) {
    return OptimisticJpaSaveSupport.save(
        jpa,
        aggregate.getId(),
        aggregate.getVersion(),
        aggregate,
        CommunityPersistenceAssembler::toPersistence,
        CommunityPersistenceAssembler::toDomain,
        events,
        RiskZone.class);
  }

  @Override
  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  @Override
  public List<RiskZone> findByReportedBy(UUID value) {
    return jpa.findByReportedBy(value).stream()
        .map(CommunityPersistenceAssembler::toDomain)
        .toList();
  }

  @Override
  public List<RiskZone> findByStatus(String value) {
    return jpa.findByStatus(value).stream().map(CommunityPersistenceAssembler::toDomain).toList();
  }
}
