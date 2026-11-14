package com.noctiva.noxway.wellness.interfaces.rest;

import com.noctiva.noxway.iam.interfaces.acl.IamContextFacade;
import com.noctiva.noxway.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.noctiva.noxway.wellness.application.commands.WellnessCommands.DeleteRestSessionCommand;
import com.noctiva.noxway.wellness.application.commandservices.WellnessCommandService;
import com.noctiva.noxway.wellness.application.queries.WellnessQueries.*;
import com.noctiva.noxway.wellness.application.queryservices.WellnessQueryService;
import com.noctiva.noxway.wellness.interfaces.rest.resources.LogRestSessionResource;
import com.noctiva.noxway.wellness.interfaces.rest.resources.UpdateSleepGoalResource;
import com.noctiva.noxway.wellness.interfaces.rest.transform.WellnessRestAssembler;
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
@RequestMapping("/api/v1/wellness")
@Tag(name = "Wellness", description = "Rest session logging, sleep goals, and 7-day fatigue tracking")
public class WellnessController {

  private final WellnessCommandService commandService;
  private final WellnessQueryService queryService;
  private final IamContextFacade iamFacade;
  private final ResponseEntityAssembler responseAssembler;

  public WellnessController(
      WellnessCommandService commandService,
      WellnessQueryService queryService,
      IamContextFacade iamFacade,
      ResponseEntityAssembler responseAssembler) {
    this.commandService = commandService;
    this.queryService = queryService;
    this.iamFacade = iamFacade;
    this.responseAssembler = responseAssembler;
  }

  @PostMapping("/rest-sessions")
  @Operation(summary = "Log a new rest or sleep session")
  public ResponseEntity<?> logRestSession(
      @Valid @RequestBody LogRestSessionResource body,
      HttpServletRequest request,
      Locale locale) {
    UUID workerId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toCreated(
        commandService.handle(WellnessRestAssembler.toCommand(workerId, body)),
        WellnessRestAssembler::toResource,
        s -> URI.create("/api/v1/wellness/rest-sessions/" + s.getId()),
        request,
        locale);
  }

  @GetMapping("/rest-sessions")
  @Operation(summary = "List recorded rest sessions for current worker")
  public ResponseEntity<?> restSessions(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      HttpServletRequest request,
      Locale locale) {
    UUID workerId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toOk(
        queryService.handle(new GetRestSessionsQuery(workerId, page, size)),
        WellnessRestAssembler::toPagedResource,
        request,
        locale);
  }

  @DeleteMapping("/rest-sessions/{id}")
  @Operation(summary = "Delete a logged rest session")
  public ResponseEntity<?> deleteRestSession(
      @PathVariable UUID id, HttpServletRequest request, Locale locale) {
    UUID actorId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toNoContent(
        commandService.handle(new DeleteRestSessionCommand(id, actorId)), request, locale);
  }

  @PutMapping("/goal")
  @Operation(summary = "Update worker daily sleep goal target")
  public ResponseEntity<?> updateGoal(
      @Valid @RequestBody UpdateSleepGoalResource body,
      HttpServletRequest request,
      Locale locale) {
    UUID workerId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toOk(
        commandService.handle(WellnessRestAssembler.toCommand(workerId, body)),
        WellnessRestAssembler::toResource,
        request,
        locale);
  }

  @GetMapping("/goal")
  @Operation(summary = "Get active daily sleep goal for current worker")
  public ResponseEntity<?> getGoal(HttpServletRequest request, Locale locale) {
    UUID workerId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toOk(
        queryService.handle(new GetSleepGoalQuery(workerId)),
        WellnessRestAssembler::toResource,
        request,
        locale);
  }

  @GetMapping("/summary")
  @Operation(summary = "Get 7-day wellness rest summary and daily breakdown")
  public ResponseEntity<?> summary(
      @RequestParam(defaultValue = "America/Lima") String timeZone,
      HttpServletRequest request,
      Locale locale) {
    UUID workerId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toOk(
        queryService.handle(new GetWellnessSummaryQuery(workerId, timeZone)),
        WellnessRestAssembler::toResource,
        request,
        locale);
  }
}
