package com.noctiva.noxway.subscriptions.application.queries;

import java.util.UUID;

public final class SubscriptionQueries {
  private SubscriptionQueries() {}

  public record GetPlansQuery() {}

  public record GetWorkerSubscriptionsQuery(UUID workerId) {}

  public record GetPaymentsQuery(UUID actorId, boolean includeAll, int page, int size) {}

  public record GetBenefitsQuery() {}

  public record GetBenefitClaimsQuery(UUID workerId) {}

  public record GetReferralRewardsQuery(UUID workerId) {}

  public record GetSubscriptionMembersQuery(UUID subscriptionId, UUID actorId) {}

  public record GetWorkerInvitationsQuery(UUID workerId) {}
}
