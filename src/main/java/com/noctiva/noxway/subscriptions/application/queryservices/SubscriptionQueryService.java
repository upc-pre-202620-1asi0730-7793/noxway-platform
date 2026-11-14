package com.noctiva.noxway.subscriptions.application.queryservices;

import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.application.result.Result;
import com.noctiva.noxway.shared.interfaces.rest.resources.PagedResource;
import com.noctiva.noxway.subscriptions.application.queries.SubscriptionQueries.*;
import com.noctiva.noxway.subscriptions.domain.model.aggregates.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface SubscriptionQueryService {

  Result<List<Plan>, ApplicationError> handle(GetPlansQuery query);

  Result<List<SubscriptionView>, ApplicationError> handle(GetWorkerSubscriptionsQuery query);

  Result<PagedResource<PaymentView>, ApplicationError> handle(GetPaymentsQuery query);

  Result<List<CollectiveBenefit>, ApplicationError> handle(GetBenefitsQuery query);

  Result<List<BenefitClaimView>, ApplicationError> handle(GetBenefitClaimsQuery query);

  Result<List<ReferralReward>, ApplicationError> handle(GetReferralRewardsQuery query);

  Result<List<SubscriptionMemberView>, ApplicationError> handle(
      GetSubscriptionMembersQuery query);

  Result<List<InvitationView>, ApplicationError> handle(GetWorkerInvitationsQuery query);

  record SubscriptionView(
      UUID id,
      UUID workerId,
      UUID planId,
      String planCode,
      String planName,
      BigDecimal monthlyPrice,
      int maxContacts,
      int maxWorkers,
      String status,
      boolean owner,
      Instant startsAt,
      Instant endsAt,
      Instant createdAt) {}

  record PaymentView(
      UUID id,
      UUID subscriptionId,
      UUID workerId,
      String workerName,
      String planCode,
      BigDecimal amount,
      String currency,
      String reference,
      String status,
      Instant createdAt) {}

  record BenefitClaimView(
      UUID id,
      UUID workerId,
      UUID benefitId,
      String benefitName,
      String benefitDescription,
      String planCode,
      String code,
      Instant createdAt) {}

  record SubscriptionMemberView(
      UUID id,
      UUID subscriptionId,
      UUID workerId,
      String workerName,
      String workerEmail,
      String status,
      Instant createdAt) {}

  record InvitationView(
      UUID id,
      UUID subscriptionId,
      UUID workerId,
      String planCode,
      String planName,
      String ownerName,
      String subscriptionStatus,
      String status,
      Instant createdAt) {}
}
