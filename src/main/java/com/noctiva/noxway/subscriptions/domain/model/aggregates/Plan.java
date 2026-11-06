package com.noctiva.noxway.subscriptions.domain.model.aggregates;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Plan(
    UUID id,
    String code,
    String name,
    BigDecimal monthlyPrice,
    int maxContacts,
    int maxWorkers,
    boolean active,
    Instant createdAt,
    long version) {
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
        0);
  }

  public Plan withCode(String value) {
    return new Plan(
        id, value, name, monthlyPrice, maxContacts, maxWorkers, active, createdAt, version);
  }

  public Plan withName(String value) {
    return new Plan(
        id, code, value, monthlyPrice, maxContacts, maxWorkers, active, createdAt, version);
  }

  public Plan withMonthlyPrice(BigDecimal value) {
    return new Plan(id, code, name, value, maxContacts, maxWorkers, active, createdAt, version);
  }

  public Plan withMaxContacts(int value) {
    return new Plan(id, code, name, monthlyPrice, value, maxWorkers, active, createdAt, version);
  }

  public Plan withMaxWorkers(int value) {
    return new Plan(id, code, name, monthlyPrice, maxContacts, value, active, createdAt, version);
  }

  public Plan withActive(boolean value) {
    return new Plan(
        id, code, name, monthlyPrice, maxContacts, maxWorkers, value, createdAt, version);
  }
}
