package com.noctiva.noxway.subscriptions.application.internal.outboundservices.acl;

import com.noctiva.noxway.subscriptions.domain.model.aggregates.Plan;
import com.noctiva.noxway.subscriptions.domain.model.aggregates.Subscription;
import com.noctiva.noxway.subscriptions.domain.repositories.PlanRepository;
import com.noctiva.noxway.subscriptions.domain.repositories.SubscriptionMemberRepository;
import com.noctiva.noxway.subscriptions.domain.repositories.SubscriptionRepository;
import com.noctiva.noxway.subscriptions.interfaces.acl.ContactAllowance;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ContactAllowanceImpl implements ContactAllowance {

  private final SubscriptionRepository subscriptions;
  private final PlanRepository plans;
  private final SubscriptionMemberRepository members;
  private final Clock clock;

  public ContactAllowanceImpl(
      SubscriptionRepository subscriptions,
      PlanRepository plans,
      SubscriptionMemberRepository members,
      Clock clock) {
    this.subscriptions = subscriptions;
    this.plans = plans;
    this.members = members;
    this.clock = clock;
  }

  public List<Subscription> activeSubscriptionsOf(UUID workerId) {
    Instant now = clock.instant();
    var result = new ArrayList<Subscription>();
    subscriptions.findByWorkerId(workerId).stream()
        .filter(s -> s.isActiveAt(now))
        .forEach(result::add);
    members.findByWorkerId(workerId).stream()
        .filter(m -> "ACCEPTED".equals(m.getStatus()))
        .forEach(
            m ->
                subscriptions
                    .findById(m.getSubscriptionId())
                    .filter(s -> s.isActiveAt(now))
                    .ifPresent(result::add));
    return result;
  }

  @Override
  public int maxContacts(UUID workerId) {
    return activeSubscriptionsOf(workerId).stream()
        .flatMap(s -> plans.findById(s.getPlanId()).stream())
        .mapToInt(Plan::getMaxContacts)
        .max()
        .orElse(1);
  }

  public boolean hasEligiblePlan(UUID workerId, String requiredPlanCode) {
    if (requiredPlanCode == null
        || "FREE".equalsIgnoreCase(requiredPlanCode)
        || "ESSENTIAL".equalsIgnoreCase(requiredPlanCode)) {
      return true;
    }
    int requiredTier = tierOf(requiredPlanCode);
    return activeSubscriptionsOf(workerId).stream()
        .flatMap(s -> plans.findById(s.getPlanId()).stream())
        .anyMatch(
            p ->
                p.getCode().equalsIgnoreCase(requiredPlanCode)
                    || tierOf(p.getCode()) >= requiredTier);
  }

  private static int tierOf(String code) {
    if (code == null) {
      return 0;
    }
    return switch (code.toUpperCase(Locale.ROOT)) {
      case "FAMILY_CREW" -> 2;
      case "SENTINEL_PRO" -> 1;
      default -> 0;
    };
  }
}
