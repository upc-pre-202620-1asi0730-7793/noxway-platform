package com.noctiva.noxway;

import static org.junit.jupiter.api.Assertions.*;

import com.noctiva.noxway.community.domain.model.aggregates.RiskZone;
import com.noctiva.noxway.community.domain.model.aggregates.ServicePoint;
import com.noctiva.noxway.commuting.domain.model.aggregates.Alert;
import com.noctiva.noxway.commuting.domain.model.aggregates.Commute;
import com.noctiva.noxway.commuting.domain.model.events.CommuteArrivedEvent;
import com.noctiva.noxway.commuting.domain.model.events.CommuteDelayedEvent;
import com.noctiva.noxway.commuting.domain.model.events.CommuteStartedEvent;
import com.noctiva.noxway.profiles.domain.model.aggregates.AccountProfile;
import com.noctiva.noxway.profiles.domain.model.aggregates.TrustLink;
import com.noctiva.noxway.shared.domain.RuleViolation;
import com.noctiva.noxway.subscriptions.domain.model.aggregates.PaymentTransaction;
import com.noctiva.noxway.subscriptions.domain.model.aggregates.Subscription;
import com.noctiva.noxway.wellness.domain.model.aggregates.RestSession;
import com.noctiva.noxway.wellness.domain.model.aggregates.SleepGoal;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AggregateLifecycleTest {

  @Test
  void commuteStartDelayArriveEmitsDomainEventsAndGuardsArrival() {
    UUID workerId = UUID.randomUUID();
    Instant start = Instant.parse("2026-06-01T22:00:00Z");
    Instant eta = start.plus(Duration.ofMinutes(40));

    Commute commute = Commute.start(workerId, "Hospital Loayza", "San Miguel", "BUS", start, eta, 10);
    assertEquals("ACTIVE", commute.getStatus());
    assertTrue(commute.isOngoing());
    assertEquals(1, commute.getDomainEvents().size());
    assertInstanceOf(CommuteStartedEvent.class, commute.getDomainEvents().get(0));

    commute.extendDelay(15, start.plus(Duration.ofMinutes(35)));
    assertEquals("DELAYED", commute.getStatus());
    assertEquals(eta.plus(Duration.ofMinutes(25)), commute.getDelayUntil());
    assertInstanceOf(CommuteDelayedEvent.class, commute.getDomainEvents().get(1));

    Instant arrival = start.plus(Duration.ofMinutes(58));
    commute.arrive(arrival);
    assertEquals("ARRIVED", commute.getStatus());
    assertFalse(commute.isOngoing());
    assertEquals(arrival, commute.getArrivalTime());
    assertInstanceOf(CommuteArrivedEvent.class, commute.getDomainEvents().get(2));

    assertThrows(RuleViolation.class, () -> commute.extendDelay(5, arrival.plusSeconds(60)));
    assertThrows(RuleViolation.class, () -> commute.arrive(arrival.plusSeconds(120)));
  }

  @Test
  void alertStatusTransitionsHandleDeliveryFailureAndRetry() {
    Alert alert =
        Alert.create(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            "EMAIL",
            "PENDING",
            "Shift SOS alert triggered",
            null);
    assertEquals("PENDING", alert.getStatus());

    alert.markFailed();
    assertEquals("FAILED", alert.getStatus());

    alert.retry();
    assertEquals("PENDING_PROVIDER", alert.getStatus());

    Instant deliveredAt = Instant.now();
    alert.markDelivered(deliveredAt);
    assertEquals("DELIVERED", alert.getStatus());
    assertEquals(deliveredAt, alert.getDeliveredAt());
    assertThrows(RuleViolation.class, alert::retry);
  }

  @Test
  void accountProfileAndTrustLinkEnforceDomainRules() {
    AccountProfile worker =
        AccountProfile.create(
            null,
            "worker@noxway.pe",
            "Ana Torres",
            "+51987654321",
            "WORKER",
            "NIGHT",
            "22:00",
            "06:00",
            "America/Lima",
            "hash");
    worker.updateProfile("Ana Torres Vega", "+51999111222", "ROTATING", "21:00", "05:00", "America/Lima");
    assertEquals("Ana Torres Vega", worker.getFullName());

    assertThrows(
        RuleViolation.class,
        () ->
            AccountProfile.create(
                null,
                "bad@noxway.pe",
                "Invalid",
                "123",
                "WORKER",
                "NIGHT",
                "22:00",
                "06:00",
                "America/Lima",
                "hash"));

    UUID contactId = UUID.randomUUID();
    TrustLink link = TrustLink.createPending(worker.getId(), contactId);
    assertTrue(link.isPending());

    assertThrows(RuleViolation.class, () -> link.accept(UUID.randomUUID()));
    link.accept(contactId);
    assertTrue(link.isActive());
    link.revoke(worker.getId());
    assertEquals("REVOKED", link.getStatus());
    assertThrows(RuleViolation.class, () -> link.accept(contactId));
  }

  @Test
  void subscriptionPaymentAndCommunityModerationEnforceStateTransitions() {
    Subscription sub =
        Subscription.create(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "PENDING_PAYMENT",
            Instant.now(),
            Instant.now().plus(Duration.ofDays(30)));
    assertFalse(sub.isActiveAt(Instant.now()));

    PaymentTransaction tx =
        PaymentTransaction.createPending(sub.getId(), sub.getWorkerId(), new BigDecimal("12.00"), "PEN");
    tx.confirmOffline("YAPE-9988");
    assertEquals("CONFIRMED", tx.getStatus());
    assertThrows(RuleViolation.class, tx::reject);

    sub.activateFrom(Instant.now());
    assertTrue(sub.isActiveAt(Instant.now().plusSeconds(60)));
    sub.extendDays(15);
    sub.cancel();
    assertEquals("CANCELLED", sub.getStatus());
    assertThrows(RuleViolation.class, sub::cancel);

    ServicePoint point =
        ServicePoint.create(
            UUID.randomUUID(), "Botica Nocturna", "PHARMACY", -12.05, -77.04, "America/Lima", "PENDING");
    assertEquals("PENDING", point.getStatus());
    point.decide(true);
    assertEquals("APPROVED", point.getStatus());

    RiskZone risk = RiskZone.create(UUID.randomUUID(), "Poca iluminación", "HIGH", -12.06, -77.05, "PENDING");
    risk.decide(true);
    assertEquals("APPROVED", risk.getStatus());
    risk.resolve();
    assertEquals("RESOLVED", risk.getStatus());
  }

  @Test
  void wellnessAggregatesEnforceMinAndMaxBounds() {
    SleepGoal goal = SleepGoal.create(UUID.randomUUID(), 450);
    goal.updateTarget(480);
    assertEquals(480, goal.getTargetMinutes());
    assertThrows(RuleViolation.class, () -> goal.updateTarget(180));

    Instant start = Instant.parse("2026-06-02T07:00:00Z");
    RestSession session = RestSession.create(UUID.randomUUID(), start, start.plus(Duration.ofHours(7)), 4, "Good");
    assertEquals(420, session.durationMinutes());
    assertTrue(session.overlaps(start.plus(Duration.ofHours(1)), start.plus(Duration.ofHours(2))));
    assertFalse(session.overlaps(start.plus(Duration.ofHours(7)), start.plus(Duration.ofHours(9))));
  }
}
