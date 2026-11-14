package com.noctiva.noxway.community.domain.model.aggregates;

import com.noctiva.noxway.community.domain.model.valueobjects.OpeningHours;
import com.noctiva.noxway.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import java.time.Instant;
import java.util.UUID;

public class ServiceOpeningInterval extends AbstractDomainAggregateRoot<ServiceOpeningInterval> {

  private final UUID id;
  private final UUID serviceId;
  private final int dayOfWeek;
  private final String opensAt;
  private final String closesAt;
  private final boolean closesNextDay;
  private final boolean is24Hours;
  private final Instant createdAt;
  private final long version;

  private ServiceOpeningInterval(
      UUID id,
      UUID serviceId,
      int dayOfWeek,
      String opensAt,
      String closesAt,
      boolean closesNextDay,
      boolean is24Hours,
      Instant createdAt,
      long version) {
    this.id = id;
    this.serviceId = serviceId;
    this.dayOfWeek = dayOfWeek;
    this.opensAt = opensAt;
    this.closesAt = closesAt;
    this.closesNextDay = closesNextDay;
    this.is24Hours = is24Hours;
    this.createdAt = createdAt;
    this.version = version;
  }

  public static ServiceOpeningInterval create(
      UUID serviceId,
      int dayOfWeek,
      String opensAt,
      String closesAt,
      boolean closesNextDay,
      boolean is24Hours) {
    OpeningHours.validate(dayOfWeek, opensAt, closesAt, closesNextDay, is24Hours);
    return new ServiceOpeningInterval(
        UUID.randomUUID(),
        serviceId,
        dayOfWeek,
        opensAt,
        closesAt,
        closesNextDay,
        is24Hours,
        Instant.now(),
        0L);
  }

  public static ServiceOpeningInterval reconstitute(
      UUID id,
      UUID serviceId,
      int dayOfWeek,
      String opensAt,
      String closesAt,
      boolean closesNextDay,
      boolean is24Hours,
      Instant createdAt,
      long version) {
    return new ServiceOpeningInterval(
        id, serviceId, dayOfWeek, opensAt, closesAt, closesNextDay, is24Hours, createdAt, version);
  }

  public UUID getId() {
    return id;
  }

  public UUID getServiceId() {
    return serviceId;
  }

  public int getDayOfWeek() {
    return dayOfWeek;
  }

  public String getOpensAt() {
    return opensAt;
  }

  public String getClosesAt() {
    return closesAt;
  }

  public boolean isClosesNextDay() {
    return closesNextDay;
  }

  public boolean is24Hours() {
    return is24Hours;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public long getVersion() {
    return version;
  }
}
