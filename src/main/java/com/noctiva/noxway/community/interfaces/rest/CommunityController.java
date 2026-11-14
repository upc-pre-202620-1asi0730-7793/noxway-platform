package com.noctiva.noxway.community.interfaces.rest;

import com.noctiva.noxway.community.application.commands.CommunityCommands.DeleteCommunityPostCommand;
import com.noctiva.noxway.community.application.commands.CommunityCommands.ResolveRiskZoneCommand;
import com.noctiva.noxway.community.application.commandservices.CommunityCommandService;
import com.noctiva.noxway.community.application.queries.CommunityQueries.*;
import com.noctiva.noxway.community.application.queryservices.CommunityQueryService;
import com.noctiva.noxway.community.interfaces.rest.resources.*;
import com.noctiva.noxway.community.interfaces.rest.transform.CommunityRestAssembler;
import com.noctiva.noxway.iam.interfaces.acl.IamContextFacade;
import com.noctiva.noxway.profiles.interfaces.acl.ProfileDirectoryFacade;
import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/community")
@Tag(
    name = "Community & Safety Map",
    description = "24/7 services directory, risk zones, trust voting, route ratings, and feed")
public class CommunityController {

  private final CommunityCommandService commandService;
  private final CommunityQueryService queryService;
  private final IamContextFacade iamFacade;
  private final ProfileDirectoryFacade profileDirectory;
  private final ResponseEntityAssembler responseAssembler;

  public CommunityController(
      CommunityCommandService commandService,
      CommunityQueryService queryService,
      IamContextFacade iamFacade,
      ProfileDirectoryFacade profileDirectory,
      ResponseEntityAssembler responseAssembler) {
    this.commandService = commandService;
    this.queryService = queryService;
    this.iamFacade = iamFacade;
    this.profileDirectory = profileDirectory;
    this.responseAssembler = responseAssembler;
  }

  @PostMapping("/services")
  @Operation(summary = "Submit a new night-shift service point with opening hours")
  public ResponseEntity<?> submitService(
      @Valid @RequestBody SubmitServicePointResource body,
      HttpServletRequest request,
      Locale locale) {
    UUID reporterId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toCreated(
        commandService.handle(CommunityRestAssembler.toCommand(reporterId, body)),
        CommunityRestAssembler::toResource,
        s -> URI.create("/api/v1/community/services/" + s.id()),
        request,
        locale);
  }

  @PutMapping("/services/{id}")
  @Operation(summary = "Update a submitted service point and re-queue for review")
  public ResponseEntity<?> updateService(
      @PathVariable UUID id,
      @Valid @RequestBody SubmitServicePointResource body,
      HttpServletRequest request,
      Locale locale) {
    UUID actorId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toOk(
        commandService.handle(CommunityRestAssembler.toCommand(id, actorId, body)),
        CommunityRestAssembler::toResource,
        request,
        locale);
  }

  @GetMapping("/services")
  @Operation(summary = "Search approved night-shift service points with open-now & radius filtering")
  public ResponseEntity<?> listServices(
      @RequestParam(required = false) String type,
      @RequestParam(required = false) Instant at,
      @RequestParam(defaultValue = "false") boolean openOnly,
      @RequestParam(required = false) Double latitude,
      @RequestParam(required = false) Double longitude,
      @RequestParam(required = false) Double radiusKm,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      HttpServletRequest request,
      Locale locale) {
    UUID viewerId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toOk(
        queryService.handle(
            new GetServicePointsQuery(
                viewerId, type, at, openOnly, latitude, longitude, radiusKm, page, size)),
        CommunityRestAssembler::toPagedServices,
        request,
        locale);
  }

  @GetMapping("/services/mine")
  @Operation(summary = "List service points submitted by current worker")
  public ResponseEntity<?> myServices(HttpServletRequest request, Locale locale) {
    UUID workerId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toOk(
        queryService.handle(new GetMyServicePointsQuery(workerId)),
        CommunityRestAssembler::toServiceResources,
        request,
        locale);
  }

  @GetMapping("/services/{id}")
  @Operation(summary = "View service point details and real-time open status")
  public ResponseEntity<?> viewService(
      @PathVariable UUID id,
      @RequestParam(required = false) Instant at,
      HttpServletRequest request,
      Locale locale) {
    var actor = iamFacade.resolveCurrentActor();
    return responseAssembler.toOk(
        queryService.handle(new GetServicePointByIdQuery(id, actor.id(), actor.isStaff(), at)),
        CommunityRestAssembler::toResource,
        request,
        locale);
  }

