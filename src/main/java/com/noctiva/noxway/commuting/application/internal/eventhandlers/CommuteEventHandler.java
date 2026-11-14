package com.noctiva.noxway.commuting.application.internal.eventhandlers;

import com.noctiva.noxway.commuting.domain.model.aggregates.Alert;
import com.noctiva.noxway.commuting.domain.model.events.*;
import com.noctiva.noxway.commuting.domain.repositories.AlertRepository;
import com.noctiva.noxway.profiles.interfaces.acl.ProfileDirectoryFacade;
import com.noctiva.noxway.profiles.interfaces.acl.ProfileDirectoryFacade.AccountSummary;
import com.noctiva.noxway.profiles.interfaces.acl.ProfileDirectoryFacade.NotificationChannelView;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CommuteEventHandler {

  private final AlertRepository alertRepository;
  private final ProfileDirectoryFacade profileDirectory;

  public CommuteEventHandler(
      AlertRepository alertRepository, ProfileDirectoryFacade profileDirectory) {
    this.alertRepository = alertRepository;
    this.profileDirectory = profileDirectory;
  }

  @EventListener
  public void on(CommuteStartedEvent event) {
    String workerName = workerName(event.workerId());
    String message =
        workerName + " started night commute toward " + event.destination() + ".";
    dispatchToContacts(event.commuteId(), null, event.workerId(), message, event.occurredAt());
  }

  @EventListener
  public void on(CommuteDelayedEvent event) {
    String workerName = workerName(event.workerId());
    String message =
        workerName
            + " reported a "
            + event.minutes()
            + "-minute commute delay (new ETA threshold: "
            + event.delayUntil()
            + ").";
    dispatchToContacts(event.commuteId(), null, event.workerId(), message, event.occurredAt());
  }

  @EventListener
  public void on(CommuteArrivedEvent event) {
    String workerName = workerName(event.workerId());
    String message = workerName + " arrived safely home at " + event.arrivalTime() + ".";
    dispatchToContacts(event.commuteId(), null, event.workerId(), message, event.occurredAt());
  }

  @EventListener
  public void on(SafetyIncidentTriggeredEvent event) {
    String workerName = workerName(event.workerId());
    String message =
        "[" + event.type() + "] " + workerName + ": " + event.description();
    dispatchToContacts(
        event.commuteId(), event.incidentId(), event.workerId(), message, event.occurredAt());
  }

  public void notifyOverdue(UUID commuteId, UUID workerId, String destination, Instant now) {
    String workerName = workerName(workerId);
    String message =
        "OVERDUE CHECK-IN: " + workerName + " has not confirmed arrival at " + destination + ".";
    dispatchToContacts(commuteId, null, workerId, message, now);
  }

  private void dispatchToContacts(
      UUID commuteId, UUID incidentId, UUID workerId, String message, Instant now) {
    for (UUID contactId : profileDirectory.activeContactIdsForWorker(workerId)) {
      alertRepository.save(
          Alert.create(commuteId, incidentId, contactId, "IN_APP", "DELIVERED", message, now));
      var prefs = profileDirectory.notificationPreferencesFor(contactId);
      for (var pref : prefs) {
        if (pref.enabled() && !"IN_APP".equals(pref.channel())) {
          alertRepository.save(
              Alert.create(
                  commuteId,
                  incidentId,
                  contactId,
                  pref.channel(),
                  "PENDING_PROVIDER",
                  message,
                  null));
        }
      }
    }
  }

  private String workerName(UUID workerId) {
    return profileDirectory
        .findById(workerId)
        .map(AccountSummary::fullName)
        .filter(n -> !n.isBlank())
        .orElse("Worker");
  }
}
