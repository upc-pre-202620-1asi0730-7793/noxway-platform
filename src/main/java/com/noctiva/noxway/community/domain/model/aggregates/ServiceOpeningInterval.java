package com.noctiva.noxway.community.domain.model.aggregates;

import java.time.Instant;
import java.util.UUID;

public record ServiceOpeningInterval(
    UUID id,
    UUID serviceId,
    int dayOfWeek,
    String opensAt,
    String closesAt,
    boolean closesNextDay,
    boolean is24Hours,
    Instant createdAt,
    long version) {
  public static ServiceOpeningInterval create(
      UUID serviceId,
      int dayOfWeek,
      String opensAt,
      String closesAt,
      boolean closesNextDay,
      boolean is24Hours) {
    return new ServiceOpeningInterval(
        UUID.randomUUID(),
        serviceId,
        dayOfWeek,
        opensAt,
        closesAt,
        closesNextDay,
        is24Hours,
        Instant.now(),
        0);
  }

  public ServiceOpeningInterval withServiceId(UUID value) {
    return new ServiceOpeningInterval(
        id, value, dayOfWeek, opensAt, closesAt, closesNextDay, is24Hours, createdAt, version);
  }

  public ServiceOpeningInterval withDayOfWeek(int value) {
    return new ServiceOpeningInterval(
        id, serviceId, value, opensAt, closesAt, closesNextDay, is24Hours, createdAt, version);
  }

  public ServiceOpeningInterval withOpensAt(String value) {
    return new ServiceOpeningInterval(
        id, serviceId, dayOfWeek, value, closesAt, closesNextDay, is24Hours, createdAt, version);
  }

  public ServiceOpeningInterval withClosesAt(String value) {
    return new ServiceOpeningInterval(
        id, serviceId, dayOfWeek, opensAt, value, closesNextDay, is24Hours, createdAt, version);
  }

  public ServiceOpeningInterval withClosesNextDay(boolean value) {
    return new ServiceOpeningInterval(
        id, serviceId, dayOfWeek, opensAt, closesAt, value, is24Hours, createdAt, version);
  }

  public ServiceOpeningInterval withIs24Hours(boolean value) {
    return new ServiceOpeningInterval(
        id, serviceId, dayOfWeek, opensAt, closesAt, closesNextDay, value, createdAt, version);
  }
}
