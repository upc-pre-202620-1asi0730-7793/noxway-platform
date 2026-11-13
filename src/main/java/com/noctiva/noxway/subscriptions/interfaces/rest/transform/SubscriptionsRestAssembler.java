package com.noctiva.noxway.subscriptions.interfaces.rest.transform;

import com.noctiva.noxway.shared.interfaces.rest.resources.PagedResource;
import com.noctiva.noxway.subscriptions.application.commands.SubscriptionCommands.*;
import com.noctiva.noxway.subscriptions.application.queryservices.SubscriptionQueryService.*;
import com.noctiva.noxway.subscriptions.domain.model.aggregates.*;
import com.noctiva.noxway.subscriptions.interfaces.rest.resources.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public final class SubscriptionsRestAssembler {
  private SubscriptionsRestAssembler() {}

  public static SubscribeCommand toCommand(UUID workerId, SubscribeResource r) {
    return new SubscribeCommand(workerId, r.planId());
  }

  public static ConfirmOfflinePaymentCommand toCommand(
      UUID paymentId, UUID actorId, ConfirmOfflinePaymentResource r) {
    return new ConfirmOfflinePaymentCommand(paymentId, r.reference(), actorId);
  }

  public static CreateBenefitCommand toCommand(CreateBenefitResource r) {
    return new CreateBenefitCommand(r.name(), r.description(), r.planCode());
  }

  public static UseReferralCodeCommand toCommand(UUID workerId, UseReferralCodeResource r) {
    return new UseReferralCodeCommand(workerId, r.code());
  }

  public static InviteSubscriptionMemberCommand toCommand(
      UUID subscriptionId, UUID actorId, InviteSubscriptionMemberResource r) {
    return new InviteSubscriptionMemberCommand(subscriptionId, actorId, r.workerEmail());
  }

  public static PlanResource toResource(Plan p) {
    return new PlanResource(
        p.getId(),
        p.getCode(),
        p.getName(),
        p.getMonthlyPrice(),
        p.getMaxContacts(),
        p.getMaxWorkers(),
        p.isActive(),
        p.getCreatedAt());
  }

  public static List<PlanResource> toPlanResources(List<Plan> plans) {
    return plans.stream().map(SubscriptionsRestAssembler::toResource).toList();
  }

  public static SubscriptionResource toResource(Subscription s) {
    return new SubscriptionResource(
        s.getId(),
        s.getWorkerId(),
        s.getPlanId(),
        "",
        "",
        BigDecimal.ZERO,
        1,
        1,
        s.getStatus(),
        true,
        s.getStartsAt(),
        s.getEndsAt(),
        s.getCreatedAt());
  }

  public static SubscriptionResource toResource(SubscriptionView v) {
    return new SubscriptionResource(
        v.id(),
        v.workerId(),
        v.planId(),
        v.planCode(),
        v.planName(),
        v.monthlyPrice(),
        v.maxContacts(),
        v.maxWorkers(),
        v.status(),
        v.owner(),
        v.startsAt(),
        v.endsAt(),
        v.createdAt());
  }

  public static List<SubscriptionResource> toSubscriptionResources(List<SubscriptionView> list) {
    return list.stream().map(SubscriptionsRestAssembler::toResource).toList();
  }

  public static PaymentTransactionResource toResource(PaymentTransaction p) {
    return new PaymentTransactionResource(
        p.getId(),
        p.getSubscriptionId(),
        p.getWorkerId(),
        "",
        "",
        p.getAmount(),
        p.getCurrency(),
        p.getReference(),
        p.getStatus(),
        p.getCreatedAt());
  }

  public static PaymentTransactionResource toResource(PaymentView p) {
    return new PaymentTransactionResource(
        p.id(),
        p.subscriptionId(),
        p.workerId(),
        p.workerName(),
        p.planCode(),
        p.amount(),
        p.currency(),
        p.reference(),
        p.status(),
        p.createdAt());
  }

  public static PagedResource<PaymentTransactionResource> toPagedPaymentResources(
      PagedResource<PaymentView> page) {
    return new PagedResource<>(
        page.items().stream().map(SubscriptionsRestAssembler::toResource).toList(),
        page.page(),
        page.size(),
        page.total());
  }

  public static CollectiveBenefitResource toResource(CollectiveBenefit b) {
    return new CollectiveBenefitResource(
        b.getId(), b.getName(), b.getDescription(), b.getPlanCode(), b.isActive(), b.getCreatedAt());
  }

  public static List<CollectiveBenefitResource> toBenefitResources(List<CollectiveBenefit> list) {
    return list.stream().map(SubscriptionsRestAssembler::toResource).toList();
  }

  public static BenefitClaimResource toResource(BenefitClaim c) {
    return new BenefitClaimResource(
        c.getId(), c.getWorkerId(), c.getBenefitId(), "", "", "", c.getCode(), c.getCreatedAt());
  }

  public static BenefitClaimResource toResource(BenefitClaimView v) {
    return new BenefitClaimResource(
        v.id(),
        v.workerId(),
        v.benefitId(),
        v.benefitName(),
        v.benefitDescription(),
        v.planCode(),
        v.code(),
        v.createdAt());
  }

  public static List<BenefitClaimResource> toBenefitClaimResources(List<BenefitClaimView> list) {
    return list.stream().map(SubscriptionsRestAssembler::toResource).toList();
  }

  public static ReferralCodeResource toResource(ReferralCode c) {
    return new ReferralCodeResource(c.getId(), c.getWorkerId(), c.getCode(), c.getCreatedAt());
  }

  public static ReferralUseResource toResource(ReferralUse u) {
    return new ReferralUseResource(
        u.getId(), u.getReferralId(), u.getReferredWorkerId(), u.getCreatedAt());
  }

  public static ReferralRewardResource toResource(ReferralReward r) {
    return new ReferralRewardResource(
        r.getId(), r.getWorkerId(), r.getReferralUseId(), r.getDays(), r.getStatus(), r.getCreatedAt());
  }

  public static List<ReferralRewardResource> toRewardResources(List<ReferralReward> list) {
    return list.stream().map(SubscriptionsRestAssembler::toResource).toList();
  }

  public static SubscriptionMemberResource toResource(SubscriptionMember m) {
    return new SubscriptionMemberResource(
        m.getId(), m.getSubscriptionId(), m.getWorkerId(), "", "", m.getStatus(), m.getCreatedAt());
  }

  public static SubscriptionMemberResource toResource(SubscriptionMemberView v) {
    return new SubscriptionMemberResource(
        v.id(),
        v.subscriptionId(),
        v.workerId(),
        v.workerName(),
        v.workerEmail(),
        v.status(),
        v.createdAt());
  }

  public static List<SubscriptionMemberResource> toMemberResources(
      List<SubscriptionMemberView> list) {
    return list.stream().map(SubscriptionsRestAssembler::toResource).toList();
  }

  public static List<SubscriptionInvitationResource> toInvitationResources(
      List<InvitationView> list) {
    return list.stream()
        .map(
            v ->
                new SubscriptionInvitationResource(
                    v.id(),
                    v.subscriptionId(),
                    v.workerId(),
                    v.planCode(),
                    v.planName(),
                    v.ownerName(),
                    v.subscriptionStatus(),
                    v.status(),
                    v.createdAt()))
        .toList();
  }
}
