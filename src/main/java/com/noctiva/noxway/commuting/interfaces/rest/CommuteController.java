package com.noctiva.noxway.commuting.interfaces.rest;

import com.noctiva.noxway.commuting.application.CommuteApplicationService;
import com.noctiva.noxway.commuting.application.commands.CommuteCommands;
import com.noctiva.noxway.commuting.domain.model.aggregates.*;
import com.noctiva.noxway.shared.interfaces.rest.Pages;
import jakarta.validation.Valid;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class CommuteController {
  private final CommuteApplicationService service;

  public CommuteController(CommuteApplicationService s) {
    service = s;
  }

  @PostMapping("/commutes")
  @ResponseStatus(HttpStatus.CREATED)
  public Commute start(@Valid @RequestBody CommuteCommands.Start c) {
    return service.start(c);
  }

  @GetMapping("/commutes")
  public Pages<Commute> list(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return Pages.of(service.own(), page, size);
  }

  @GetMapping("/workers/{id}/commutes")
  public Pages<Commute> accompanied(
      @PathVariable UUID id,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return Pages.of(service.accompanied(id), page, size);
  }

  @GetMapping("/commutes/{id}")
  public Commute view(@PathVariable UUID id) {
    return service.view(id);
  }

  @PostMapping("/commutes/{id}/arrival")
  public Commute arrive(@PathVariable UUID id) {
    return service.complete(id);
  }

  @PostMapping("/commutes/{id}/cancel")
  public Commute cancel(@PathVariable UUID id) {
    return service.cancel(id);
  }

  @PostMapping("/commutes/{id}/delay")
  public Commute delay(@PathVariable UUID id, @Valid @RequestBody CommuteCommands.Delay c) {
    return service.delay(id, c.minutes());
  }

  @PostMapping("/commutes/{id}/telemetry")
  @ResponseStatus(HttpStatus.CREATED)
  public TelemetryPing ping(@PathVariable UUID id, @Valid @RequestBody CommuteCommands.Ping c) {
    return service.ping(id, c);
  }

  @GetMapping("/commutes/{id}/telemetry")
  public List<TelemetryPing> telemetry(@PathVariable UUID id) {
    return service.telemetry(id);
  }

  @PostMapping("/commutes/{id}/incidents")
  @ResponseStatus(HttpStatus.CREATED)
  public SafetyIncident incident(
      @PathVariable UUID id, @Valid @RequestBody CommuteCommands.Incident c) {
    return service.incident(id, c);
  }

  @GetMapping("/commutes/{id}/incidents")
  public List<SafetyIncident> incidents(@PathVariable UUID id) {
    return service.incidents(id);
  }

  @PostMapping("/incidents/{id}/resolve")
  public SafetyIncident resolve(@PathVariable UUID id) {
    return service.resolve(id);
  }

  @GetMapping("/alerts")
  public Pages<Alert> alerts(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return Pages.of(service.myAlerts(), page, size);
  }

  @PostMapping("/alerts/{id}/read")
  public Alert read(@PathVariable UUID id) {
    return service.readAlert(id);
  }
}
