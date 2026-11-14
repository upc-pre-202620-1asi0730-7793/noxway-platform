package com.noctiva.noxway.subscriptions.application.commands;

import java.util.UUID;

public final class SubscriptionCommands {
  private SubscriptionCommands() {}

  public record SubscribeCommand(UUID workerId, UUID planId) {}

  public record CancelSubscriptionCommand(UUID subscriptionId, UUID actorId) {}

  public record RequestPaymentCommand(UUID subscriptionId, UUID actorId) {}

  public record ConfirmOfflinePaymentCommand(UUID paymentId, String reference, UUID actorId) {}

  public record RejectPaymentCommand(UUID paymentId, UUID actorId) {}

  public record CreateBenefitCommand(String name, String description, String planCode) {}

  public record ClaimBenefitCommand(UUID benefitId, UUID workerId) {}

  public record GetOrCreateReferralCodeCommand(UUID workerId) {}

  public record UseReferralCodeCommand(UUID workerId, String code) {}

  public record RedeemReferralRewardCommand(UUID rewardId, UUID workerId) {}

  public record InviteSubscriptionMemberCommand(
      UUID subscriptionId, UUID actorId, String workerEmail) {}

  public record RespondInvitationCommand(UUID memberId, UUID actorId, boolean accept) {}

  public record RevokeSubscriptionMemberCommand(UUID memberId, UUID actorId) {}
}
