package com.noctiva.noxway.commuting.infrastructure.scheduling;

import com.noctiva.noxway.commuting.application.AlertDispatchService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AlertDeliveryJob {
  private final AlertDispatchService service;

  public AlertDeliveryJob(AlertDispatchService s) {
    service = s;
  }

  @Scheduled(fixedDelayString = "${noxway.delivery-check-ms:30000}")
  public void run() {
    service.dispatch();
  }
}
