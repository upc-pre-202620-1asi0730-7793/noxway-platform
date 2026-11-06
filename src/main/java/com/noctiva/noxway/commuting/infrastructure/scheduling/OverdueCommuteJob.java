package com.noctiva.noxway.commuting.infrastructure.scheduling;

import com.noctiva.noxway.commuting.application.CommuteApplicationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OverdueCommuteJob {
  private final CommuteApplicationService service;

  public OverdueCommuteJob(CommuteApplicationService s) {
    service = s;
  }

  @Scheduled(fixedDelayString = "${noxway.overdue-check-ms:60000}")
  public void run() {
    service.evaluateOverdue();
  }
}
