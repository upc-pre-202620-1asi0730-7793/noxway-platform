package com.noctiva.noxway.subscriptions.application.internal.commandservices;

import com.noctiva.noxway.profiles.interfaces.acl.ProfileDirectoryFacade;
import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.application.result.Result;
import com.noctiva.noxway.shared.domain.RuleViolation;
import com.noctiva.noxway.subscriptions.application.commands.SubscriptionCommands.*;
import com.noctiva.noxway.subscriptions.application.commandservices.SubscriptionCommandService;
import com.noctiva.noxway.subscriptions.application.internal.outboundservices.acl.ContactAllowanceImpl;
import com.noctiva.noxway.subscriptions.domain.model.aggregates.*;
import com.noctiva.noxway.subscriptions.domain.repositories.*;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SubscriptionCommandServiceImpl implements SubscriptionCommandService {

  private static final int REFERRAL_REWARD_DAYS = 15;

  private final PlanRepository planRepository;
  private final SubscriptionRepository subscriptionRepository;
  private final SubscriptionMemberRepository memberRepository;
  private final PaymentTransactionRepository paymentRepository;
  private final CollectiveBenefitRepository benefitRepository;
  private final BenefitClaimRepository claimRepository;
  private final ReferralCodeRepository referralCodeRepository;
  private final ReferralUseRepository referralUseRepository;
  private final ReferralRewardRepository referralRewardRepository;
  private final ContactAllowanceImpl entitlements;
  private final ProfileDirectoryFacade profileDirectory;
  private final Clock clock;

  public SubscriptionCommandServiceImpl(
      PlanRepository planRepository,
      SubscriptionRepository subscriptionRepository,
      SubscriptionMemberRepository memberRepository,
      PaymentTransactionRepository paymentRepository,
      CollectiveBenefitRepository benefitRepository,
      BenefitClaimRepository claimRepository,
      ReferralCodeRepository referralCodeRepository,
      ReferralUseRepository referralUseRepository,
      ReferralRewardRepository referralRewardRepository,
      ContactAllowanceImpl entitlements,
      ProfileDirectoryFacade profileDirectory,
      Clock clock) {
    this.planRepository = planRepository;
    this.subscriptionRepository = subscriptionRepository;
    this.memberRepository = memberRepository;
    this.paymentRepository = paymentRepository;
    this.benefitRepository = benefitRepository;
    this.claimRepository = claimRepository;
    this.referralCodeRepository = referralCodeRepository;
    this.referralUseRepository = referralUseRepository;
    this.referralRewardRepository = referralRewardRepository;
    this.entitlements = entitlements;
    this.profileDirectory = profileDirectory;
    this.clock = clock;
  }

  @Override
  public Result<Subscription, ApplicationError> handle(SubscribeCommand cmd) {
    var planOpt = planRepository.findById(cmd.planId());
    if (planOpt.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError(
              "subscriptions.plan.not_found", "Subscription plan not found"));
    }
    var plan = planOpt.get();
    if (!plan.isActive()) {
      return Result.failure(
          new ApplicationError.BusinessRuleError(
              "subscriptions.plan.inactive", "Selected plan is not currently active"));
    }

    Instant now = clock.instant();
    var owned = subscriptionRepository.findByWorkerId(cmd.workerId());
    boolean alreadyPendingSamePlan =
        owned.stream()
            .anyMatch(
                s ->
                    "PENDING_PAYMENT".equals(s.getStatus())
                        && s.getPlanId().equals(plan.getId()));
    if (alreadyPendingSamePlan) {
      return Result.failure(
          new ApplicationError.ConflictError(
              "subscriptions.already_pending",
              "A pending subscription for this plan already exists"));
    }

    boolean hasActivePaid =
        owned.stream()
            .filter(s -> s.isActiveAt(now))
            .anyMatch(
                s -> planRepository.findById(s.getPlanId()).map(Plan::isPaid).orElse(false));
    if (hasActivePaid) {
      return Result.failure(
          new ApplicationError.ConflictError(
              "subscriptions.active_exists",
              "Worker already has an active paid subscription"));
    }

    if (!plan.isPaid() && owned.stream().anyMatch(s -> s.isActiveAt(now))) {
      return Result.failure(
          new ApplicationError.ConflictError(
              "subscriptions.active_exists", "Worker already has an active subscription"));
    }

    try {
      String initialStatus = plan.isPaid() ? "PENDING_PAYMENT" : "ACTIVE";
      var subscription =
          Subscription.create(
              cmd.workerId(), plan.getId(), initialStatus, now, now.plus(Duration.ofDays(30)));
      var saved = subscriptionRepository.save(subscription);
      if (plan.isPaid()) {
        paymentRepository.save(
            PaymentTransaction.createPending(
                saved.getId(), cmd.workerId(), plan.getMonthlyPrice(), "PEN"));
      }
      return Result.success(saved);
    } catch (RuleViolation ex) {
      return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
    }
  }

  @Override
  public Result<Subscription, ApplicationError> handle(CancelSubscriptionCommand cmd) {
    var subOpt = subscriptionRepository.findById(cmd.subscriptionId());
    if (subOpt.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError(
              "subscriptions.not_found", "Subscription not found"));
    }
    var sub = subOpt.get();
    if (!sub.getWorkerId().equals(cmd.actorId())) {
      return Result.failure(
          new ApplicationError.ForbiddenError("Only subscription owner can cancel it"));
    }
    try {
      sub.cancel();
      var saved = subscriptionRepository.save(sub);
      for (var member : memberRepository.findBySubscriptionId(sub.getId())) {
        if ("PENDING".equals(member.getStatus()) || "ACCEPTED".equals(member.getStatus())) {
          member.revoke();
          memberRepository.save(member);
        }
      }
      return Result.success(saved);
    } catch (RuleViolation ex) {
      return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
    }
  }

  @Override
  public Result<PaymentTransaction, ApplicationError> handle(RequestPaymentCommand cmd) {
    var subOpt = subscriptionRepository.findById(cmd.subscriptionId());
    if (subOpt.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError(
              "subscriptions.not_found", "Subscription not found"));
    }
    var sub = subOpt.get();
    if (!sub.getWorkerId().equals(cmd.actorId())) {
      return Result.failure(
          new ApplicationError.ForbiddenError("Only subscription owner can request payment"));
    }
    if (!"PENDING_PAYMENT".equals(sub.getStatus())) {
      return Result.failure(
          new ApplicationError.BusinessRuleError(
              "subscriptions.payment.not_pending_subscription",
              "Payments can only be created for subscriptions awaiting payment"));
    }
    boolean existingPending =
        paymentRepository.findBySubscriptionId(sub.getId()).stream()
            .anyMatch(p -> "PENDING".equals(p.getStatus()));
    if (existingPending) {
      return Result.failure(
          new ApplicationError.ConflictError(
              "subscriptions.payment.already_pending",
              "A pending payment transaction already exists for this subscription"));
    }
    var plan = planRepository.findById(sub.getPlanId()).orElse(null);
    if (plan == null || !plan.isPaid()) {
      return Result.failure(
          new ApplicationError.BusinessRuleError("Selected plan does not require payment"));
    }
    var tx =
        PaymentTransaction.createPending(
            sub.getId(), cmd.actorId(), plan.getMonthlyPrice(), "PEN");
    return Result.success(paymentRepository.save(tx));
  }

  @Override
  public Result<PaymentTransaction, ApplicationError> handle(ConfirmOfflinePaymentCommand cmd) {
    var txOpt = paymentRepository.findById(cmd.paymentId());
    if (txOpt.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError(
              "subscriptions.payment.not_found", "Payment transaction not found"));
    }
    var tx = txOpt.get();
    String normalizedRef = cmd.reference() == null ? "" : cmd.reference().trim();
    boolean duplicateRef =
        paymentRepository.findAll().stream()
            .anyMatch(
                other ->
                    !other.getId().equals(tx.getId())
                        && other.getReference().equalsIgnoreCase(normalizedRef));
    if (duplicateRef) {
      return Result.failure(
          new ApplicationError.ConflictError(
              "subscriptions.payment.duplicate_reference",
              "Payment receipt reference has already been used"));
    }

    try {
      tx.confirmOffline(normalizedRef);
      var savedTx = paymentRepository.save(tx);

      var sub =
          subscriptionRepository
              .findById(tx.getSubscriptionId())
              .orElseThrow();
      Instant now = clock.instant();
      for (var existingSub : subscriptionRepository.findByWorkerId(sub.getWorkerId())) {
        if (!existingSub.getId().equals(sub.getId()) && existingSub.isActiveAt(now)) {
          existingSub.cancel();
          subscriptionRepository.save(existingSub);
        }
      }
      sub.activateFrom(now);
      subscriptionRepository.save(sub);

      issueFirstPaymentReferralReward(sub.getWorkerId());
      return Result.success(savedTx);
    } catch (RuleViolation ex) {
      return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
    } catch (DataIntegrityViolationException ex) {
      return Result.failure(
          new ApplicationError.ConflictError(
              "subscriptions.payment.duplicate_reference",
              "Payment receipt reference has already been used"));
    }
  }

  private void issueFirstPaymentReferralReward(UUID referredWorkerId) {
    for (var use : referralUseRepository.findByReferredWorkerId(referredWorkerId)) {
      if (referralRewardRepository.findByReferralUseId(use.getId()).isEmpty()) {
        referralCodeRepository
            .findById(use.getReferralId())
            .ifPresent(
                code ->
                    referralRewardRepository.save(
                        ReferralReward.createAvailable(
                            code.getWorkerId(), use.getId(), REFERRAL_REWARD_DAYS)));
      }
    }
  }

  @Override
  public Result<PaymentTransaction, ApplicationError> handle(RejectPaymentCommand cmd) {
    var txOpt = paymentRepository.findById(cmd.paymentId());
    if (txOpt.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError(
              "subscriptions.payment.not_found", "Payment transaction not found"));
    }
    try {
      var tx = txOpt.get();
      tx.reject();
      return Result.success(paymentRepository.save(tx));
    } catch (RuleViolation ex) {
      return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
    }
  }

  @Override
  public Result<CollectiveBenefit, ApplicationError> handle(CreateBenefitCommand cmd) {
    String planCode = cmd.planCode().trim().toUpperCase(Locale.ROOT);
    if (planRepository.findByCode(planCode).isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError(
              "subscriptions.plan.not_found", "Referenced plan code does not exist"));
    }
    try {
      var benefit = CollectiveBenefit.create(cmd.name(), cmd.description(), planCode, true);
      return Result.success(benefitRepository.save(benefit));
    } catch (RuleViolation ex) {
      return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
    }
  }

  @Override
  public Result<BenefitClaim, ApplicationError> handle(ClaimBenefitCommand cmd) {
    var benefitOpt = benefitRepository.findById(cmd.benefitId());
    if (benefitOpt.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError(
              "subscriptions.benefit.not_found", "Collective benefit not found"));
    }
    var benefit = benefitOpt.get();
    if (!benefit.isActive()) {
      return Result.failure(
          new ApplicationError.BusinessRuleError("Collective benefit is no longer active"));
    }
    if (!entitlements.hasEligiblePlan(cmd.workerId(), benefit.getPlanCode())) {
      return Result.failure(
          new ApplicationError.BusinessRuleError(
              "subscriptions.benefit.plan_required",
              "Active plan does not qualify for this benefit"));
    }
    boolean alreadyClaimed =
        claimRepository.findByWorkerId(cmd.workerId()).stream()
            .anyMatch(c -> c.getBenefitId().equals(benefit.getId()));
    if (alreadyClaimed) {
      return Result.failure(
          new ApplicationError.ConflictError(
              "subscriptions.benefit.already_claimed",
              "Worker has already claimed this collective benefit"));
    }
    String voucherCode = "NOX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
    return Result.success(
        claimRepository.save(BenefitClaim.create(cmd.workerId(), benefit.getId(), voucherCode)));
  }

  @Override
  public Result<ReferralCode, ApplicationError> handle(GetOrCreateReferralCodeCommand cmd) {
    var existing = referralCodeRepository.findByWorkerId(cmd.workerId());
    if (!existing.isEmpty()) {
      return Result.success(existing.get(0));
    }
    String generated =
        "NOX-" + cmd.workerId().toString().replace("-", "").substring(0, 8).toUpperCase(Locale.ROOT);
    return Result.success(referralCodeRepository.save(ReferralCode.create(cmd.workerId(), generated)));
  }

  @Override
  public Result<ReferralUse, ApplicationError> handle(UseReferralCodeCommand cmd) {
    String normalized = cmd.code() == null ? "" : cmd.code().trim().toUpperCase(Locale.ROOT);
    var codes = referralCodeRepository.findByCode(normalized);
    if (codes.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError(
              "subscriptions.referral.not_found", "Referral code not found"));
    }
    var code = codes.get(0);
    if (code.getWorkerId().equals(cmd.workerId())) {
      return Result.failure(
          new ApplicationError.BusinessRuleError(
              "subscriptions.referral.self_use", "Cannot redeem your own referral code"));
    }
    if (!referralUseRepository.findByReferredWorkerId(cmd.workerId()).isEmpty()) {
      return Result.failure(
          new ApplicationError.ConflictError(
              "subscriptions.referral.already_used",
              "A referral code has already been applied to this account"));
    }
    return Result.success(
        referralUseRepository.save(ReferralUse.create(code.getId(), cmd.workerId())));
  }

  @Override
  public Result<ReferralReward, ApplicationError> handle(RedeemReferralRewardCommand cmd) {
    var rewardOpt = referralRewardRepository.findById(cmd.rewardId());
    if (rewardOpt.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError(
              "subscriptions.reward.not_found", "Referral reward not found"));
    }
    var reward = rewardOpt.get();
    if (!reward.getWorkerId().equals(cmd.workerId())) {
      return Result.failure(
          new ApplicationError.ForbiddenError("Only the reward owner can redeem it"));
    }
    try {
      reward.redeem();
      Instant now = clock.instant();
      var activeOwned =
          subscriptionRepository.findByWorkerId(cmd.workerId()).stream()
              .filter(s -> s.isActiveAt(now))
              .findFirst();
      if (activeOwned.isPresent()) {
        var sub = activeOwned.get();
        sub.extendDays(reward.getDays());
        subscriptionRepository.save(sub);
      } else {
        var proPlan =
            planRepository.findByCode("SENTINEL_PRO").stream()
                .findFirst()
                .orElseGet(() -> planRepository.findAll().stream().findFirst().orElse(null));
        if (proPlan != null) {
          subscriptionRepository.save(
              Subscription.create(
                  cmd.workerId(),
                  proPlan.getId(),
                  "ACTIVE",
                  now,
                  now.plus(Duration.ofDays(reward.getDays()))));
        }
      }
      return Result.success(referralRewardRepository.save(reward));
    } catch (RuleViolation ex) {
      return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
    }
  }

  @Override
  public Result<SubscriptionMember, ApplicationError> handle(InviteSubscriptionMemberCommand cmd) {
    var subOpt = subscriptionRepository.findById(cmd.subscriptionId());
    if (subOpt.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError(
              "subscriptions.not_found", "Subscription not found"));
    }
    var sub = subOpt.get();
    if (!sub.getWorkerId().equals(cmd.actorId())) {
      return Result.failure(
          new ApplicationError.ForbiddenError("Only subscription owner can invite members"));
    }
    if (!sub.isActiveAt(clock.instant())) {
      return Result.failure(
          new ApplicationError.BusinessRuleError(
              "subscriptions.inactive_invite", "Only active subscriptions can invite members"));
    }
    var plan = planRepository.findById(sub.getPlanId()).orElse(null);
    if (plan == null || plan.getMaxWorkers() <= 1) {
      return Result.failure(
          new ApplicationError.BusinessRuleError(
              "subscriptions.plan.no_family_seats",
              "Current plan does not support additional crew members"));
    }

    long occupiedSeats =
        1
            + memberRepository.findBySubscriptionId(sub.getId()).stream()
                .filter(m -> "ACCEPTED".equals(m.getStatus()) || "PENDING".equals(m.getStatus()))
                .count();
    if (occupiedSeats >= plan.getMaxWorkers()) {
      return Result.failure(
          new ApplicationError.BusinessRuleError(
              "subscriptions.group.full", "Subscription worker seat limit has been reached"));
    }

    var targetOpt = profileDirectory.findByEmail(cmd.workerEmail().trim().toLowerCase(Locale.ROOT));
    if (targetOpt.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError(
              "profiles.account.not_found", "Target worker email was not found"));
    }
    var target = targetOpt.get();
    if (target.id().equals(sub.getWorkerId())) {
      return Result.failure(
          new ApplicationError.BusinessRuleError("Cannot invite the subscription owner"));
    }

    boolean alreadyMemberOrPending =
        memberRepository.findBySubscriptionId(sub.getId()).stream()
            .anyMatch(
                m ->
                    m.getWorkerId().equals(target.id())
                        && ("PENDING".equals(m.getStatus()) || "ACCEPTED".equals(m.getStatus())));
    if (alreadyMemberOrPending) {
      return Result.failure(
          new ApplicationError.ConflictError(
              "subscriptions.member.exists", "Worker is already invited or a member"));
    }

    return Result.success(
        memberRepository.save(SubscriptionMember.invite(sub.getId(), target.id())));
  }

  @Override
  public Result<SubscriptionMember, ApplicationError> handle(RespondInvitationCommand cmd) {
    var memberOpt = memberRepository.findById(cmd.memberId());
    if (memberOpt.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError(
              "subscriptions.invitation.not_found", "Subscription invitation not found"));
    }
    var member = memberOpt.get();
    var subOpt = subscriptionRepository.findById(member.getSubscriptionId());
    if (subOpt.isEmpty() || !subOpt.get().isActiveAt(clock.instant())) {
      return Result.failure(
          new ApplicationError.BusinessRuleError(
              "subscriptions.invitation.inactive_subscription",
              "Parent subscription is no longer active"));
    }
    try {
      if (cmd.accept()) {
        member.accept(cmd.actorId());
      } else {
        member.reject(cmd.actorId());
      }
      return Result.success(memberRepository.save(member));
    } catch (RuleViolation ex) {
      return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
    }
  }

  @Override
  public Result<Void, ApplicationError> handle(RevokeSubscriptionMemberCommand cmd) {
    var memberOpt = memberRepository.findById(cmd.memberId());
    if (memberOpt.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError(
              "subscriptions.member.not_found", "Subscription member not found"));
    }
    var member = memberOpt.get();
    var sub = subscriptionRepository.findById(member.getSubscriptionId()).orElse(null);
    boolean isOwner = sub != null && sub.getWorkerId().equals(cmd.actorId());
    boolean isMember = member.getWorkerId().equals(cmd.actorId());
    if (!isOwner && !isMember) {
      return Result.failure(
          new ApplicationError.ForbiddenError("Only subscription owner or member can revoke seat"));
    }
    try {
      member.revoke();
      memberRepository.save(member);
      return Result.success(null);
    } catch (RuleViolation ex) {
      return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
    }
  }
}
