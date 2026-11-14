package com.noctiva.noxway.commuting.interfaces.rest;

import com.noctiva.noxway.commuting.application.commands.CommuteCommands.*;
import com.noctiva.noxway.commuting.application.commandservices.CommuteCommandService;
import com.noctiva.noxway.commuting.application.queries.CommuteQueries.*;
import com.noctiva.noxway.commuting.application.queryservices.CommuteQueryService;
import com.noctiva.noxway.commuting.interfaces.rest.resources.*;
import com.noctiva.noxway.commuting.interfaces.rest.transform.CommutingRestAssembler;
import com.noctiva.noxway.iam.interfaces.acl.IamContextFacade;
import com.noctiva.noxway.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.Locale;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Tag(
    name = "Commutes & Companion Tracking",
    description = "Night commute lifecycle, telemetry pings, safety incidents, and companion live view")
public class CommuteController {

  private final CommuteCommandService commandService;
  private final CommuteQueryService queryService;
  private final IamContextFacade iamFacade;
  private final ResponseEntityAssembler responseAssembler;

  public CommuteController(
      CommuteCommandService commandService,
      CommuteQueryService queryService,
      IamContextFacade iamFacade,
      ResponseEntityAssembler responseAssembler) {
    this.commandService = commandService;
    this.queryService = queryService;
    this.iamFacade = iamFacade;
    this.responseAssembler = responseAssembler;
  }

  @PostMapping("/commutes")
  @Operation(summary = "Start a new monitored night commute")
  public ResponseEntity<?> start(
      @Valid @RequestBody StartCommuteResource body, HttpServletRequest request, Locale locale) {
    UUID workerId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toCreated(
        commandService.handle(CommutingRestAssembler.toCommand(workerId, body)),
        CommutingRestAssembler::toResource,
        c -> URI.create("/api/v1/commutes/" + c.getId()),
        request,
        locale);
  }

  @GetMapping({"/commutes", "/commutes/mine"})
  @Operation(summary = "List commutes for current worker")
  public ResponseEntity<?> mine(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      HttpServletRequest request,
      Locale locale) {
    UUID workerId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toOk(
        queryService.handle(new GetMyCommutesQuery(workerId, page, size)),
        CommutingRestAssembler::toPagedCommutes,
        request,
        locale);
  }

  @GetMapping("/commutes/{id}")
  @Operation(summary = "Get commute details by id")
  public ResponseEntity<?> view(
      @PathVariable UUID id, HttpServletRequest request, Locale locale) {
    var actor = iamFacade.resolveCurrentActor();
    return responseAssembler.toOk(
        queryService.handle(new GetCommuteByIdQuery(id, actor.id(), actor.isStaff())),
        CommutingRestAssembler::toResource,
        request,
        locale);
  }

  @PostMapping("/commutes/{id}/telemetry")
  @Operation(summary = "Record a GPS telemetry ping on an active commute")
  public ResponseEntity<?> telemetry(
      @PathVariable UUID id,
      @Valid @RequestBody RecordTelemetryPingResource body,
      HttpServletRequest request,
      Locale locale) {
    UUID actorId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toCreated(
        commandService.handle(CommutingRestAssembler.toCommand(id, actorId, body)),
        CommutingRestAssembler::toResource,
        p -> URI.create("/api/v1/commutes/" + id + "/telemetry"),
        request,
        locale);
  }

  @GetMapping("/commutes/{id}/telemetry")
  @Operation(summary = "List recorded telemetry pings for a commute")
  public ResponseEntity<?> route(
      @PathVariable UUID id, HttpServletRequest request, Locale locale) {
    var actor = iamFacade.resolveCurrentActor();
    return responseAssembler.toOk(
        queryService.handle(new GetTelemetryPingsQuery(id, actor.id(), actor.isStaff())),
        CommutingRestAssembler::toTelemetryResources,
        request,
        locale);
  }

  @PostMapping("/commutes/{id}/delay")
  @Operation(summary = "Extend estimated arrival deadline for an active commute")
  public ResponseEntity<?> delay(
      @PathVariable UUID id,
      @Valid @RequestBody DelayCommuteResource body,
      HttpServletRequest request,
      Locale locale) {
    UUID actorId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toOk(
        commandService.handle(CommutingRestAssembler.toCommand(id, actorId, body)),
        CommutingRestAssembler::toResource,
        request,
        locale);
  }

  @PostMapping("/commutes/{id}/arrive")
  @Operation(summary = "Confirm safe arrival at destination")
  public ResponseEntity<?> arrive(
      @PathVariable UUID id, HttpServletRequest request, Locale locale) {
    UUID actorId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toOk(
        commandService.handle(new ArriveCommuteCommand(id, actorId)),
        CommutingRestAssembler::toResource,
        request,
        locale);
  }

  @PostMapping("/commutes/{id}/incidents")
  @Operation(summary = "Trigger SOS or safety incident on an active commute")
  public ResponseEntity<?> triggerIncident(
      @PathVariable UUID id,
      @Valid @RequestBody ReportSafetyIncidentResource body,
      HttpServletRequest request,
      Locale locale) {
    UUID actorId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toCreated(
        commandService.handle(CommutingRestAssembler.toCommand(id, actorId, body)),
        CommutingRestAssembler::toResource,
        i -> URI.create("/api/v1/commutes/" + id + "/incidents"),
        request,
        locale);
  }

  @GetMapping("/commutes/{id}/incidents")
  @Operation(summary = "List safety incidents recorded on a commute")
  public ResponseEntity<?> incidents(
      @PathVariable UUID id, HttpServletRequest request, Locale locale) {
    var actor = iamFacade.resolveCurrentActor();
    return responseAssembler.toOk(
        queryService.handle(new GetCommuteIncidentsQuery(id, actor.id(), actor.isStaff())),
        CommutingRestAssembler::toIncidentResources,
        request,
        locale);
  }

  @PostMapping("/incidents/{id}/resolve")
  @Operation(summary = "Resolve an open safety incident")
  public ResponseEntity<?> resolveIncident(
      @PathVariable UUID id, HttpServletRequest request, Locale locale) {
    var actor = iamFacade.resolveCurrentActor();
    return responseAssembler.toOk(
        commandService.handle(new ResolveSafetyIncidentCommand(id, actor.id(), actor.isStaff())),
        CommutingRestAssembler::toResource,
        request,
        locale);
  }

  @GetMapping("/companion/live")
  @Operation(summary = "Get live tracking status of monitored workers for current companion")
  public ResponseEntity<?> companionLive(HttpServletRequest request, Locale locale) {
    UUID contactId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toOk(
        queryService.handle(new GetCompanionLiveQuery(contactId)),
        CommutingRestAssembler::toCompanionLiveResources,
        request,
        locale);
  }

  @GetMapping("/companion/alerts")
  @Operation(summary = "List safety alerts received by current trusted companion")
  public ResponseEntity<?> companionAlerts(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      HttpServletRequest request,
      Locale locale) {
    UUID contactId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toOk(
        queryService.handle(new GetCompanionAlertsQuery(contactId, page, size)),
        CommutingRestAssembler::toPagedAlerts,
        request,
        locale);
  }
}
