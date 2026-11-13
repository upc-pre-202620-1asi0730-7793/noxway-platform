package com.noctiva.noxway.community.domain.model.aggregates;

import static com.noctiva.noxway.shared.domain.Rules.*;

import com.noctiva.noxway.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class ServicePoint extends AbstractDomainAggregateRoot<ServicePoint> {

  private final UUID id;
  private final UUID reportedBy;
  private String name;
  private String type;
  private double latitude;
  private double longitude;
  private String timeZone;
  private String status;
  private final Instant createdAt;
  private final long version;

  private ServicePoint(
      UUID id,
      UUID reportedBy,
      String name,
      String type,
      double latitude,
      double longitude,
      String timeZone,
      String status,
      Instant createdAt,
      long version) {
    this.id = id;
    this.reportedBy = reportedBy;
    this.name = name;
    this.type = type;
    this.latitude = latitude;
    this.longitude = longitude;
    this.timeZone = timeZone;
    this.status = status;
    this.createdAt = createdAt;
    this.version = version;
  }

  public static ServicePoint create(
      UUID reportedBy,
      String name,
      String type,
      double latitude,
      double longitude,
      String timeZone,
      String status) {
    Objects.requireNonNull(reportedBy);
    require(name != null && !name.isBlank(), "Service name is required");
    coordinates(latitude, longitude);
    zone(timeZone);
    return new ServicePoint(
        UUID.randomUUID(),
        reportedBy,
        name.trim(),
        type,
        latitude,
        longitude,
        timeZone.trim(),
        status,
        Instant.now(),
        0L);
  }

  public static ServicePoint reconstitute(
      UUID id,
      UUID reportedBy,
      String name,
      String type,
      double latitude,
      double longitude,
      String timeZone,
      String status,
      Instant createdAt,
      long version) {
    return new ServicePoint(
        id, reportedBy, name, type, latitude, longitude, timeZone, status, createdAt, version);
  }

  public void updateForReview(
      String name, String type, double latitude, double longitude, String timeZone) {
    require(name != null && !name.isBlank(), "Service name is required");
    coordinates(latitude, longitude);
    zone(timeZone);
    this.name = name.trim();
    this.type = type;
    this.latitude = latitude;
    this.longitude = longitude;
    this.timeZone = timeZone.trim();
    this.status = "PENDING";
  }

  public void decide(boolean approved) {
    this.status = approved ? "APPROVED" : "REJECTED";
  }

  public UUID getId() {
    return id;
  }

  public UUID getReportedBy() {
    return reportedBy;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public String getTimeZone() {
    return timeZone;
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
