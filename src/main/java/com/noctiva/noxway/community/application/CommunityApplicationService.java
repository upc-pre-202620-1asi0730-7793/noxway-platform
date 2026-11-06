package com.noctiva.noxway.community.application;

import static com.noctiva.noxway.shared.domain.Rules.*;

import com.noctiva.noxway.community.application.commands.CommunityCommands;
import com.noctiva.noxway.community.domain.model.aggregates.*;
import com.noctiva.noxway.community.domain.model.valueobjects.OpeningHours;
import com.noctiva.noxway.community.domain.repositories.*;
import com.noctiva.noxway.community.interfaces.acl.CommunityModeration;
import com.noctiva.noxway.commuting.interfaces.acl.CommuteAccess;
import com.noctiva.noxway.moderation.interfaces.acl.ModerationQueue;
import com.noctiva.noxway.shared.infrastructure.security.Actor;
import java.time.*;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class CommunityApplicationService implements CommunityModeration {
  private final ServicePointRepository services;
  private final ServiceOpeningIntervalRepository hours;
  private final RiskZoneRepository risks;
  private final RouteRatingRepository ratings;
  private final CommunityPostRepository posts;
  private final ServiceVoteRepository votes;
  private final ModerationQueue moderation;
  private final CommuteAccess commutes;

  public CommunityApplicationService(
      ServicePointRepository s,
      ServiceOpeningIntervalRepository h,
      RiskZoneRepository r,
      RouteRatingRepository ra,
      CommunityPostRepository p,
      ServiceVoteRepository v,
      ModerationQueue m,
      CommuteAccess c) {
    services = s;
    hours = h;
    risks = r;
    ratings = ra;
    posts = p;
    votes = v;
    moderation = m;
    commutes = c;
  }

  public record ServiceResource(
      ServicePoint service,
      List<ServiceOpeningInterval> openingHours,
      int trustScore,
      boolean openAtReference,
      Instant referenceTime) {}

  private ServicePoint get(UUID id) {
    return services
        .findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found"));
  }

  public ServiceResource resource(ServicePoint s, Instant at) {
    var list = hours.findByServiceId(s.id());
    return new ServiceResource(
        s,
        list,
        votes.findByServiceId(s.id()).stream().mapToInt(ServiceVote::direction).sum(),
        OpeningHours.isOpen(list, s.timeZone(), at),
        at);
  }

  public ServiceResource submit(CommunityCommands.Service c) {
    coordinates(c.latitude(), c.longitude());
    zone(c.timeZone());
    for (var h : c.openingHours())
      OpeningHours.validate(
          h.dayOfWeek(), h.opensAt(), h.closesAt(), h.closesNextDay(), h.is24Hours());
    var s =
        services.save(
            ServicePoint.create(
                Actor.current().id(),
                c.name(),
                c.type(),
                c.latitude(),
                c.longitude(),
                c.timeZone(),
                "PENDING"));
    for (var h : c.openingHours())
      hours.save(
          ServiceOpeningInterval.create(
              s.id(), h.dayOfWeek(), h.opensAt(), h.closesAt(), h.closesNextDay(), h.is24Hours()));
    moderation.submit(Actor.current().id(), "SERVICE", s.id(), "New service and opening hours");
    return resource(s, Instant.now());
  }

  public ServiceResource update(UUID id, CommunityCommands.Service c) {
    var old = get(id);
    Actor.current().own(old.reportedBy());
    coordinates(c.latitude(), c.longitude());
    zone(c.timeZone());
    for (var h : c.openingHours())
      OpeningHours.validate(
          h.dayOfWeek(), h.opensAt(), h.closesAt(), h.closesNextDay(), h.is24Hours());
    var updated =
        services.save(
            old.withName(c.name())
                .withType(c.type())
                .withLatitude(c.latitude())
                .withLongitude(c.longitude())
                .withTimeZone(c.timeZone())
                .withStatus("PENDING"));
    hours.findByServiceId(id).forEach(h -> hours.delete(h.id()));
    for (var h : c.openingHours())
      hours.save(
          ServiceOpeningInterval.create(
              id, h.dayOfWeek(), h.opensAt(), h.closesAt(), h.closesNextDay(), h.is24Hours()));
    moderation.submit(
        Actor.current().id(), "SERVICE", id, "Service information or opening hours changed");
    return resource(updated, Instant.now());
  }

  public List<ServiceResource> list(
      String type, Instant at, boolean openOnly, Double lat, Double lon, Double radius) {
    Instant reference = at == null ? Instant.now() : at;
    if (lat != null || lon != null || radius != null) {
      require(
          lat != null && lon != null && radius != null,
          "Latitude, longitude and radiusKm must be supplied together");
      coordinates(lat, lon);
      require(radius > 0 && radius <= 100, "Radius must be 0..100 km");
    }
    return services.findByStatus("APPROVED").stream()
        .filter(s -> type == null || s.type().equals(type))
        .filter(
            s ->
                lat == null
                    || OpeningHours.distanceKm(lat, lon, s.latitude(), s.longitude()) <= radius)
        .map(s -> resource(s, reference))
        .filter(s -> !openOnly || s.openAtReference())
        .toList();
  }

  public ServiceResource view(UUID id, Instant at) {
    var s = get(id);
    if (!s.status().equals("APPROVED")
        && !s.reportedBy().equals(Actor.current().id())
        && !Actor.current().privileged())
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not published");
    return resource(s, at == null ? Instant.now() : at);
  }

  public List<ServiceResource> own() {
    return services.findByReportedBy(Actor.current().id()).stream()
        .map(s -> resource(s, Instant.now()))
        .toList();
  }

  public ServiceVote vote(UUID id, int direction) {
    require(direction == -1 || direction == 1, "Vote must be -1 or 1");
    var s = get(id);
    require(s.status().equals("APPROVED"), "Service is not published");
    UUID who = Actor.current().id();
    var old = votes.findByServiceId(id).stream().filter(v -> v.accountId().equals(who)).findFirst();
    return votes.save(
        old.map(v -> v.withDirection(direction))
            .orElseGet(() -> ServiceVote.create(id, who, direction)));
  }

  public RiskZone report(CommunityCommands.Risk c) {
    coordinates(c.latitude(), c.longitude());
    var risk =
        risks.save(
            RiskZone.create(
                Actor.current().id(),
                c.description(),
                c.riskLevel(),
                c.latitude(),
                c.longitude(),
                "PENDING"));
    moderation.submit(Actor.current().id(), "RISK", risk.id(), "New risk-zone report");
    return risk;
  }

  public List<RiskZone> riskZones() {
    return risks.findByStatus("APPROVED");
  }

  public RiskZone resolveRisk(UUID id) {
    Actor.current().moderator();
    var r =
        risks
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Risk not found"));
    require(r.status().equals("APPROVED"), "Only active published risk can be resolved");
    return risks.save(r.withStatus("RESOLVED"));
  }

  public RouteRating rate(CommunityCommands.Rating c) {
    var who = Actor.current();
    who.worker();
    commutes.requireCompletedOwn(c.commuteId(), who.id());
    require(
        ratings.findByCommuteId(c.commuteId()).stream()
            .noneMatch(r -> r.workerId().equals(who.id())),
        "Commute already rated");
    return ratings.save(RouteRating.create(c.commuteId(), who.id(), c.score(), c.comments()));
  }

  public List<RouteRating> myRatings() {
    return ratings.findByWorkerId(Actor.current().id());
  }

  public CommunityPost post(CommunityCommands.Post c) {
    return posts.save(
        CommunityPost.create(Actor.current().id(), c.title(), c.content(), c.district()));
  }

  public List<CommunityPost> posts(String district) {
    return posts.findAll().stream()
        .filter(p -> district == null || p.district().equalsIgnoreCase(district))
        .sorted(Comparator.comparing(CommunityPost::createdAt).reversed())
        .toList();
  }

  public void deletePost(UUID id) {
    var p =
        posts
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
    if (!Actor.current().privileged()) Actor.current().own(p.authorId());
    posts.delete(id);
  }

  public boolean exists(String type, UUID id) {
    return type.equals("SERVICE")
        ? services.findById(id).isPresent()
        : type.equals("RISK") && risks.findById(id).isPresent();
  }

  public void decide(String type, UUID id, boolean approved) {
    if (type.equals("SERVICE")) {
      services.save(get(id).withStatus(approved ? "APPROVED" : "REJECTED"));
    } else if (type.equals("RISK")) {
      var r =
          risks
              .findById(id)
              .orElseThrow(
                  () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Risk not found"));
      risks.save(r.withStatus(approved ? "APPROVED" : "REJECTED"));
    } else
      throw new com.noctiva.noxway.shared.domain.RuleViolation("Unsupported moderation target");
  }
}
