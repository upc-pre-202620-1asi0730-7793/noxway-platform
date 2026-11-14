package com.noctiva.noxway.subscriptions.domain.model.aggregates;

import com.noctiva.noxway.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Plan extends AbstractDomainAggregateRoot<Plan> {

  private final UUID id;
  private final String code;
  private final String name;
  private final BigDecimal monthlyPrice;
  private final int maxContacts;
  private final int maxWorkers;
  private final boolean active;
  private final Instant createdAt;
  private final long version;

  private Plan(
      UUID id,
      String code,
      String name,
      BigDecimal monthlyPrice,
      int maxContacts,
      int maxWorkers,
      boolean active,
      Instant createdAt,
      long version) {
    this.id = id;
    this.code = code;
    this.name = name;
    this.monthlyPrice = monthlyPrice;
    this.maxContacts = maxContacts;
    this.maxWorkers = maxWorkers;
    this.active = active;
    this.createdAt = createdAt;
    this.version = version;
  }

  public static Plan create(
      String code,
      String name,
      BigDecimal monthlyPrice,
      int maxContacts,
      int maxWorkers,
      boolean active) {
    return new Plan(
        UUID.randomUUID(),
        code,
        name,
        monthlyPrice,
        maxContacts,
        maxWorkers,
        active,
        Instant.now(),
        0L);
  }

  public static Plan reconstitute(
      UUID id,
      String code,
      String name,
      BigDecimal monthlyPrice,
      int maxContacts,
      int maxWorkers,
      boolean active,
      Instant createdAt,
      long version) {
    return new Plan(id, code, name, monthlyPrice, maxContacts, maxWorkers, active, createdAt, version);
  }

  public boolean isPaid() {
    return monthlyPrice != null && monthlyPrice.signum() > 0;
  }

  public UUID getId() {
    return id;
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public BigDecimal getMonthlyPrice() {
    return monthlyPrice;
  }

  public int getMaxContacts() {
    return maxContacts;
  }

  public int getMaxWorkers() {
    return maxWorkers;
  }

  public boolean isActive() {
    return active;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public long getVersion() {
    return version;
  }
}
