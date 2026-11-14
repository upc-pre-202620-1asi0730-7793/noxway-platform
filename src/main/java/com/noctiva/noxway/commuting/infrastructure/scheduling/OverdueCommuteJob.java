package com.noctiva.noxway.commuting.infrastructure.scheduling;

import com.noctiva.noxway.commuting.application.commandservices.CommuteCommandService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OverdueCommuteJob {

  private final CommuteCommandService commuteCommandService;

  public OverdueCommuteJob(CommuteCommandService commuteCommandService) {
    this.commuteCommandService = commuteCommandService;
  }

  @Scheduled(fixedDelayString = "${noxway.overdue-check-ms:60000}")
  public void run() {
    commuteCommandService.evaluateOverdueCommutes();
  }
}
