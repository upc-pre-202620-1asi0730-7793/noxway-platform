package com.noctiva.noxway.profiles.interfaces.rest;

import com.noctiva.noxway.iam.interfaces.acl.IamContextFacade;
import com.noctiva.noxway.profiles.application.commands.ProfileCommands.UpdateNotificationPreferenceCommand;
import com.noctiva.noxway.profiles.application.commandservices.ProfileCommandService;
import com.noctiva.noxway.profiles.application.queries.ProfileQueries.*;
import com.noctiva.noxway.profiles.application.queryservices.ProfileQueryService;
import com.noctiva.noxway.profiles.interfaces.rest.resources.*;
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
@RequestMapping("/api/v1/accounts")
@Tag(name = "Accounts & Profiles", description = "Worker & Trusted Contact profiles and notification preferences")
public class AccountsController {

  private final ProfileCommandService commandService;
  private final ProfileQueryService queryService;
  private final IamContextFacade iamFacade;
  private final ResponseEntityAssembler responseAssembler;

  public AccountsController(
      ProfileCommandService commandService,
      ProfileQueryService queryService,
      IamContextFacade iamFacade,
      ResponseEntityAssembler responseAssembler) {
    this.commandService = commandService;
    this.queryService = queryService;
    this.iamFacade = iamFacade;
    this.responseAssembler = responseAssembler;
  }

  @GetMapping("/me")
  @Operation(summary = "Get active profile for current actor")
  public ResponseEntity<?> me(HttpServletRequest request, Locale locale) {
    UUID actorId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toOk(
        queryService.handle(new GetProfileByIdQuery(actorId)),
        ProfileRestAssembler::toResource,
        request,
        locale);
  }

  @PutMapping("/me")
  @Operation(summary = "Update active profile details and shift schedule")
  public ResponseEntity<?> updateMe(
      @Valid @RequestBody UpdateProfileResource body,
      HttpServletRequest request,
      Locale locale) {
    UUID actorId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toOk(
        commandService.handle(ProfileRestAssembler.toCommand(actorId, body)),
        ProfileRestAssembler::toResource,
        request,
        locale);
  }

  @GetMapping("/directory")
  @Operation(summary = "List active profiles available in workspace directory")
  public ResponseEntity<?> directory(HttpServletRequest request, Locale locale) {
    return responseAssembler.toOk(
        queryService.handle(new GetDirectoryQuery()),
        list -> list.stream().map(ProfileRestAssembler::toResource).toList(),
        request,
        locale);
  }

  @PostMapping
  @Operation(summary = "Create a worker or trusted contact profile")
  public ResponseEntity<?> createProfile(
      @Valid @RequestBody CreateProfileResource body,
      HttpServletRequest request,
      Locale locale) {
    return responseAssembler.toCreated(
        commandService.handle(ProfileRestAssembler.toCommand(body)),
        ProfileRestAssembler::toResource,
        created -> URI.create("/api/v1/accounts/" + created.getId()),
        request,
        locale);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get account profile by ID")
  public ResponseEntity<?> byId(
      @PathVariable UUID id, HttpServletRequest request, Locale locale) {
    return responseAssembler.toOk(
        queryService.handle(new GetProfileByIdQuery(id)),
        ProfileRestAssembler::toResource,
        request,
        locale);
  }

  @GetMapping("/me/notification-preferences")
  @Operation(summary = "List notification channel preferences for active profile")
  public ResponseEntity<?> notificationPreferences(HttpServletRequest request, Locale locale) {
    UUID actorId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toOk(
        queryService.handle(new GetNotificationPreferencesQuery(actorId)),
        list -> list.stream().map(ProfileRestAssembler::toResource).toList(),
        request,
        locale);
  }

  @PutMapping("/me/notification-preferences")
  @Operation(summary = "Update notification preference channel and priority window")
  public ResponseEntity<?> updateNotificationPreference(
      @Valid @RequestBody UpdateNotificationPreferenceResource body,
      HttpServletRequest request,
      Locale locale) {
    UUID actorId = iamFacade.resolveCurrentActor().id();
    UpdateNotificationPreferenceCommand cmd = ProfileRestAssembler.toCommand(actorId, body);
    return responseAssembler.toOk(
        commandService.handle(cmd), ProfileRestAssembler::toResource, request, locale);
  }
}
