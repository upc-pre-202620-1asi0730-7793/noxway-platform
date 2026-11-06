package com.noctiva.noxway.commuting.domain.model.aggregates;

import java.time.Instant;
import java.util.UUID;

public record Alert(
    UUID id,
    UUID commuteId,
    UUID incidentId,
    UUID contactId,
    String channel,
    String status,
    String message,
    Instant deliveredAt,
    Instant createdAt,
    long version) {
  public static Alert create(
      UUID commuteId,
      UUID incidentId,
      UUID contactId,
      String channel,
      String status,
      String message,
      Instant deliveredAt) {
    return new Alert(
        UUID.randomUUID(),
        commuteId,
        incidentId,
        contactId,
        channel,
        status,
        message,
        deliveredAt,
        Instant.now(),
        0);
  }

  public Alert withCommuteId(UUID value) {
    return new Alert(
        id,
        value,
        incidentId,
        contactId,
        channel,
        status,
        message,
        deliveredAt,
        createdAt,
        version);
  }

  public Alert withIncidentId(UUID value) {
    return new Alert(
        id, commuteId, value, contactId, channel, status, message, deliveredAt, createdAt, version);
  }

  public Alert withContactId(UUID value) {
    return new Alert(
        id,
        commuteId,
        incidentId,
        value,
        channel,
        status,
        message,
        deliveredAt,
        createdAt,
        version);
  }

  public Alert withChannel(String value) {
    return new Alert(
        id,
        commuteId,
        incidentId,
        contactId,
        value,
        status,
        message,
        deliveredAt,
        createdAt,
        version);
  }

  public Alert withStatus(String value) {
    return new Alert(
        id,
        commuteId,
        incidentId,
        contactId,
        channel,
        value,
        message,
        deliveredAt,
        createdAt,
        version);
  }

  public Alert withMessage(String value) {
    return new Alert(
        id,
        commuteId,
        incidentId,
        contactId,
        channel,
        status,
        value,
        deliveredAt,
        createdAt,
        version);
  }

  public Alert withDeliveredAt(Instant value) {
    return new Alert(
        id, commuteId, incidentId, contactId, channel, status, message, value, createdAt, version);
  }
}
