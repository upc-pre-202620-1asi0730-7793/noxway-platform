package com.noctiva.noxway.wellness.interfaces.rest;

import com.noctiva.noxway.shared.interfaces.rest.Pages;
import com.noctiva.noxway.wellness.application.WellnessApplicationService;
import com.noctiva.noxway.wellness.application.commands.WellnessCommands;
import com.noctiva.noxway.wellness.domain.model.aggregates.*;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wellness")
public class WellnessController {
  private final WellnessApplicationService service;

  public WellnessController(WellnessApplicationService s) {
    service = s;
  }

  @PostMapping("/rest-sessions")
  @ResponseStatus(HttpStatus.CREATED)
  public RestSession rest(@Valid @RequestBody WellnessCommands.Rest c) {
    return service.register(c);
  }

  @GetMapping("/rest-sessions")
  public Pages<RestSession> list(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return Pages.of(service.list(), page, size);
  }

  @DeleteMapping("/rest-sessions/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    service.delete(id);
  }

  @PutMapping("/goal")
  public SleepGoal goal(@Valid @RequestBody WellnessCommands.Goal c) {
    return service.goal(c.targetMinutes());
  }

  @GetMapping("/summary")
  public WellnessApplicationService.Summary summary(
      @RequestParam(required = false) LocalDate through,
      @RequestParam(defaultValue = "America/Lima") String timeZone) {
    return service.summary(through, timeZone);
  }
}
