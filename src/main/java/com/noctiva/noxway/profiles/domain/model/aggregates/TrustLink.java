package com.noctiva.noxway.profiles.domain.model.aggregates;

import static com.noctiva.noxway.shared.domain.Rules.require;

import com.noctiva.noxway.profiles.domain.model.events.TrustLinkChangedEvent;
import com.noctiva.noxway.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * TrustLink aggregate root managing a worker's trusted companion link lifecycle.
 */
public class TrustLink extends AbstractDomainAggregateRoot<TrustLink> {

  private final UUID id;
  private final UUID workerId;
  private final UUID contactId;
  private String status;
  private Instant acceptedAt;
  private final Instant createdAt;
  private final long version;

  private TrustLink(
      UUID id,
      UUID workerId,
      UUID contactId,
      String status,
      Instant acceptedAt,
      Instant createdAt,
      long version) {
    this.id = id;
    this.workerId = workerId;
    this.contactId = contactId;
    this.status = status;
    this.acceptedAt = acceptedAt;
    this.createdAt = createdAt;
    this.version = version;
  }

  public static TrustLink createPending(UUID workerId, UUID contactId) {
    Objects.requireNonNull(workerId, "workerId is required");
    Objects.requireNonNull(contactId, "contactId is required");
    require(!workerId.equals(contactId), "Cannot invite yourself as a trusted contact");
    var link =
        new TrustLink(
            UUID.randomUUID(), workerId, contactId, "PENDING", null, Instant.now(), 0L);
    link.registerEvent(
        new TrustLinkChangedEvent(link.id, workerId, contactId, "PENDING", Instant.now()));
    return link;
  }

  public static TrustLink createActive(UUID workerId, UUID contactId) {
    Objects.requireNonNull(workerId);
    Objects.requireNonNull(contactId);
    Instant now = Instant.now();
    return new TrustLink(UUID.randomUUID(), workerId, contactId, "ACTIVE", now, now, 0L);
  }

  public static TrustLink reconstitute(
      UUID id,
      UUID workerId,
      UUID contactId,
      String status,
      Instant acceptedAt,
      Instant createdAt,
      long version) {
    return new TrustLink(id, workerId, contactId, status, acceptedAt, createdAt, version);
  }

  public void accept(UUID actorId) {
    require(contactId.equals(actorId), "Only the invited contact can accept this link");
    require("PENDING".equals(status), "Only pending links can be accepted");
    this.status = "ACTIVE";
    this.acceptedAt = Instant.now();
    registerEvent(new TrustLinkChangedEvent(id, workerId, contactId, "ACTIVE", acceptedAt));
  }

  public void reject(UUID actorId) {
    require(contactId.equals(actorId), "Only the invited contact can reject this link");
    require("PENDING".equals(status), "Only pending links can be rejected");
    this.status = "REJECTED";
    registerEvent(new TrustLinkChangedEvent(id, workerId, contactId, "REJECTED", Instant.now()));
  }

  public void revoke(UUID actorId) {
    require(
        workerId.equals(actorId) || contactId.equals(actorId),
        "Only participants can revoke this link");
    require(!"REVOKED".equals(status), "Trust link is already revoked");
    this.status = "REVOKED";
    registerEvent(new TrustLinkChangedEvent(id, workerId, contactId, "REVOKED", Instant.now()));
  }

  public boolean isActive() {
    return "ACTIVE".equals(status);
  }

  public boolean isPending() {
    return "PENDING".equals(status);
  }

  public UUID getId() {
    return id;
  }

  public UUID getWorkerId() {
    return workerId;
  }

  public UUID getContactId() {
    return contactId;
  }

  public String getStatus() {
    return status;
  }

  public Instant getAcceptedAt() {
    return acceptedAt;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public long getVersion() {
    return version;
  }
}
