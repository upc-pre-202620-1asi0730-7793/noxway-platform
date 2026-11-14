package com.noctiva.noxway.commuting.infrastructure.persistence.jpa.entities;

import com.noctiva.noxway.shared.infrastructure.persistence.jpa.entities.AuditableUuidPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "alerts", schema = "commute_incident_schema")
public class AlertPersistenceEntity extends AuditableUuidPersistenceEntity {

  @Column(name = "commute_id", nullable = false)
  private UUID commuteId;

  @Column(name = "incident_id")
  private UUID incidentId;

  @Column(name = "contact_id", nullable = false)
  private UUID contactId;

  @Column(name = "channel", nullable = false)
  private String channel;

  @Column(name = "status", nullable = false)
  private String status;

  @Column(name = "message", nullable = false)
  private String message;

  @Column(name = "delivered_at")
  private Instant deliveredAt;

  public AlertPersistenceEntity() {}

  public UUID getCommuteId() {
    return commuteId;
  }

  public void setCommuteId(UUID commuteId) {
    this.commuteId = commuteId;
  }

  public UUID getIncidentId() {
    return incidentId;
  }

  public void setIncidentId(UUID incidentId) {
    this.incidentId = incidentId;
  }

  public UUID getContactId() {
    return contactId;
  }

  public void setContactId(UUID contactId) {
    this.contactId = contactId;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Instant getDeliveredAt() {
    return deliveredAt;
  }

  public void setDeliveredAt(Instant deliveredAt) {
    this.deliveredAt = deliveredAt;
  }
}
