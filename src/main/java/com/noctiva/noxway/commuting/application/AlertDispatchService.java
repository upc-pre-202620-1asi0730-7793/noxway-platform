package com.noctiva.noxway.commuting.application;

import com.noctiva.noxway.commuting.domain.model.aggregates.Alert;
import com.noctiva.noxway.commuting.domain.repositories.AlertRepository;
import com.noctiva.noxway.commuting.domain.repositories.CommuteRepository;
import com.noctiva.noxway.commuting.domain.services.AlertDeliveryGateway;
import com.noctiva.noxway.profiles.interfaces.acl.ProfileDirectoryFacade;
import com.noctiva.noxway.profiles.interfaces.acl.ProfileDirectoryFacade.NotificationChannelView;
import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.application.result.Result;
import com.noctiva.noxway.shared.domain.RuleViolation;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlertDispatchService {

  private final AlertRepository alertRepository;
  private final CommuteRepository commuteRepository;
  private final ProfileDirectoryFacade profileDirectory;
  private final AlertDeliveryGateway gateway;
  private final Clock clock;

  public AlertDispatchService(
      AlertRepository alertRepository,
      CommuteRepository commuteRepository,
      ProfileDirectoryFacade profileDirectory,
      AlertDeliveryGateway gateway,
      Clock clock) {
    this.alertRepository = alertRepository;
    this.commuteRepository = commuteRepository;
    this.profileDirectory = profileDirectory;
    this.gateway = gateway;
    this.clock = clock;
  }

  @Transactional
  public int dispatch() {
    int processed = 0;
    Instant now = clock.instant();
    for (var alert : alertRepository.findByStatus("PENDING_PROVIDER")) {
      var commute = commuteRepository.findById(alert.getCommuteId()).orElse(null);
      if (commute == null) {
        continue;
      }
      var prefs = profileDirectory.notificationPreferencesFor(alert.getContactId());
      var channelPref =
          prefs.stream()
              .filter(p -> p.channel().equals(alert.getChannel()) && p.enabled())
              .findFirst();
      boolean canAccompany =
          profileDirectory.isActiveTrustLink(commute.getWorkerId(), alert.getContactId());
      if (!canAccompany || channelPref.isEmpty()) {
        alert.markSuppressed();
        alertRepository.save(alert);
        processed++;
        continue;
      }
      var target = profileDirectory.findById(alert.getContactId()).orElse(null);
      if (target == null) {
        alert.markSuppressed();
        alertRepository.save(alert);
        processed++;
        continue;
      }
      boolean priority = isPriorityNow(channelPref.get(), now);
      var result =
          gateway.deliver(
              alert.getChannel(),
              target.email(),
              target.phone(),
              alert.getMessage(),
              alert.getId().toString(),
              priority);
      if (result == AlertDeliveryGateway.Result.DELIVERED) {
        alert.markDelivered(now);
        alertRepository.save(alert);
        processed++;
      } else if (result == AlertDeliveryGateway.Result.FAILED) {
        alert.markFailed();
        alertRepository.save(alert);
        processed++;
      }
    }
    return processed;
  }

  @Transactional(readOnly = true)
  public Result<List<Alert>, ApplicationError> queue() {
    var list =
        alertRepository.findAll().stream()
            .filter(a -> !"IN_APP".equals(a.getChannel()))
            .sorted(Comparator.comparing(Alert::getCreatedAt).reversed())
            .toList();
    return Result.success(list);
  }

  @Transactional
  public Result<Alert, ApplicationError> retry(UUID alertId) {
    var alertOpt = alertRepository.findById(alertId);
    if (alertOpt.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError("commuting.alert.not_found", "Alert not found"));
    }
    try {
      var alert = alertOpt.get();
      alert.retry();
      return Result.success(alertRepository.save(alert));
    } catch (RuleViolation ex) {
      return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
    }
  }

  private static boolean isPriorityNow(NotificationChannelView pref, Instant now) {
    if (pref.priorityStart() == null
        || pref.priorityStart().isBlank()
        || pref.priorityEnd() == null
        || pref.priorityEnd().isBlank()) {
      return false;
    }
    try {
      ZoneId zone =
          (pref.timeZone() == null || pref.timeZone().isBlank())
              ? ZoneId.of("America/Lima")
              : ZoneId.of(pref.timeZone());
      LocalTime current = now.atZone(zone).toLocalTime();
      LocalTime start = LocalTime.parse(pref.priorityStart());
      LocalTime end = LocalTime.parse(pref.priorityEnd());
      if (!start.isAfter(end)) {
        return !current.isBefore(start) && !current.isAfter(end);
      }
      return !current.isBefore(start) || !current.isAfter(end);
    } catch (Exception ex) {
      return false;
    }
  }
}
