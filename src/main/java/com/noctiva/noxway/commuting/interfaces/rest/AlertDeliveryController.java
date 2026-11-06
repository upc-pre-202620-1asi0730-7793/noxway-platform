package com.noctiva.noxway.commuting.interfaces.rest;

import com.noctiva.noxway.commuting.application.AlertDispatchService;
import com.noctiva.noxway.commuting.domain.model.aggregates.Alert;
import com.noctiva.noxway.shared.interfaces.rest.Pages;
import java.util.UUID;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/alert-deliveries")
public class AlertDeliveryController {
  private final AlertDispatchService service;

  public AlertDeliveryController(AlertDispatchService s) {
    service = s;
  }

  @GetMapping
  public Pages<Alert> queue(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return Pages.of(service.queue(), page, size);
  }

  @PostMapping("/{id}/retry")
  public Alert retry(@PathVariable UUID id) {
    return service.retry(id);
  }
}
