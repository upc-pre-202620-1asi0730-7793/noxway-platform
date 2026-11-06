package com.noctiva.noxway.identity.interfaces.rest;

import com.noctiva.noxway.identity.application.IdentityApplicationService;
import com.noctiva.noxway.identity.application.commands.IdentityCommands;
import com.noctiva.noxway.identity.application.results.AccountResult;
import com.noctiva.noxway.identity.domain.model.aggregates.*;
import com.noctiva.noxway.shared.infrastructure.security.Actor;
import com.noctiva.noxway.shared.interfaces.rest.Pages;
import jakarta.validation.Valid;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class IdentityController {
  private final IdentityApplicationService service;

  public IdentityController(IdentityApplicationService s) {
    service = s;
  }

  @PostMapping("/auth/register")
  @ResponseStatus(HttpStatus.CREATED)
  public AccountResult register(@Valid @RequestBody IdentityCommands.Register c) {
    return AccountResult.from(service.register(c));
  }

  @PostMapping("/auth/login")
  public IdentityApplicationService.LoginResult login(
      @Valid @RequestBody IdentityCommands.Login c) {
    return service.login(c);
  }

  @PostMapping("/auth/logout")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void logout(@RequestHeader("Authorization") String h) {
    service.logout(h);
  }

  @GetMapping("/accounts/me")
  public AccountResult me() {
    return AccountResult.from(service.account(Actor.current().id()));
  }

  @PutMapping("/accounts/me")
  public AccountResult profile(@Valid @RequestBody IdentityCommands.Profile c) {
    return AccountResult.from(service.profile(c));
  }

  @PutMapping("/accounts/me/password")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void password(@Valid @RequestBody IdentityCommands.Password c) {
    service.changePassword(c);
  }

  @PostMapping("/trust-links")
  @ResponseStatus(HttpStatus.CREATED)
  public TrustLink invite(@Valid @RequestBody IdentityCommands.InviteTrustedContact c) {
    return service.invite(c.contactEmail());
  }

  @GetMapping("/trust-links")
  public Pages<TrustLink> links(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return Pages.of(service.myLinks(), page, size);
  }

  @PostMapping("/trust-links/{id}/accept")
  public TrustLink accept(@PathVariable UUID id) {
    return service.respond(id, "ACCEPT");
  }

  @PostMapping("/trust-links/{id}/reject")
  public TrustLink reject(@PathVariable UUID id) {
    return service.respond(id, "REJECT");
  }

  @DeleteMapping("/trust-links/{id}")
  public TrustLink revoke(@PathVariable UUID id) {
    return service.respond(id, "REVOKE");
  }

  @GetMapping("/accounts/me/notification-preferences")
  public List<NotificationPreference> preferences() {
    return service.preferences();
  }

  @PutMapping("/accounts/me/notification-preferences")
  public NotificationPreference preference(@Valid @RequestBody IdentityCommands.Preference c) {
    return service.preference(c);
  }
}
