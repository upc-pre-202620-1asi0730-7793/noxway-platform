package com.noctiva.noxway.moderation.interfaces.rest;

import com.noctiva.noxway.iam.interfaces.acl.IamContextFacade;
import com.noctiva.noxway.moderation.application.commandservices.ModerationCommandService;
import com.noctiva.noxway.moderation.application.queries.ModerationQueries.*;
import com.noctiva.noxway.moderation.application.queryservices.ModerationQueryService;
import com.noctiva.noxway.moderation.interfaces.rest.resources.ModerationDecisionResource;
import com.noctiva.noxway.moderation.interfaces.rest.resources.SubmitReportResource;
import com.noctiva.noxway.moderation.interfaces.rest.transform.ModerationRestAssembler;
import com.noctiva.noxway.shared.application.result.ApplicationError;
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
@RequestMapping("/api/v1/moderation")
@Tag(
    name = "Moderation",
    description = "Community safety & service report review queue and decision audit log")
public class ModerationController {

  private final ModerationCommandService commandService;
  private final ModerationQueryService queryService;
  private final IamContextFacade iamFacade;
  private final ResponseEntityAssembler responseAssembler;

  public ModerationController(
      ModerationCommandService commandService,
      ModerationQueryService queryService,
      IamContextFacade iamFacade,
      ResponseEntityAssembler responseAssembler) {
    this.commandService = commandService;
    this.queryService = queryService;
    this.iamFacade = iamFacade;
    this.responseAssembler = responseAssembler;
  }

  @GetMapping("/reports")
  @Operation(summary = "List community moderation reports")
  public ResponseEntity<?> queue(
      @RequestParam(required = false) String status,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      HttpServletRequest request,
      Locale locale) {
    var actor = iamFacade.resolveCurrentActor();
    if (iamFacade.isSecurityEnabled() && !actor.isStaff()) {
      return responseAssembler.toErrorResponse(
          new ApplicationError.ForbiddenError("Moderator role required"), request, locale);
    }
    return responseAssembler.toOk(
        queryService.handle(new GetModerationQueueQuery(status, page, size)),
        ModerationRestAssembler::toPagedResources,
        request,
        locale);
  }

  @PostMapping("/reports")
  @Operation(summary = "Submit a community report for moderation")
  public ResponseEntity<?> report(
      @Valid @RequestBody SubmitReportResource body, HttpServletRequest request, Locale locale) {
    UUID reporterId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toCreated(
        commandService.handle(ModerationRestAssembler.toCommand(reporterId, body)),
        ModerationRestAssembler::toResource,
        r -> URI.create("/api/v1/moderation/reports/" + r.getId()),
        request,
        locale);
  }

  @PostMapping("/reports/{id}/decision")
  @Operation(summary = "Approve or reject a pending community moderation report")
  public ResponseEntity<?> decide(
      @PathVariable UUID id,
      @Valid @RequestBody ModerationDecisionResource body,
      HttpServletRequest request,
      Locale locale) {
    var actor = iamFacade.resolveCurrentActor();
    if (iamFacade.isSecurityEnabled() && !actor.isStaff()) {
      return responseAssembler.toErrorResponse(
          new ApplicationError.ForbiddenError("Moderator role required"), request, locale);
    }
    return responseAssembler.toOk(
        commandService.handle(ModerationRestAssembler.toCommand(id, actor.id(), body)),
        ModerationRestAssembler::toResource,
        request,
        locale);
  }

  @GetMapping("/reports/{id}/actions")
  @Operation(summary = "Get moderation audit actions for a report")
  public ResponseEntity<?> audit(
      @PathVariable UUID id, HttpServletRequest request, Locale locale) {
    var actor = iamFacade.resolveCurrentActor();
    if (iamFacade.isSecurityEnabled() && !actor.isStaff()) {
      return responseAssembler.toErrorResponse(
          new ApplicationError.ForbiddenError("Moderator role required"), request, locale);
    }
    return responseAssembler.toOk(
        queryService.handle(new GetModerationAuditQuery(id)),
        ModerationRestAssembler::toActionResources,
        request,
        locale);
  }
}
