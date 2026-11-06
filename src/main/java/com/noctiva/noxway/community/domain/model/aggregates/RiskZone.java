package com.noctiva.noxway.community.domain.model.aggregates;

import java.time.Instant;
import java.util.UUID;

public record RiskZone(
    UUID id,
    UUID reportedBy,
    String description,
    String riskLevel,
    double latitude,
    double longitude,
    String status,
    Instant createdAt,
    long version) {
  public static RiskZone create(
      UUID reportedBy,
      String description,
      String riskLevel,
      double latitude,
      double longitude,
      String status) {
    return new RiskZone(
        UUID.randomUUID(),
        reportedBy,
        description,
        riskLevel,
        latitude,
        longitude,
        status,
        Instant.now(),
        0);
  }

  public RiskZone withReportedBy(UUID value) {
    return new RiskZone(
        id, value, description, riskLevel, latitude, longitude, status, createdAt, version);
  }

  public RiskZone withDescription(String value) {
    return new RiskZone(
        id, reportedBy, value, riskLevel, latitude, longitude, status, createdAt, version);
  }

  public RiskZone withRiskLevel(String value) {
    return new RiskZone(
        id, reportedBy, description, value, latitude, longitude, status, createdAt, version);
  }

  public RiskZone withLatitude(double value) {
    return new RiskZone(
        id, reportedBy, description, riskLevel, value, longitude, status, createdAt, version);
  }

  public RiskZone withLongitude(double value) {
    return new RiskZone(
        id, reportedBy, description, riskLevel, latitude, value, status, createdAt, version);
  }

  public RiskZone withStatus(String value) {
    return new RiskZone(
        id, reportedBy, description, riskLevel, latitude, longitude, value, createdAt, version);
  }
}
