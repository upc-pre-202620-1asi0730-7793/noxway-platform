package com.noctiva.noxway.moderation.domain.model.aggregates;

import java.time.Instant;
import java.util.UUID;

public record CommunityReport(
    UUID id,
    UUID reportedBy,
    String targetType,
    UUID targetId,
    String reason,
    String status,
    Instant createdAt,
    long version) {
  public static CommunityReport create(
      UUID reportedBy, String targetType, UUID targetId, String reason, String status) {
    return new CommunityReport(
        UUID.randomUUID(), reportedBy, targetType, targetId, reason, status, Instant.now(), 0);
  }

  public CommunityReport withReportedBy(UUID value) {
    return new CommunityReport(id, value, targetType, targetId, reason, status, createdAt, version);
  }

  public CommunityReport withTargetType(String value) {
    return new CommunityReport(id, reportedBy, value, targetId, reason, status, createdAt, version);
  }

  public CommunityReport withTargetId(UUID value) {
    return new CommunityReport(
        id, reportedBy, targetType, value, reason, status, createdAt, version);
  }

  public CommunityReport withReason(String value) {
    return new CommunityReport(
        id, reportedBy, targetType, targetId, value, status, createdAt, version);
  }

  public CommunityReport withStatus(String value) {
    return new CommunityReport(
        id, reportedBy, targetType, targetId, reason, value, createdAt, version);
  }
}
