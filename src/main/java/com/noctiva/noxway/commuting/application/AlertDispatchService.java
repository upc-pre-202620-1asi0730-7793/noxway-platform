package com.noctiva.noxway.commuting.application;

import static com.noctiva.noxway.shared.domain.Rules.*;

import com.noctiva.noxway.commuting.domain.model.aggregates.*;
import com.noctiva.noxway.commuting.domain.repositories.*;
import com.noctiva.noxway.commuting.domain.services.AlertDeliveryGateway;
import com.noctiva.noxway.identity.interfaces.acl.IdentityAccess;
import com.noctiva.noxway.shared.infrastructure.security.Actor;
import java.time.Instant;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AlertDispatchService {
  private final AlertRepository alerts;
  private final CommuteRepository commutes;
  private final IdentityAccess identity;
  private final AlertDeliveryGateway gateway;

  public AlertDispatchService(
      AlertRepository a, CommuteRepository c, IdentityAccess i, AlertDeliveryGateway g) {
    alerts = a;
    commutes = c;
    identity = i;
    gateway = g;
  }

  @Transactional
  public void dispatch() {
    for (var a : alerts.findByStatus("PENDING_PROVIDER")) {
      var c = commutes.findById(a.commuteId()).orElseThrow();
      if (!identity.canAccompany(a.contactId(), c.workerId())
          || !identity.channelsFor(a.contactId()).contains(a.channel())) {
        alerts.save(a.withStatus("SUPPRESSED"));
        continue;
      }
      var target = identity.deliveryContact(a.contactId());
      var result =
          gateway.deliver(
              a.channel(),
              target.email(),
              target.phone(),
              a.message(),
              a.id().toString(),
              identity.priorityAt(a.contactId(), a.channel(), Instant.now()));
      if (result == AlertDeliveryGateway.Result.DELIVERED)
        alerts.save(a.withStatus("DELIVERED").withDeliveredAt(Instant.now()));
      else if (result == AlertDeliveryGateway.Result.FAILED) alerts.save(a.withStatus("FAILED"));
    }
  }

  public List<Alert> queue() {
    Actor.current().moderator();
    return alerts.findAll().stream().filter(a -> !a.channel().equals("IN_APP")).toList();
  }

  @Transactional
  public Alert retry(UUID id) {
    Actor.current().moderator();
    var a =
        alerts
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alert not found"));
    require(a.status().equals("FAILED"), "Only failed deliveries can be retried");
    return alerts.save(a.withStatus("PENDING_PROVIDER"));
  }
}
