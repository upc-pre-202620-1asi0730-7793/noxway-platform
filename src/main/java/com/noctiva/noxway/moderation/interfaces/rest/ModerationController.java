package com.noctiva.noxway.moderation.interfaces.rest;

import com.noctiva.noxway.moderation.application.ModerationApplicationService;
import com.noctiva.noxway.moderation.domain.model.aggregates.*;
import com.noctiva.noxway.shared.interfaces.rest.Pages;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/moderation")
public class ModerationController {
  private final ModerationApplicationService service;

  public ModerationController(ModerationApplicationService s) {
    service = s;
  }

  public record Report(
      @Pattern(regexp = "SERVICE|RISK") @NotNull String targetType,
      @NotNull UUID targetId,
      @NotBlank @Size(max = 1000) String reason) {}

  public record Decision(
      @Pattern(regexp = "APPROVE|REJECT") @NotNull String decision,
      @NotBlank @Size(max = 1000) String notes) {}

  @GetMapping("/reports")
  public Pages<CommunityReport> queue(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return Pages.of(service.queue(), page, size);
  }

  @PostMapping("/reports")
  @ResponseStatus(HttpStatus.CREATED)
  public CommunityReport report(@Valid @RequestBody Report c) {
    return service.report(c.targetType(), c.targetId(), c.reason());
  }

  @PostMapping("/reports/{id}/decision")
  public CommunityReport decide(@PathVariable UUID id, @Valid @RequestBody Decision c) {
    return service.decide(id, c.decision(), c.notes());
  }

  @GetMapping("/reports/{id}/actions")
  public List<ModerationAction> audit(@PathVariable UUID id) {
    return service.audit(id);
  }
}
