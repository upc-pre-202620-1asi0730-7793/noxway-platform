package com.noctiva.noxway.community.infrastructure.persistence.jpa.entities;

import com.noctiva.noxway.shared.infrastructure.persistence.jpa.entities.AuditableUuidPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "service_opening_intervals", schema = "community_data_schema")
public class ServiceOpeningIntervalPersistenceEntity extends AuditableUuidPersistenceEntity {

  @Column(name = "service_id", nullable = false)
  private UUID serviceId;

  @Column(name = "day_of_week", nullable = false)
  private int dayOfWeek;

  @Column(name = "opens_at", nullable = false)
  private String opensAt;

  @Column(name = "closes_at", nullable = false)
  private String closesAt;

  @Column(name = "closes_next_day", nullable = false)
  private boolean closesNextDay;

  @Column(name = "is24_hours", nullable = false)
  private boolean is24Hours;

  public ServiceOpeningIntervalPersistenceEntity() {}

  public UUID getServiceId() {
    return serviceId;
  }

  public void setServiceId(UUID serviceId) {
    this.serviceId = serviceId;
  }

  public int getDayOfWeek() {
    return dayOfWeek;
  }

  public void setDayOfWeek(int dayOfWeek) {
    this.dayOfWeek = dayOfWeek;
  }

  public String getOpensAt() {
    return opensAt;
  }

  public void setOpensAt(String opensAt) {
    this.opensAt = opensAt;
  }

  public String getClosesAt() {
    return closesAt;
  }

  public void setClosesAt(String closesAt) {
    this.closesAt = closesAt;
  }

  public boolean isClosesNextDay() {
    return closesNextDay;
  }

  public void setClosesNextDay(boolean closesNextDay) {
    this.closesNextDay = closesNextDay;
  }

  public boolean is24Hours() {
    return is24Hours;
  }

  public void setIs24Hours(boolean is24Hours) {
    this.is24Hours = is24Hours;
  }
}
