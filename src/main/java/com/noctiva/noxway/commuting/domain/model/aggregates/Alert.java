package com.noctiva.noxway.commuting.domain.model.aggregates;

import static com.noctiva.noxway.shared.domain.Rules.require;

import com.noctiva.noxway.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import java.time.Instant;
import java.util.UUID;

public class Alert extends AbstractDomainAggregateRoot<Alert> {

  private final UUID id;
  private final UUID commuteId;
  private final UUID incidentId;
  private final UUID contactId;
  private final String channel;
  private String status;
  private final String message;
  private Instant deliveredAt;
  private final Instant createdAt;
  private final long version;

  private Alert(
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
    this.id = id;
    this.commuteId = commuteId;
    this.incidentId = incidentId;
    this.contactId = contactId;
    this.channel = channel;
    this.status = status;
    this.message = message;
    this.deliveredAt = deliveredAt;
    this.createdAt = createdAt;
    this.version = version;
  }

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
        0L);
  }

  public static Alert reconstitute(
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
    return new Alert(
        id, commuteId, incidentId, contactId, channel, status, message, deliveredAt, createdAt, version);
  }

  public void markDelivered(Instant deliveredAt) {
    this.status = "DELIVERED";
    this.deliveredAt = deliveredAt;
  }

  public void markFailed() {
    this.status = "FAILED";
  }

  public void markSuppressed() {
    this.status = "SUPPRESSED";
  }

  public void retry() {
    require("FAILED".equals(status), "Only failed deliveries can be retried");
    this.status = "PENDING_PROVIDER";
  }

  public UUID getId() {
    return id;
  }

  public UUID getCommuteId() {
    return commuteId;
  }

  public UUID getIncidentId() {
    return incidentId;
  }

  public UUID getContactId() {
    return contactId;
  }

  public String getChannel() {
    return channel;
  }

  public String getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }

  public Instant getDeliveredAt() {
    return deliveredAt;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public long getVersion() {
    return version;
  }
}
