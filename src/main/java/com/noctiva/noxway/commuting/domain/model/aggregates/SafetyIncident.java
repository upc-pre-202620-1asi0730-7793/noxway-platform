package com.noctiva.noxway.commuting.domain.model.aggregates;

import static com.noctiva.noxway.shared.domain.Rules.require;

import com.noctiva.noxway.commuting.domain.model.events.SafetyIncidentTriggeredEvent;
import com.noctiva.noxway.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class SafetyIncident extends AbstractDomainAggregateRoot<SafetyIncident> {

  private final UUID id;
  private final UUID commuteId;
  private final UUID workerId;
  private final String type;
  private final String description;
  private String status;
  private final Instant reportedAt;
  private final Instant createdAt;
  private final long version;

  private SafetyIncident(
      UUID id,
      UUID commuteId,
      UUID workerId,
      String type,
      String description,
      String status,
      Instant reportedAt,
      Instant createdAt,
      long version) {
    this.id = id;
    this.commuteId = commuteId;
    this.workerId = workerId;
    this.type = type;
    this.description = description;
    this.status = status;
    this.reportedAt = reportedAt;
    this.createdAt = createdAt;
    this.version = version;
  }

  public static SafetyIncident report(
      UUID commuteId, UUID workerId, String type, String description, Instant reportedAt) {
    Objects.requireNonNull(commuteId);
    Objects.requireNonNull(workerId);
    require(description != null && !description.isBlank(), "Incident description is required");
    var incident =
        new SafetyIncident(
            UUID.randomUUID(),
            commuteId,
            workerId,
            type,
            description.trim(),
            "OPEN",
            reportedAt,
            Instant.now(),
            0L);
    incident.registerEvent(
        new SafetyIncidentTriggeredEvent(
            incident.id, commuteId, workerId, type, incident.description, reportedAt));
    return incident;
  }

  public static SafetyIncident create(
      UUID commuteId,
      UUID workerId,
      String type,
      String description,
      String status,
      Instant reportedAt) {
    return new SafetyIncident(
        UUID.randomUUID(),
        commuteId,
        workerId,
        type,
        description,
        status,
        reportedAt,
        Instant.now(),
        0L);
  }

  public static SafetyIncident reconstitute(
      UUID id,
      UUID commuteId,
      UUID workerId,
      String type,
      String description,
      String status,
      Instant reportedAt,
      Instant createdAt,
      long version) {
    return new SafetyIncident(
        id, commuteId, workerId, type, description, status, reportedAt, createdAt, version);
  }

  public void resolve() {
    require("OPEN".equals(status), "Incident is already resolved");
    this.status = "RESOLVED";
  }

  public UUID getId() {
    return id;
  }

  public UUID getCommuteId() {
    return commuteId;
  }

  public UUID getWorkerId() {
    return workerId;
  }

  public String getType() {
    return type;
  }

  public String getDescription() {
    return description;
  }

  public String getStatus() {
    return status;
  }

  public Instant getReportedAt() {
    return reportedAt;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public long getVersion() {
    return version;
  }
}
