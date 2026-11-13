package com.noctiva.noxway.community.domain.model.aggregates;

import static com.noctiva.noxway.shared.domain.Rules.*;

import com.noctiva.noxway.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class RiskZone extends AbstractDomainAggregateRoot<RiskZone> {

  private final UUID id;
  private final UUID reportedBy;
  private final String description;
  private final String riskLevel;
  private final double latitude;
  private final double longitude;
  private String status;
  private final Instant createdAt;
  private final long version;

  private RiskZone(
      UUID id,
      UUID reportedBy,
      String description,
      String riskLevel,
      double latitude,
      double longitude,
      String status,
      Instant createdAt,
      long version) {
    this.id = id;
    this.reportedBy = reportedBy;
    this.description = description;
    this.riskLevel = riskLevel;
    this.latitude = latitude;
    this.longitude = longitude;
    this.status = status;
    this.createdAt = createdAt;
    this.version = version;
  }

  public static RiskZone create(
      UUID reportedBy,
      String description,
      String riskLevel,
      double latitude,
      double longitude,
      String status) {
    Objects.requireNonNull(reportedBy);
    require(description != null && !description.isBlank(), "Risk description is required");
    coordinates(latitude, longitude);
    return new RiskZone(
        UUID.randomUUID(),
        reportedBy,
        description.trim(),
        riskLevel,
        latitude,
        longitude,
        status,
        Instant.now(),
        0L);
  }

  public static RiskZone reconstitute(
      UUID id,
      UUID reportedBy,
      String description,
      String riskLevel,
      double latitude,
      double longitude,
      String status,
      Instant createdAt,
      long version) {
    return new RiskZone(
        id, reportedBy, description, riskLevel, latitude, longitude, status, createdAt, version);
  }

  public void decide(boolean approved) {
    this.status = approved ? "APPROVED" : "REJECTED";
  }

  public void resolve() {
    require("APPROVED".equals(status), "Only active published risk can be resolved");
    this.status = "RESOLVED";
  }

  public UUID getId() {
    return id;
  }

  public UUID getReportedBy() {
    return reportedBy;
  }

  public String getDescription() {
    return description;
  }

  public String getRiskLevel() {
    return riskLevel;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public String getStatus() {
    return status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public long getVersion() {
    return version;
  }
}
