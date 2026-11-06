package com.noctiva.noxway.community.domain.model.aggregates;

import java.time.Instant;
import java.util.UUID;

public record ServicePoint(
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
  public static ServicePoint create(
      UUID reportedBy,
      String name,
      String type,
      double latitude,
      double longitude,
      String timeZone,
      String status) {
    return new ServicePoint(
        UUID.randomUUID(),
        reportedBy,
        name,
        type,
        latitude,
        longitude,
        timeZone,
        status,
        Instant.now(),
        0);
  }

  public ServicePoint withReportedBy(UUID value) {
    return new ServicePoint(
        id, value, name, type, latitude, longitude, timeZone, status, createdAt, version);
  }

  public ServicePoint withName(String value) {
    return new ServicePoint(
        id, reportedBy, value, type, latitude, longitude, timeZone, status, createdAt, version);
  }

  public ServicePoint withType(String value) {
    return new ServicePoint(
        id, reportedBy, name, value, latitude, longitude, timeZone, status, createdAt, version);
  }

  public ServicePoint withLatitude(double value) {
    return new ServicePoint(
        id, reportedBy, name, type, value, longitude, timeZone, status, createdAt, version);
  }

  public ServicePoint withLongitude(double value) {
    return new ServicePoint(
        id, reportedBy, name, type, latitude, value, timeZone, status, createdAt, version);
  }

  public ServicePoint withTimeZone(String value) {
    return new ServicePoint(
        id, reportedBy, name, type, latitude, longitude, value, status, createdAt, version);
  }

  public ServicePoint withStatus(String value) {
    return new ServicePoint(
        id, reportedBy, name, type, latitude, longitude, timeZone, value, createdAt, version);
  }
}
