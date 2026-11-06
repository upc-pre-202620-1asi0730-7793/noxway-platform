package com.noctiva.noxway.subscriptions.application;

import com.noctiva.noxway.subscriptions.domain.model.aggregates.*;
import com.noctiva.noxway.subscriptions.domain.repositories.*;
import com.noctiva.noxway.subscriptions.interfaces.acl.ContactAllowance;
import java.time.Instant;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionEntitlements implements ContactAllowance {
  private final SubscriptionRepository subscriptions;
  private final PlanRepository plans;
  private final SubscriptionMemberRepository members;

  public SubscriptionEntitlements(
      SubscriptionRepository s, PlanRepository p, SubscriptionMemberRepository m) {
    subscriptions = s;
    plans = p;
    members = m;
  }

  public boolean active(Subscription s) {
    return s.status().equals("ACTIVE") && s.endsAt().isAfter(Instant.now());
  }

  public List<Subscription> subscriptionsOf(UUID worker) {
    var result = new ArrayList<Subscription>();
    subscriptions.findByWorkerId(worker).stream().filter(this::active).forEach(result::add);
    members.findByWorkerId(worker).stream()
        .filter(m -> m.status().equals("ACCEPTED"))
        .forEach(
            m ->
                subscriptions
                    .findById(m.subscriptionId())
                    .filter(this::active)
                    .ifPresent(result::add));
    return result;
  }

  public int maxContacts(UUID worker) {
    return subscriptionsOf(worker).stream()
        .map(s -> plans.findById(s.planId()).orElseThrow())
        .mapToInt(Plan::maxContacts)
        .max()
        .orElse(1);
  }

  public boolean hasPlan(UUID worker, String code) {
    return subscriptionsOf(worker).stream()
        .anyMatch(s -> plans.findById(s.planId()).map(p -> p.code().equals(code)).orElse(false));
  }
}
