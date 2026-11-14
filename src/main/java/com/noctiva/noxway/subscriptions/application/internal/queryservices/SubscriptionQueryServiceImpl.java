package com.noctiva.noxway.subscriptions.application.internal.queryservices;

import com.noctiva.noxway.profiles.interfaces.acl.ProfileDirectoryFacade;
import com.noctiva.noxway.profiles.interfaces.acl.ProfileDirectoryFacade.AccountSummary;
import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.application.result.Result;
import com.noctiva.noxway.shared.interfaces.rest.Pages;
import com.noctiva.noxway.shared.interfaces.rest.resources.PagedResource;
import com.noctiva.noxway.subscriptions.application.queries.SubscriptionQueries.*;
import com.noctiva.noxway.subscriptions.application.queryservices.SubscriptionQueryService;
import com.noctiva.noxway.subscriptions.domain.model.aggregates.*;
import com.noctiva.noxway.subscriptions.domain.repositories.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SubscriptionQueryServiceImpl implements SubscriptionQueryService {

  private final PlanRepository planRepository;
  private final SubscriptionRepository subscriptionRepository;
  private final SubscriptionMemberRepository memberRepository;
  private final PaymentTransactionRepository paymentRepository;
  private final CollectiveBenefitRepository benefitRepository;
  private final BenefitClaimRepository claimRepository;
  private final ReferralRewardRepository rewardRepository;
  private final ProfileDirectoryFacade profileDirectory;

  public SubscriptionQueryServiceImpl(
      PlanRepository planRepository,
      SubscriptionRepository subscriptionRepository,
      SubscriptionMemberRepository memberRepository,
      PaymentTransactionRepository paymentRepository,
      CollectiveBenefitRepository benefitRepository,
      BenefitClaimRepository claimRepository,
      ReferralRewardRepository rewardRepository,
      ProfileDirectoryFacade profileDirectory) {
    this.planRepository = planRepository;
    this.subscriptionRepository = subscriptionRepository;
    this.memberRepository = memberRepository;
    this.paymentRepository = paymentRepository;
    this.benefitRepository = benefitRepository;
    this.claimRepository = claimRepository;
    this.rewardRepository = rewardRepository;
    this.profileDirectory = profileDirectory;
  }

  @Override
  public Result<List<Plan>, ApplicationError> handle(GetPlansQuery query) {
    var plans =
        planRepository.findAll().stream()
            .filter(Plan::isActive)
            .sorted(Comparator.comparing(Plan::getMonthlyPrice))
            .toList();
    return Result.success(plans);
  }

  @Override
  public Result<List<SubscriptionView>, ApplicationError> handle(
      GetWorkerSubscriptionsQuery query) {
    Map<UUID, Plan> plans =
        planRepository.findAll().stream().collect(Collectors.toMap(Plan::getId, p -> p));
    var items = new ArrayList<SubscriptionView>();
    for (var s : subscriptionRepository.findByWorkerId(query.workerId())) {
      items.add(toSubscriptionView(s, plans.get(s.getPlanId()), true));
    }
    for (var m : memberRepository.findByWorkerId(query.workerId())) {
      if ("ACCEPTED".equals(m.getStatus())) {
        subscriptionRepository
            .findById(m.getSubscriptionId())
            .ifPresent(s -> items.add(toSubscriptionView(s, plans.get(s.getPlanId()), false)));
      }
    }
    items.sort(Comparator.comparing(SubscriptionView::createdAt).reversed());
    return Result.success(items);
  }

  @Override
  public Result<PagedResource<PaymentView>, ApplicationError> handle(GetPaymentsQuery query) {
    Map<UUID, Subscription> subMap =
        subscriptionRepository.findAll().stream()
            .collect(Collectors.toMap(Subscription::getId, s -> s));
    Map<UUID, Plan> planMap =
        planRepository.findAll().stream().collect(Collectors.toMap(Plan::getId, p -> p));

    var raw =
        query.includeAll()
            ? paymentRepository.findAll()
            : paymentRepository.findByWorkerId(query.actorId());
    var views =
        raw.stream()
            .sorted(Comparator.comparing(PaymentTransaction::getCreatedAt).reversed())
            .map(
                p -> {
                  String workerName =
                      profileDirectory
                          .findById(p.getWorkerId())
                          .map(AccountSummary::fullName)
                          .orElse(p.getWorkerId().toString());
                  var sub = subMap.get(p.getSubscriptionId());
                  String planCode =
                      sub != null && planMap.containsKey(sub.getPlanId())
                          ? planMap.get(sub.getPlanId()).getCode()
                          : "";
                  return new PaymentView(
                      p.getId(),
                      p.getSubscriptionId(),
                      p.getWorkerId(),
                      workerName,
                      planCode,
                      p.getAmount(),
                      p.getCurrency(),
                      p.getReference(),
                      p.getStatus(),
                      p.getCreatedAt());
                })
            .toList();

    int page = Math.max(0, query.page());
    int size = Pages.clamp(query.size());
    int from = Math.min(page * size, views.size());
    int to = Math.min(from + size, views.size());
    return Result.success(new PagedResource<>(views.subList(from, to), page, size, views.size()));
  }

  @Override
  public Result<List<CollectiveBenefit>, ApplicationError> handle(GetBenefitsQuery query) {
    var benefits =
        benefitRepository.findAll().stream()
            .filter(CollectiveBenefit::isActive)
            .sorted(Comparator.comparing(CollectiveBenefit::getName))
            .toList();
    return Result.success(benefits);
  }

  @Override
  public Result<List<BenefitClaimView>, ApplicationError> handle(GetBenefitClaimsQuery query) {
    Map<UUID, CollectiveBenefit> benefits =
        benefitRepository.findAll().stream()
            .collect(Collectors.toMap(CollectiveBenefit::getId, b -> b));
    var items =
        claimRepository.findByWorkerId(query.workerId()).stream()
            .sorted(Comparator.comparing(BenefitClaim::getCreatedAt).reversed())
            .map(
                c -> {
                  var b = benefits.get(c.getBenefitId());
                  return new BenefitClaimView(
                      c.getId(),
                      c.getWorkerId(),
                      c.getBenefitId(),
                      b != null ? b.getName() : "",
                      b != null ? b.getDescription() : "",
                      b != null ? b.getPlanCode() : "",
                      c.getCode(),
                      c.getCreatedAt());
                })
            .toList();
    return Result.success(items);
  }

  @Override
  public Result<List<ReferralReward>, ApplicationError> handle(GetReferralRewardsQuery query) {
    var rewards =
        rewardRepository.findByWorkerId(query.workerId()).stream()
            .sorted(Comparator.comparing(ReferralReward::getCreatedAt).reversed())
            .toList();
    return Result.success(rewards);
  }

  @Override
  public Result<List<SubscriptionMemberView>, ApplicationError> handle(
      GetSubscriptionMembersQuery query) {
    var subOpt = subscriptionRepository.findById(query.subscriptionId());
    if (subOpt.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError(
              "subscriptions.not_found", "Subscription not found"));
    }
    var members =
        memberRepository.findBySubscriptionId(query.subscriptionId()).stream()
            .sorted(Comparator.comparing(SubscriptionMember::getCreatedAt).reversed())
            .map(
                m -> {
                  var profile = profileDirectory.findById(m.getWorkerId()).orElse(null);
                  return new SubscriptionMemberView(
                      m.getId(),
                      m.getSubscriptionId(),
                      m.getWorkerId(),
                      profile != null ? profile.fullName() : "",
                      profile != null ? profile.email() : "",
                      m.getStatus(),
                      m.getCreatedAt());
                })
            .toList();
    return Result.success(members);
  }

  @Override
  public Result<List<InvitationView>, ApplicationError> handle(GetWorkerInvitationsQuery query) {
    var list =
        memberRepository.findByWorkerId(query.workerId()).stream()
            .sorted(Comparator.comparing(SubscriptionMember::getCreatedAt).reversed())
            .map(
                m -> {
                  var sub = subscriptionRepository.findById(m.getSubscriptionId()).orElse(null);
                  var plan =
                      sub != null ? planRepository.findById(sub.getPlanId()).orElse(null) : null;
                  String ownerName =
                      sub != null
                          ? profileDirectory
                              .findById(sub.getWorkerId())
                              .map(AccountSummary::fullName)
                              .orElse("")
                          : "";
                  return new InvitationView(
                      m.getId(),
                      m.getSubscriptionId(),
                      m.getWorkerId(),
                      plan != null ? plan.getCode() : "",
                      plan != null ? plan.getName() : "",
                      ownerName,
                      sub != null ? sub.getStatus() : "",
                      m.getStatus(),
                      m.getCreatedAt());
                })
            .toList();
    return Result.success(list);
  }

  private static SubscriptionView toSubscriptionView(
      Subscription s, Plan plan, boolean owner) {
    return new SubscriptionView(
        s.getId(),
        s.getWorkerId(),
        s.getPlanId(),
        plan != null ? plan.getCode() : "",
        plan != null ? plan.getName() : "",
        plan != null ? plan.getMonthlyPrice() : BigDecimal.ZERO,
        plan != null ? plan.getMaxContacts() : 1,
        plan != null ? plan.getMaxWorkers() : 1,
        s.getStatus(),
        owner,
        s.getStartsAt(),
        s.getEndsAt(),
        s.getCreatedAt());
  }
}
