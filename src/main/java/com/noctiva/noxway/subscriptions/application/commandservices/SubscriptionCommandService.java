package com.noctiva.noxway.subscriptions.application.commandservices;

import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.application.result.Result;
import com.noctiva.noxway.subscriptions.application.commands.SubscriptionCommands.*;
import com.noctiva.noxway.subscriptions.domain.model.aggregates.*;

public interface SubscriptionCommandService {

  Result<Subscription, ApplicationError> handle(SubscribeCommand cmd);

  Result<Subscription, ApplicationError> handle(CancelSubscriptionCommand cmd);

  Result<PaymentTransaction, ApplicationError> handle(RequestPaymentCommand cmd);

  Result<PaymentTransaction, ApplicationError> handle(ConfirmOfflinePaymentCommand cmd);

  Result<PaymentTransaction, ApplicationError> handle(RejectPaymentCommand cmd);

  Result<CollectiveBenefit, ApplicationError> handle(CreateBenefitCommand cmd);

  Result<BenefitClaim, ApplicationError> handle(ClaimBenefitCommand cmd);

  Result<ReferralCode, ApplicationError> handle(GetOrCreateReferralCodeCommand cmd);

  Result<ReferralUse, ApplicationError> handle(UseReferralCodeCommand cmd);

  Result<ReferralReward, ApplicationError> handle(RedeemReferralRewardCommand cmd);

  Result<SubscriptionMember, ApplicationError> handle(InviteSubscriptionMemberCommand cmd);

  Result<SubscriptionMember, ApplicationError> handle(RespondInvitationCommand cmd);

  Result<Void, ApplicationError> handle(RevokeSubscriptionMemberCommand cmd);
}
