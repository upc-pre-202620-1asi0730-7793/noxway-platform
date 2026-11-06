package com.noctiva.noxway.community.interfaces.rest;

import com.noctiva.noxway.community.application.CommunityApplicationService;
import com.noctiva.noxway.community.application.commands.CommunityCommands;
import com.noctiva.noxway.community.domain.model.aggregates.*;
import com.noctiva.noxway.shared.interfaces.rest.Pages;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/community")
public class CommunityController {
  private final CommunityApplicationService service;

  public CommunityController(CommunityApplicationService s) {
    service = s;
  }

  @PostMapping("/services")
  @ResponseStatus(HttpStatus.CREATED)
  public CommunityApplicationService.ServiceResource submit(
      @Valid @RequestBody CommunityCommands.Service c) {
    return service.submit(c);
  }

  @PutMapping("/services/{id}")
  public CommunityApplicationService.ServiceResource update(
      @PathVariable UUID id, @Valid @RequestBody CommunityCommands.Service c) {
    return service.update(id, c);
  }

  @GetMapping("/services")
  public Pages<CommunityApplicationService.ServiceResource> services(
      @RequestParam(required = false) String type,
      @RequestParam(required = false) Instant at,
      @RequestParam(defaultValue = "false") boolean openOnly,
      @RequestParam(required = false) Double latitude,
      @RequestParam(required = false) Double longitude,
      @RequestParam(required = false) Double radiusKm,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return Pages.of(service.list(type, at, openOnly, latitude, longitude, radiusKm), page, size);
  }

  @GetMapping("/services/mine")
  public List<CommunityApplicationService.ServiceResource> mine() {
    return service.own();
  }

  @GetMapping("/services/{id}")
  public CommunityApplicationService.ServiceResource view(
      @PathVariable UUID id, @RequestParam(required = false) Instant at) {
    return service.view(id, at);
  }

  @PutMapping("/services/{id}/vote")
  public ServiceVote vote(@PathVariable UUID id, @Valid @RequestBody CommunityCommands.Vote c) {
    return service.vote(id, c.direction());
  }

  @PostMapping("/risk-zones")
  @ResponseStatus(HttpStatus.CREATED)
  public RiskZone report(@Valid @RequestBody CommunityCommands.Risk c) {
    return service.report(c);
  }

  @GetMapping("/risk-zones")
  public Pages<RiskZone> risks(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return Pages.of(service.riskZones(), page, size);
  }

  @PostMapping("/risk-zones/{id}/resolve")
  public RiskZone resolve(@PathVariable UUID id) {
    return service.resolveRisk(id);
  }

  @PostMapping("/route-ratings")
  @ResponseStatus(HttpStatus.CREATED)
  public RouteRating rate(@Valid @RequestBody CommunityCommands.Rating c) {
    return service.rate(c);
  }

  @GetMapping("/route-ratings/mine")
  public List<RouteRating> ratings() {
    return service.myRatings();
  }

  @PostMapping("/posts")
  @ResponseStatus(HttpStatus.CREATED)
  public CommunityPost post(@Valid @RequestBody CommunityCommands.Post c) {
    return service.post(c);
  }

  @GetMapping("/posts")
  public Pages<CommunityPost> posts(
      @RequestParam(required = false) String district,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return Pages.of(service.posts(district), page, size);
  }

  @DeleteMapping("/posts/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    service.deletePost(id);
  }
}
