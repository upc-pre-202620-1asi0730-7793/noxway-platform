package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.assemblers;

import com.noctiva.noxway.subscriptions.domain.model.aggregates.*;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.*;

public final class SubscriptionsPersistenceAssembler {
  private SubscriptionsPersistenceAssembler() {}

  public static Plan toDomain(PlanPersistenceEntity e) {
    return Plan.reconstitute(
        e.getId(),
        e.getCode(),
        e.getName(),
        e.getMonthlyPrice(),
        e.getMaxContacts(),
        e.getMaxWorkers(),
        e.isActive(),
        e.getCreatedAt(),
        e.getVersion());
  }

  public static PlanPersistenceEntity toPersistence(Plan p) {
    var e = new PlanPersistenceEntity();
    e.setId(p.getId());
    e.setCode(p.getCode());
    e.setName(p.getName());
    e.setMonthlyPrice(p.getMonthlyPrice());
    e.setMaxContacts(p.getMaxContacts());
    e.setMaxWorkers(p.getMaxWorkers());
    e.setActive(p.isActive());
    e.setCreatedAt(p.getCreatedAt());
    e.setVersion(p.getVersion());
    return e;
  }

  public static Subscription toDomain(SubscriptionPersistenceEntity e) {
    return Subscription.reconstitute(
        e.getId(),
        e.getWorkerId(),
        e.getPlanId(),
        e.getStatus(),
        e.getStartsAt(),
        e.getEndsAt(),
        e.getCreatedAt(),
        e.getVersion());
  }

  public static SubscriptionPersistenceEntity toPersistence(Subscription s) {
    var e = new SubscriptionPersistenceEntity();
    e.setId(s.getId());
    e.setWorkerId(s.getWorkerId());
    e.setPlanId(s.getPlanId());
    e.setStatus(s.getStatus());
    e.setStartsAt(s.getStartsAt());
    e.setEndsAt(s.getEndsAt());
    e.setCreatedAt(s.getCreatedAt());
    e.setVersion(s.getVersion());
    return e;
  }

  public static SubscriptionMember toDomain(SubscriptionMemberPersistenceEntity e) {
    return SubscriptionMember.reconstitute(
        e.getId(), e.getSubscriptionId(), e.getWorkerId(), e.getStatus(), e.getCreatedAt(), e.getVersion());
  }

  public static SubscriptionMemberPersistenceEntity toPersistence(SubscriptionMember m) {
    var e = new SubscriptionMemberPersistenceEntity();
    e.setId(m.getId());
    e.setSubscriptionId(m.getSubscriptionId());
    e.setWorkerId(m.getWorkerId());
    e.setStatus(m.getStatus());
    e.setCreatedAt(m.getCreatedAt());
    e.setVersion(m.getVersion());
    return e;
  }

  public static PaymentTransaction toDomain(PaymentTransactionPersistenceEntity e) {
    return PaymentTransaction.reconstitute(
        e.getId(),
        e.getSubscriptionId(),
        e.getWorkerId(),
        e.getAmount(),
        e.getCurrency(),
        e.getReference(),
        e.getStatus(),
        e.getCreatedAt(),
        e.getVersion());
  }

  public static PaymentTransactionPersistenceEntity toPersistence(PaymentTransaction p) {
    var e = new PaymentTransactionPersistenceEntity();
    e.setId(p.getId());
    e.setSubscriptionId(p.getSubscriptionId());
    e.setWorkerId(p.getWorkerId());
    e.setAmount(p.getAmount());
    e.setCurrency(p.getCurrency());
    e.setReference(p.getReference());
    e.setStatus(p.getStatus());
    e.setCreatedAt(p.getCreatedAt());
    e.setVersion(p.getVersion());
    return e;
  }

  public static CollectiveBenefit toDomain(CollectiveBenefitPersistenceEntity e) {
    return CollectiveBenefit.reconstitute(
        e.getId(), e.getName(), e.getDescription(), e.getPlanCode(), e.isActive(), e.getCreatedAt(), e.getVersion());
  }

  public static CollectiveBenefitPersistenceEntity toPersistence(CollectiveBenefit b) {
    var e = new CollectiveBenefitPersistenceEntity();
    e.setId(b.getId());
    e.setName(b.getName());
    e.setDescription(b.getDescription());
    e.setPlanCode(b.getPlanCode());
    e.setActive(b.isActive());
    e.setCreatedAt(b.getCreatedAt());
    e.setVersion(b.getVersion());
    return e;
  }

  public static BenefitClaim toDomain(BenefitClaimPersistenceEntity e) {
    return BenefitClaim.reconstitute(
        e.getId(), e.getWorkerId(), e.getBenefitId(), e.getCode(), e.getCreatedAt(), e.getVersion());
  }

  public static BenefitClaimPersistenceEntity toPersistence(BenefitClaim c) {
    var e = new BenefitClaimPersistenceEntity();
    e.setId(c.getId());
    e.setWorkerId(c.getWorkerId());
    e.setBenefitId(c.getBenefitId());
    e.setCode(c.getCode());
    e.setCreatedAt(c.getCreatedAt());
    e.setVersion(c.getVersion());
    return e;
  }

  public static ReferralCode toDomain(ReferralCodePersistenceEntity e) {
    return ReferralCode.reconstitute(
        e.getId(), e.getWorkerId(), e.getCode(), e.getCreatedAt(), e.getVersion());
  }

  public static ReferralCodePersistenceEntity toPersistence(ReferralCode c) {
    var e = new ReferralCodePersistenceEntity();
    e.setId(c.getId());
    e.setWorkerId(c.getWorkerId());
    e.setCode(c.getCode());
    e.setCreatedAt(c.getCreatedAt());
    e.setVersion(c.getVersion());
    return e;
  }

  public static ReferralUse toDomain(ReferralUsePersistenceEntity e) {
    return ReferralUse.reconstitute(
        e.getId(), e.getReferralId(), e.getReferredWorkerId(), e.getCreatedAt(), e.getVersion());
  }

  public static ReferralUsePersistenceEntity toPersistence(ReferralUse u) {
    var e = new ReferralUsePersistenceEntity();
    e.setId(u.getId());
    e.setReferralId(u.getReferralId());
    e.setReferredWorkerId(u.getReferredWorkerId());
    e.setCreatedAt(u.getCreatedAt());
    e.setVersion(u.getVersion());
    return e;
  }

  public static ReferralReward toDomain(ReferralRewardPersistenceEntity e) {
    return ReferralReward.reconstitute(
        e.getId(),
        e.getWorkerId(),
        e.getReferralUseId(),
        e.getDays(),
        e.getStatus(),
        e.getCreatedAt(),
        e.getVersion());
  }

  public static ReferralRewardPersistenceEntity toPersistence(ReferralReward r) {
    var e = new ReferralRewardPersistenceEntity();
    e.setId(r.getId());
    e.setWorkerId(r.getWorkerId());
    e.setReferralUseId(r.getReferralUseId());
    e.setDays(r.getDays());
    e.setStatus(r.getStatus());
    e.setCreatedAt(r.getCreatedAt());
    e.setVersion(r.getVersion());
    return e;
  }
}
