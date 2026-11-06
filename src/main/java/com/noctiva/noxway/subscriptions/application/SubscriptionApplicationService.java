package com.noctiva.noxway.subscriptions.application;

import static com.noctiva.noxway.shared.domain.Rules.*;

import com.noctiva.noxway.identity.interfaces.acl.IdentityAccess;
import com.noctiva.noxway.shared.infrastructure.security.Actor;
import com.noctiva.noxway.subscriptions.application.commands.SubscriptionCommands;
import com.noctiva.noxway.subscriptions.domain.model.aggregates.*;
import com.noctiva.noxway.subscriptions.domain.repositories.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class SubscriptionApplicationService {
  private final PlanRepository plans;
  private final SubscriptionRepository subscriptions;
  private final PaymentTransactionRepository payments;
  private final CollectiveBenefitRepository benefits;
  private final BenefitClaimRepository claims;
  private final ReferralCodeRepository codes;
  private final ReferralUseRepository referrals;
  private final SubscriptionMemberRepository members;
  private final SubscriptionEntitlements entitlements;
  private final IdentityAccess identity;
  private final ReferralRewardRepository rewards;

  public SubscriptionApplicationService(
      PlanRepository p,
      SubscriptionRepository s,
      PaymentTransactionRepository pa,
      CollectiveBenefitRepository b,
      BenefitClaimRepository c,
      ReferralCodeRepository co,
      ReferralUseRepository r,
      SubscriptionMemberRepository m,
      SubscriptionEntitlements e,
      IdentityAccess i,
      ReferralRewardRepository rewards) {
    plans = p;
    subscriptions = s;
    payments = pa;
    benefits = b;
    claims = c;
    codes = co;
    referrals = r;
    members = m;
    entitlements = e;
    identity = i;
    this.rewards = rewards;
  }

  public List<Plan> plans() {
    return plans.findAll().stream().filter(Plan::active).toList();
  }

  private Subscription get(UUID id) {
    return subscriptions
        .findById(id)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));
  }

  public Subscription subscribe(UUID planId) {
    var a = Actor.current();
    a.worker();
    identity.lockWorker(a.id());
    var p =
        plans
            .findById(planId)
            .filter(Plan::active)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found"));
    require(
        subscriptions.findByWorkerId(a.id()).stream()
            .noneMatch(s -> entitlements.active(s) || s.status().equals("PENDING_PAYMENT")),
        "Cancel current subscription before changing plans");
    Instant now = Instant.now();
    boolean free = p.monthlyPrice().compareTo(BigDecimal.ZERO) == 0;
    return subscriptions.save(
        Subscription.create(
            a.id(),
            p.id(),
            free ? "ACTIVE" : "PENDING_PAYMENT",
            now,
            now.plus(Duration.ofDays(free ? 36500 : 30))));
  }

  public List<Subscription> mine() {
    var all = new ArrayList<>(subscriptions.findByWorkerId(Actor.current().id()));
    for (var m : members.findByWorkerId(Actor.current().id()))
      if (m.status().equals("ACCEPTED"))
        subscriptions.findById(m.subscriptionId()).ifPresent(all::add);
    return all;
  }

  public Subscription cancel(UUID id) {
    var s = get(id);
    Actor.current().own(s.workerId());
    identity.lockWorker(s.workerId());
    require(!s.status().equals("CANCELLED"), "Subscription already cancelled");
    for (var m : members.findBySubscriptionId(id)) members.save(m.withStatus("REVOKED"));
    return subscriptions.save(s.withStatus("CANCELLED").withEndsAt(Instant.now()));
  }

  public PaymentTransaction requestPayment(UUID id) {
    var s = get(id);
    Actor.current().own(s.workerId());
    identity.lockWorker(s.workerId());
    require(
        s.status().equals("PENDING_PAYMENT") || entitlements.active(s),
        "Subscription is not payable");
    require(
        payments.findBySubscriptionId(id).stream().noneMatch(p -> p.status().equals("PENDING")),
        "Payment already pending");
    var plan = plans.findById(s.planId()).orElseThrow();
    require(plan.monthlyPrice().compareTo(BigDecimal.ZERO) > 0, "Free plan requires no payment");
    return payments.save(
        PaymentTransaction.create(
            id,
            s.workerId(),
            plan.monthlyPrice(),
            "PEN",
            "UNASSIGNED-" + UUID.randomUUID(),
            "PENDING"));
  }

  public PaymentTransaction confirmPayment(UUID id, String reference) {
    var actor = Actor.current();
    require(actor.role().equals("ADMIN"), "Admin role required for offline payment reconciliation");
    var payment =
        payments
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
    identity.lockWorker(payment.workerId());
    require(payment.status().equals("PENDING"), "Payment already processed");
    var subscription = get(payment.subscriptionId());
    require(!subscription.status().equals("CANCELLED"), "Subscription cancelled");
    require(
        payments.findAll().stream().noneMatch(p -> p.reference().equals(reference)),
        "Receipt reference already used");
    boolean renewal = subscription.status().equals("ACTIVE");
    Instant base =
        renewal && subscription.endsAt().isAfter(Instant.now())
            ? subscription.endsAt()
            : Instant.now();
    subscriptions.save(
        subscription
            .withStatus("ACTIVE")
            .withStartsAt(renewal ? subscription.startsAt() : Instant.now())
            .withEndsAt(base.plus(Duration.ofDays(30))));
    var confirmed = payments.save(payment.withReference(reference).withStatus("CONFIRMED_OFFLINE"));
    if (payments.findByWorkerId(payment.workerId()).stream()
            .filter(p -> p.status().equals("CONFIRMED_OFFLINE"))
            .count()
        == 1) {
      referrals.findByReferredWorkerId(payment.workerId()).stream()
          .findFirst()
          .ifPresent(
              use -> {
                var code = codes.findById(use.referralId()).orElseThrow();
                rewards.save(ReferralReward.create(payment.workerId(), use.id(), 30, "AVAILABLE"));
                rewards.save(ReferralReward.create(code.workerId(), use.id(), 30, "AVAILABLE"));
              });
    }
    return confirmed;
  }

  public PaymentTransaction rejectPayment(UUID id) {
    require(Actor.current().role().equals("ADMIN"), "Admin role required");
    var p =
        payments
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
    require(p.status().equals("PENDING"), "Payment already processed");
    return payments.save(p.withStatus("REJECTED"));
  }

  public List<PaymentTransaction> payments() {
    return Actor.current().role().equals("ADMIN")
        ? payments.findAll()
        : payments.findByWorkerId(Actor.current().id());
  }

  public List<CollectiveBenefit> benefits() {
    return benefits.findAll().stream().filter(CollectiveBenefit::active).toList();
  }

  public CollectiveBenefit benefit(SubscriptionCommands.Benefit c) {
    Actor.current().moderator();
    require(plans.findByCode(c.planCode()).stream().anyMatch(Plan::active), "Unknown plan code");
    return benefits.save(
        CollectiveBenefit.create(c.name(), c.description(), c.planCode(), c.active()));
  }

  public BenefitClaim claim(UUID id) {
    var a = Actor.current();
    a.worker();
    identity.lockWorker(a.id());
    var b =
        benefits
            .findById(id)
            .filter(CollectiveBenefit::active)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Benefit not found"));
    require(
        entitlements.hasPlan(a.id(), b.planCode()), "Benefit requires its associated active plan");
    require(
        claims.findByWorkerId(a.id()).stream().noneMatch(c -> c.benefitId().equals(id)),
        "Benefit already claimed");
    return claims.save(BenefitClaim.create(a.id(), id, UUID.randomUUID().toString()));
  }

  public List<BenefitClaim> claims() {
    return claims.findByWorkerId(Actor.current().id());
  }

  public ReferralCode code() {
    var a = Actor.current();
    a.worker();
    identity.lockWorker(a.id());
    return codes.findByWorkerId(a.id()).stream()
        .findFirst()
        .orElseGet(
            () ->
                codes.save(
                    ReferralCode.create(
                        a.id(),
                        UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 12)
                            .toUpperCase(Locale.ROOT))));
  }

  public ReferralUse useCode(String code) {
    var a = Actor.current();
    a.worker();
    identity.lockWorker(a.id());
    var c =
        codes.findByCode(code.toUpperCase(Locale.ROOT)).stream()
            .findFirst()
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Referral code not found"));
    require(!c.workerId().equals(a.id()), "Cannot use own referral code");
    require(referrals.findByReferredWorkerId(a.id()).isEmpty(), "Referral already registered");
    return referrals.save(ReferralUse.create(c.id(), a.id()));
  }

  public SubscriptionMember invite(UUID id, String email) {
    var s = get(id);
    Actor.current().own(s.workerId());
    identity.lockWorker(s.workerId());
    require(entitlements.active(s), "Active subscription required");
    int max = plans.findById(s.planId()).orElseThrow().maxWorkers();
    var existing = members.findBySubscriptionId(id);
    require(
        existing.stream().filter(m -> Set.of("PENDING", "ACCEPTED").contains(m.status())).count()
                + 1
            < max,
        "Plan worker capacity reached");
    UUID worker = identity.workerByEmail(email);
    require(!worker.equals(s.workerId()), "Owner is already included");
    var old = existing.stream().filter(m -> m.workerId().equals(worker)).findFirst();
    if (old.isPresent()) {
      require(
          Set.of("REVOKED", "REJECTED").contains(old.get().status()),
          "Member invitation already exists");
      return members.save(old.get().withStatus("PENDING"));
    }
    return members.save(SubscriptionMember.create(id, worker, "PENDING"));
  }

  public List<SubscriptionMember> invitations() {
    return members.findByWorkerId(Actor.current().id());
  }

  public SubscriptionMember respond(UUID id, boolean accept) {
    var m =
        members
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invitation not found"));
    Actor.current().own(m.workerId());
    var s = get(m.subscriptionId());
    identity.lockWorker(s.workerId());
    require(m.status().equals("PENDING") && entitlements.active(s), "Invitation no longer active");
    return members.save(m.withStatus(accept ? "ACCEPTED" : "REJECTED"));
  }

  public void removeMember(UUID id) {
    var m =
        members
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));
    var s = get(m.subscriptionId());
    require(
        Actor.current().id().equals(m.workerId()) || Actor.current().id().equals(s.workerId()),
        "Not a group participant");
    identity.lockWorker(s.workerId());
    members.save(m.withStatus("REVOKED"));
  }

  public List<ReferralReward> rewards() {
    return rewards.findByWorkerId(Actor.current().id());
  }

  public ReferralReward redeemReward(UUID id) {
    var a = Actor.current();
    a.worker();
    identity.lockWorker(a.id());
    var reward =
        rewards
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reward not found"));
    a.own(reward.workerId());
    require(reward.status().equals("AVAILABLE"), "Reward already redeemed");
    var subscription =
        subscriptions.findByWorkerId(a.id()).stream()
            .filter(entitlements::active)
            .filter(s -> plans.findById(s.planId()).orElseThrow().monthlyPrice().signum() > 0)
            .findFirst()
            .orElseThrow(
                () ->
                    new com.noctiva.noxway.shared.domain.RuleViolation(
                        "An active paid plan is needed to redeem credit"));
    subscriptions.save(
        subscription.withEndsAt(subscription.endsAt().plus(Duration.ofDays(reward.days()))));
    return rewards.save(reward.withStatus("REDEEMED"));
  }

  public List<SubscriptionMember> group(UUID id) {
    var s = get(id);
    Actor.current().own(s.workerId());
    return members.findBySubscriptionId(id);
  }
}
