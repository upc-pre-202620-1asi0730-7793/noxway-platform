package com.noctiva.noxway.community.infrastructure.persistence.jpa.assemblers;

import com.noctiva.noxway.community.domain.model.aggregates.*;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.entities.*;

public final class CommunityPersistenceAssembler {
  private CommunityPersistenceAssembler() {}

  public static ServicePoint toDomain(ServicePointPersistenceEntity e) {
    return ServicePoint.reconstitute(
        e.getId(),
        e.getReportedBy(),
        e.getName(),
        e.getType(),
        e.getLatitude(),
        e.getLongitude(),
        e.getTimeZone(),
        e.getStatus(),
        e.getCreatedAt(),
        e.getVersion());
  }

  public static ServicePointPersistenceEntity toPersistence(ServicePoint s) {
    var e = new ServicePointPersistenceEntity();
    e.setId(s.getId());
    e.setReportedBy(s.getReportedBy());
    e.setName(s.getName());
    e.setType(s.getType());
    e.setLatitude(s.getLatitude());
    e.setLongitude(s.getLongitude());
    e.setTimeZone(s.getTimeZone());
    e.setStatus(s.getStatus());
    e.setCreatedAt(s.getCreatedAt());
    e.setVersion(s.getVersion());
    return e;
  }

  public static ServiceOpeningInterval toDomain(ServiceOpeningIntervalPersistenceEntity e) {
    return ServiceOpeningInterval.reconstitute(
        e.getId(),
        e.getServiceId(),
        e.getDayOfWeek(),
        e.getOpensAt(),
        e.getClosesAt(),
        e.isClosesNextDay(),
        e.is24Hours(),
        e.getCreatedAt(),
        e.getVersion());
  }

  public static ServiceOpeningIntervalPersistenceEntity toPersistence(ServiceOpeningInterval h) {
    var e = new ServiceOpeningIntervalPersistenceEntity();
    e.setId(h.getId());
    e.setServiceId(h.getServiceId());
    e.setDayOfWeek(h.getDayOfWeek());
    e.setOpensAt(h.getOpensAt());
    e.setClosesAt(h.getClosesAt());
    e.setClosesNextDay(h.isClosesNextDay());
    e.setIs24Hours(h.is24Hours());
    e.setCreatedAt(h.getCreatedAt());
    e.setVersion(h.getVersion());
    return e;
  }

  public static ServiceVote toDomain(ServiceVotePersistenceEntity e) {
    return ServiceVote.reconstitute(
        e.getId(),
        e.getServiceId(),
        e.getAccountId(),
        e.getDirection(),
        e.getCreatedAt(),
        e.getVersion());
  }

  public static ServiceVotePersistenceEntity toPersistence(ServiceVote v) {
    var e = new ServiceVotePersistenceEntity();
    e.setId(v.getId());
    e.setServiceId(v.getServiceId());
    e.setAccountId(v.getAccountId());
    e.setDirection(v.getDirection());
    e.setCreatedAt(v.getCreatedAt());
    e.setVersion(v.getVersion());
    return e;
  }

  public static RiskZone toDomain(RiskZonePersistenceEntity e) {
    return RiskZone.reconstitute(
        e.getId(),
        e.getReportedBy(),
        e.getDescription(),
        e.getRiskLevel(),
        e.getLatitude(),
        e.getLongitude(),
        e.getStatus(),
        e.getCreatedAt(),
        e.getVersion());
  }

  public static RiskZonePersistenceEntity toPersistence(RiskZone r) {
    var e = new RiskZonePersistenceEntity();
    e.setId(r.getId());
    e.setReportedBy(r.getReportedBy());
    e.setDescription(r.getDescription());
    e.setRiskLevel(r.getRiskLevel());
    e.setLatitude(r.getLatitude());
    e.setLongitude(r.getLongitude());
    e.setStatus(r.getStatus());
    e.setCreatedAt(r.getCreatedAt());
    e.setVersion(r.getVersion());
    return e;
  }

  public static RouteRating toDomain(RouteRatingPersistenceEntity e) {
    return RouteRating.reconstitute(
        e.getId(),
        e.getCommuteId(),
        e.getWorkerId(),
        e.getScore(),
        e.getComments(),
        e.getCreatedAt(),
        e.getVersion());
  }

  public static RouteRatingPersistenceEntity toPersistence(RouteRating r) {
    var e = new RouteRatingPersistenceEntity();
    e.setId(r.getId());
    e.setCommuteId(r.getCommuteId());
    e.setWorkerId(r.getWorkerId());
    e.setScore(r.getScore());
    e.setComments(r.getComments());
    e.setCreatedAt(r.getCreatedAt());
    e.setVersion(r.getVersion());
    return e;
  }

  public static CommunityPost toDomain(CommunityPostPersistenceEntity e) {
    return CommunityPost.reconstitute(
        e.getId(),
        e.getAuthorId(),
        e.getTitle(),
        e.getContent(),
        e.getDistrict(),
        e.getCreatedAt(),
        e.getVersion());
  }

  public static CommunityPostPersistenceEntity toPersistence(CommunityPost p) {
    var e = new CommunityPostPersistenceEntity();
    e.setId(p.getId());
    e.setAuthorId(p.getAuthorId());
    e.setTitle(p.getTitle());
    e.setContent(p.getContent());
    e.setDistrict(p.getDistrict());
    e.setCreatedAt(p.getCreatedAt());
    e.setVersion(p.getVersion());
    return e;
  }
}
