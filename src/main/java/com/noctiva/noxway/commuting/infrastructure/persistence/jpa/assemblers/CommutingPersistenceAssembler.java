package com.noctiva.noxway.commuting.infrastructure.persistence.jpa.assemblers;

import com.noctiva.noxway.commuting.domain.model.aggregates.*;
import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.entities.*;

public final class CommutingPersistenceAssembler {
  private CommutingPersistenceAssembler() {}

  public static Commute toDomain(CommutePersistenceEntity e) {
    return Commute.reconstitute(
        e.getId(),
        e.getWorkerId(),
        e.getOrigin(),
        e.getDestination(),
        e.getTransport(),
        e.getStartTime(),
        e.getEstimatedArrivalTime(),
        e.getToleranceMinutes(),
        e.getStatus(),
        e.getArrivalTime(),
        e.getDelayUntil(),
        e.getCreatedAt(),
        e.getVersion());
  }

  public static CommutePersistenceEntity toPersistence(Commute c) {
    var e = new CommutePersistenceEntity();
    e.setId(c.getId());
    e.setWorkerId(c.getWorkerId());
    e.setOrigin(c.getOrigin());
    e.setDestination(c.getDestination());
    e.setTransport(c.getTransport());
    e.setStartTime(c.getStartTime());
    e.setEstimatedArrivalTime(c.getEstimatedArrivalTime());
    e.setToleranceMinutes(c.getToleranceMinutes());
    e.setStatus(c.getStatus());
    e.setArrivalTime(c.getArrivalTime());
    e.setDelayUntil(c.getDelayUntil());
    e.setCreatedAt(c.getCreatedAt());
    e.setVersion(c.getVersion());
    return e;
  }

  public static TelemetryPing toDomain(TelemetryPingPersistenceEntity e) {
    return TelemetryPing.reconstitute(
        e.getId(),
        e.getCommuteId(),
        e.getLatitude(),
        e.getLongitude(),
        e.getSpeed(),
        e.getRecordedAt(),
        e.getCreatedAt(),
        e.getVersion());
  }

  public static TelemetryPingPersistenceEntity toPersistence(TelemetryPing p) {
    var e = new TelemetryPingPersistenceEntity();
    e.setId(p.getId());
    e.setCommuteId(p.getCommuteId());
    e.setLatitude(p.getLatitude());
    e.setLongitude(p.getLongitude());
    e.setSpeed(p.getSpeed());
    e.setRecordedAt(p.getRecordedAt());
    e.setCreatedAt(p.getCreatedAt());
    e.setVersion(p.getVersion());
    return e;
  }

  public static SafetyIncident toDomain(SafetyIncidentPersistenceEntity e) {
    return SafetyIncident.reconstitute(
        e.getId(),
        e.getCommuteId(),
        e.getWorkerId(),
        e.getType(),
        e.getDescription(),
        e.getStatus(),
        e.getReportedAt(),
        e.getCreatedAt(),
        e.getVersion());
  }

  public static SafetyIncidentPersistenceEntity toPersistence(SafetyIncident i) {
    var e = new SafetyIncidentPersistenceEntity();
    e.setId(i.getId());
    e.setCommuteId(i.getCommuteId());
    e.setWorkerId(i.getWorkerId());
    e.setType(i.getType());
    e.setDescription(i.getDescription());
    e.setStatus(i.getStatus());
    e.setReportedAt(i.getReportedAt());
    e.setCreatedAt(i.getCreatedAt());
    e.setVersion(i.getVersion());
    return e;
  }

  public static Alert toDomain(AlertPersistenceEntity e) {
    return Alert.reconstitute(
        e.getId(),
        e.getCommuteId(),
        e.getIncidentId(),
        e.getContactId(),
        e.getChannel(),
        e.getStatus(),
        e.getMessage(),
        e.getDeliveredAt(),
        e.getCreatedAt(),
        e.getVersion());
  }

  public static AlertPersistenceEntity toPersistence(Alert a) {
    var e = new AlertPersistenceEntity();
    e.setId(a.getId());
    e.setCommuteId(a.getCommuteId());
    e.setIncidentId(a.getIncidentId());
    e.setContactId(a.getContactId());
    e.setChannel(a.getChannel());
    e.setStatus(a.getStatus());
    e.setMessage(a.getMessage());
    e.setDeliveredAt(a.getDeliveredAt());
    e.setCreatedAt(a.getCreatedAt());
    e.setVersion(a.getVersion());
    return e;
  }
}
