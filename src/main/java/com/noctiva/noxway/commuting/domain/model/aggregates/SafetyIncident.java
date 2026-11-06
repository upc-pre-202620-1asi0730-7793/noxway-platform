package com.noctiva.noxway.commuting.domain.model.aggregates;

import java.time.Instant;
import java.util.UUID;

public record SafetyIncident(
    UUID id,
    UUID commuteId,
    UUID workerId,
    String type,
    String description,
    String status,
    Instant reportedAt,
    Instant createdAt,
    long version) {
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
        0);
  }

  public SafetyIncident withCommuteId(UUID value) {
    return new SafetyIncident(
        id, value, workerId, type, description, status, reportedAt, createdAt, version);
  }

  public SafetyIncident withWorkerId(UUID value) {
    return new SafetyIncident(
        id, commuteId, value, type, description, status, reportedAt, createdAt, version);
  }

  public SafetyIncident withType(String value) {
    return new SafetyIncident(
        id, commuteId, workerId, value, description, status, reportedAt, createdAt, version);
  }

  public SafetyIncident withDescription(String value) {
    return new SafetyIncident(
        id, commuteId, workerId, type, value, status, reportedAt, createdAt, version);
  }

  public SafetyIncident withStatus(String value) {
    return new SafetyIncident(
        id, commuteId, workerId, type, description, value, reportedAt, createdAt, version);
  }

  public SafetyIncident withReportedAt(Instant value) {
    return new SafetyIncident(
        id, commuteId, workerId, type, description, status, value, createdAt, version);
  }
}
