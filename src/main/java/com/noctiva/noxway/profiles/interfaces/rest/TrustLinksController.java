package com.noctiva.noxway.profiles.interfaces.rest;

import com.noctiva.noxway.iam.interfaces.acl.IamContextFacade;
import com.noctiva.noxway.profiles.application.commands.ProfileCommands.*;
import com.noctiva.noxway.profiles.application.commandservices.ProfileCommandService;
import com.noctiva.noxway.profiles.application.queries.ProfileQueries.GetTrustLinksQuery;
import com.noctiva.noxway.profiles.application.queryservices.ProfileQueryService;
import com.noctiva.noxway.profiles.interfaces.rest.resources.CreateTrustLinkResource;
import com.noctiva.noxway.profiles.interfaces.rest.transform.ProfileRestAssembler;
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
@RequestMapping("/api/v1/trust-links")
@Tag(name = "Trust Links", description = "Trusted contact invitations, consent acceptance, and revocation")
public class TrustLinksController {

  private final ProfileCommandService commandService;
  private final ProfileQueryService queryService;
  private final IamContextFacade iamFacade;
  private final ResponseEntityAssembler responseAssembler;

  public TrustLinksController(
      ProfileCommandService commandService,
      ProfileQueryService queryService,
      IamContextFacade iamFacade,
      ResponseEntityAssembler responseAssembler) {
    this.commandService = commandService;
    this.queryService = queryService;
    this.iamFacade = iamFacade;
    this.responseAssembler = responseAssembler;
  }

  @PostMapping
  @Operation(summary = "Invite a trusted contact by email")
  public ResponseEntity<?> create(
      @Valid @RequestBody CreateTrustLinkResource body,
      HttpServletRequest request,
      Locale locale) {
    UUID actorId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toCreated(
        commandService.handle(new CreateTrustLinkCommand(actorId, body.contactEmail())),
        ProfileRestAssembler::toResource,
        link -> URI.create("/api/v1/trust-links/" + link.getId()),
        request,
        locale);
  }

  @GetMapping
  @Operation(summary = "List trust links involving the current actor (enriched with contact and worker names)")
  public ResponseEntity<?> list(HttpServletRequest request, Locale locale) {
    UUID actorId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toOk(
        queryService.handle(new GetTrustLinksQuery(actorId)),
        list -> list.stream().map(ProfileRestAssembler::toResource).toList(),
        request,
        locale);
  }

  @PostMapping("/{id}/accept")
  @Operation(summary = "Accept a pending trust link invitation as the invited trusted contact")
  public ResponseEntity<?> accept(
      @PathVariable UUID id, HttpServletRequest request, Locale locale) {
    UUID actorId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toOk(
        commandService.handle(new AcceptTrustLinkCommand(id, actorId)),
        ProfileRestAssembler::toResource,
        request,
        locale);
  }

  @PostMapping("/{id}/reject")
  @Operation(summary = "Reject a pending trust link invitation")
  public ResponseEntity<?> reject(
      @PathVariable UUID id, HttpServletRequest request, Locale locale) {
    UUID actorId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toOk(
        commandService.handle(new RejectTrustLinkCommand(id, actorId)),
        ProfileRestAssembler::toResource,
        request,
        locale);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Revoke an existing trust link")
  public ResponseEntity<?> revoke(
      @PathVariable UUID id, HttpServletRequest request, Locale locale) {
    UUID actorId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toNoContent(
        commandService.handle(new RevokeTrustLinkCommand(id, actorId)), request, locale);
  }
}