  @PutMapping("/services/{id}/vote")
  @Operation(summary = "Upvote (+1), downvote (-1), or retract (0) a vote on an approved service point")
  public ResponseEntity<?> voteService(
      @PathVariable UUID id,
      @Valid @RequestBody VoteServicePointResource body,
      HttpServletRequest request,
      Locale locale) {
    UUID accountId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toOk(
        commandService.handle(CommunityRestAssembler.toCommand(id, accountId, body)),
        CommunityRestAssembler::toResource,
        request,
        locale);
  }

  @PostMapping("/risk-zones")
  @Operation(summary = "Report a new night risk zone for moderation")
  public ResponseEntity<?> reportRisk(
      @Valid @RequestBody ReportRiskZoneResource body,
      HttpServletRequest request,
      Locale locale) {
    UUID reporterId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toCreated(
        commandService.handle(CommunityRestAssembler.toCommand(reporterId, body)),
        r -> CommunityRestAssembler.toResource(r, profileDirectory.findById(r.getReportedBy()).orElse(null)),
        r -> URI.create("/api/v1/community/risk-zones/" + r.getId()),
        request,
        locale);
  }

  @GetMapping("/risk-zones")
  @Operation(summary = "List approved active risk zones")
  public ResponseEntity<?> listRiskZones(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      HttpServletRequest request,
      Locale locale) {
    return responseAssembler.toOk(
        queryService.handle(new GetApprovedRiskZonesQuery(page, size)),
        CommunityRestAssembler::toPagedRisks,
        request,
        locale);
  }

  @PostMapping("/risk-zones/{id}/resolve")
  @Operation(summary = "Resolve an active approved risk zone")
  public ResponseEntity<?> resolveRisk(
      @PathVariable UUID id, HttpServletRequest request, Locale locale) {
    var actor = iamFacade.resolveCurrentActor();
    if (iamFacade.isSecurityEnabled() && !actor.isStaff()) {
      return responseAssembler.toErrorResponse(
          new ApplicationError.ForbiddenError("Moderator role required"), request, locale);
    }
    return responseAssembler.toOk(
        commandService.handle(new ResolveRiskZoneCommand(id, actor.id())),
        r -> CommunityRestAssembler.toResource(r, profileDirectory.findById(r.getReportedBy()).orElse(null)),
        request,
        locale);
  }

  @PostMapping("/route-ratings")
  @Operation(summary = "Rate route safety after completing a commute")
  public ResponseEntity<?> rateRoute(
      @Valid @RequestBody RateRouteResource body, HttpServletRequest request, Locale locale) {
    UUID workerId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toCreated(
        commandService.handle(CommunityRestAssembler.toCommand(workerId, body)),
        CommunityRestAssembler::toResource,
        r -> URI.create("/api/v1/community/route-ratings/" + r.getId()),
        request,
        locale);
  }

  @GetMapping("/route-ratings/mine")
  @Operation(summary = "List route ratings submitted by current worker")
  public ResponseEntity<?> myRatings(HttpServletRequest request, Locale locale) {
    UUID workerId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toOk(
        queryService.handle(new GetMyRouteRatingsQuery(workerId)),
        CommunityRestAssembler::toRatingResources,
        request,
        locale);
  }

  @PostMapping("/posts")
  @Operation(summary = "Publish a community feed post")
  public ResponseEntity<?> createPost(
      @Valid @RequestBody CreateCommunityPostResource body,
      HttpServletRequest request,
      Locale locale) {
    UUID authorId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toCreated(
        commandService.handle(CommunityRestAssembler.toCommand(authorId, body)),
        p -> CommunityRestAssembler.toResource(p, profileDirectory.findById(p.getAuthorId()).orElse(null)),
        p -> URI.create("/api/v1/community/posts/" + p.getId()),
        request,
        locale);
  }

  @GetMapping("/posts")
  @Operation(summary = "List community feed posts optionally filtered by district")
  public ResponseEntity<?> listPosts(
      @RequestParam(required = false) String district,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      HttpServletRequest request,
      Locale locale) {
    return responseAssembler.toOk(
        queryService.handle(new GetCommunityPostsQuery(district, page, size)),
        CommunityRestAssembler::toPagedPosts,
        request,
        locale);
  }

  @DeleteMapping("/posts/{id}")
  @Operation(summary = "Delete a community post")
  public ResponseEntity<?> deletePost(
      @PathVariable UUID id, HttpServletRequest request, Locale locale) {
    var actor = iamFacade.resolveCurrentActor();
    return responseAssembler.toNoContent(
        commandService.handle(
            new DeleteCommunityPostCommand(id, actor.id(), actor.isStaff())),
        request,
        locale);
  }
}
