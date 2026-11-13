package com.noctiva.noxway.subscriptions.interfaces.rest;

import com.noctiva.noxway.iam.interfaces.acl.IamContextFacade;
import com.noctiva.noxway.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.noctiva.noxway.subscriptions.application.commands.SubscriptionCommands.*;
import com.noctiva.noxway.subscriptions.application.commandservices.SubscriptionCommandService;
import com.noctiva.noxway.subscriptions.application.queries.SubscriptionQueries.*;
import com.noctiva.noxway.subscriptions.application.queryservices.SubscriptionQueryService;
import com.noctiva.noxway.subscriptions.interfaces.rest.resources.*;
import com.noctiva.noxway.subscriptions.interfaces.rest.transform.SubscriptionsRestAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.Locale;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Tag(
    name = "Subscriptions & Benefits",
    description = "Plans, subscriptions, offline payments, crew sharing, benefits, and referrals")
public class SubscriptionsController {

  private final SubscriptionCommandService commandService;
  private final SubscriptionQueryService queryService;
  private final IamContextFacade iamFacade;
  private final ResponseEntityAssembler responseAssembler;

  public SubscriptionsController(
      SubscriptionCommandService commandService,
      SubscriptionQueryService queryService,
      IamContextFacade iamFacade,
      ResponseEntityAssembler responseAssembler) {
    this.commandService = commandService;
    this.queryService = queryService;
    this.iamFacade = iamFacade;
    this.responseAssembler = responseAssembler;
  }

  @GetMapping("/plans")
  @Operation(summary = "List active subscription plans")
  public ResponseEntity<?> plans(HttpServletRequest request, Locale locale) {
    return responseAssembler.toOk(
        queryService.handle(new GetPlansQuery()),
        SubscriptionsRestAssembler::toPlanResources,
        request,
        locale);
  }

  @PostMapping("/subscriptions")
  @Operation(summary = "Subscribe worker to a plan")
  public ResponseEntity<?> subscribe(
      @Valid @RequestBody SubscribeResource body, HttpServletRequest request, Locale locale) {
    UUID workerId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toCreated(
        commandService.handle(SubscriptionsRestAssembler.toCommand(workerId, body)),
        SubscriptionsRestAssembler::toResource,
        s -> URI.create("/api/v1/subscriptions/" + s.getId()),
        request,
        locale);
  }

  @GetMapping("/subscriptions")
  @Operation(summary = "List worker subscriptions (owned and accepted family memberships)")
  public ResponseEntity<?> mine(HttpServletRequest request, Locale locale) {
    UUID workerId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toOk(
        queryService.handle(new GetWorkerSubscriptionsQuery(workerId)),
        SubscriptionsRestAssembler::toSubscriptionResources,
        request,
        locale);
  }

  @PostMapping("/subscriptions/{id}/cancel")
  @Operation(summary = "Cancel a worker subscription")
  public ResponseEntity<?> cancel(
      @PathVariable UUID id, HttpServletRequest request, Locale locale) {
    UUID actorId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toOk(
        commandService.handle(new CancelSubscriptionCommand(id, actorId)),
        SubscriptionsRestAssembler::toResource,
        request,
        locale);
  }

  @PostMapping("/subscriptions/{id}/payments")
  @Operation(summary = "Create pending offline payment transaction for subscription")
  public ResponseEntity<?> requestPayment(
      @PathVariable UUID id, HttpServletRequest request, Locale locale) {
    UUID actorId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toCreated(
        commandService.handle(new RequestPaymentCommand(id, actorId)),
        SubscriptionsRestAssembler::toResource,
        p -> URI.create("/api/v1/payments/" + p.getId()),
        request,
        locale);
  }

  @GetMapping("/payments")
  @Operation(summary = "List payment transactions (worker own or all when requested/staff)")
  public ResponseEntity<?> payments(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "false") boolean all,
      HttpServletRequest request,
      Locale locale) {
    var actor = iamFacade.resolveCurrentActor();
    return responseAssembler.toOk(
        queryService.handle(new GetPaymentsQuery(actor.id(), all || actor.isStaff(), page, size)),
        SubscriptionsRestAssembler::toPagedPaymentResources,
        request,
        locale);
  }

  @PostMapping("/payments/{id}/confirm-offline")
  @Operation(summary = "Confirm an offline payment with unique receipt reference")
  public ResponseEntity<?> confirmOffline(
      @PathVariable UUID id,
      @Valid @RequestBody ConfirmOfflinePaymentResource body,
      HttpServletRequest request,
      Locale locale) {
    UUID actorId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toOk(
        commandService.handle(SubscriptionsRestAssembler.toCommand(id, actorId, body)),
        SubscriptionsRestAssembler::toResource,
        request,
        locale);
  }

  @PostMapping("/payments/{id}/reject")
  @Operation(summary = "Reject a pending offline payment")
  public ResponseEntity<?> reject(
      @PathVariable UUID id, HttpServletRequest request, Locale locale) {
    UUID actorId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toOk(
        commandService.handle(new RejectPaymentCommand(id, actorId)),
        SubscriptionsRestAssembler::toResource,
        request,
        locale);
  }

  @GetMapping("/benefits")
  @Operation(summary = "List active collective benefits")
  public ResponseEntity<?> benefits(HttpServletRequest request, Locale locale) {
    return responseAssembler.toOk(
        queryService.handle(new GetBenefitsQuery()),
        SubscriptionsRestAssembler::toBenefitResources,
        request,
        locale);
  }

  @PostMapping("/benefits")
  @Operation(summary = "Create a collective benefit")
  public ResponseEntity<?> createBenefit(
      @Valid @RequestBody CreateBenefitResource body, HttpServletRequest request, Locale locale) {
    return responseAssembler.toCreated(
        commandService.handle(SubscriptionsRestAssembler.toCommand(body)),
        SubscriptionsRestAssembler::toResource,
        b -> URI.create("/api/v1/benefits/" + b.getId()),
        request,
        locale);
  }

  @PostMapping("/benefits/{id}/claims")
  @Operation(summary = "Claim a collective benefit eligible for worker active plan")
  public ResponseEntity<?> claimBenefit(
      @PathVariable UUID id, HttpServletRequest request, Locale locale) {
    UUID workerId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toCreated(
        commandService.handle(new ClaimBenefitCommand(id, workerId)),
        SubscriptionsRestAssembler::toResource,
        c -> URI.create("/api/v1/benefit-claims/" + c.getId()),
        request,
        locale);
  }

  @GetMapping("/benefit-claims")
  @Operation(summary = "List collective benefit claims for current worker")
  public ResponseEntity<?> claims(HttpServletRequest request, Locale locale) {
    UUID workerId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toOk(
        queryService.handle(new GetBenefitClaimsQuery(workerId)),
        SubscriptionsRestAssembler::toBenefitClaimResources,
        request,
        locale);
  }

  @GetMapping("/referral-rewards")
  @Operation(summary = "List referral rewards earned by current worker")
  public ResponseEntity<?> rewards(HttpServletRequest request, Locale locale) {
    UUID workerId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toOk(
        queryService.handle(new GetReferralRewardsQuery(workerId)),
        SubscriptionsRestAssembler::toRewardResources,
        request,
        locale);
  }

  @PostMapping("/referral-rewards/{id}/redeem")
  @Operation(summary = "Redeem a referral reward to extend active subscription")
  public ResponseEntity<?> redeem(
      @PathVariable UUID id, HttpServletRequest request, Locale locale) {
    UUID workerId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toOk(
        commandService.handle(new RedeemReferralRewardCommand(id, workerId)),
        SubscriptionsRestAssembler::toResource,
        request,
        locale);
  }

  @GetMapping("/referral-code")
  @Operation(summary = "Get or generate referral code for current worker")
  public ResponseEntity<?> referralCode(HttpServletRequest request, Locale locale) {
    UUID workerId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toOk(
        commandService.handle(new GetOrCreateReferralCodeCommand(workerId)),
        SubscriptionsRestAssembler::toResource,
        request,
        locale);
  }

  @PostMapping("/referrals")
  @Operation(summary = "Apply a referral code to current worker account")
  public ResponseEntity<?> useReferralCode(
      @Valid @RequestBody UseReferralCodeResource body, HttpServletRequest request, Locale locale) {
    UUID workerId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toCreated(
        commandService.handle(SubscriptionsRestAssembler.toCommand(workerId, body)),
        SubscriptionsRestAssembler::toResource,
        u -> URI.create("/api/v1/referrals/" + u.getId()),
        request,
        locale);
  }

  @PostMapping("/subscriptions/{id}/members")
  @Operation(summary = "Invite another worker to share a family/crew subscription")
  public ResponseEntity<?> inviteMember(
      @PathVariable UUID id,
      @Valid @RequestBody InviteSubscriptionMemberResource body,
      HttpServletRequest request,
      Locale locale) {
    UUID actorId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toCreated(
        commandService.handle(SubscriptionsRestAssembler.toCommand(id, actorId, body)),
        SubscriptionsRestAssembler::toResource,
        m -> URI.create("/api/v1/subscription-members/" + m.getId()),
        request,
        locale);
  }

  @GetMapping("/subscriptions/{id}/members")
  @Operation(summary = "List members for a subscription")
  public ResponseEntity<?> members(
      @PathVariable UUID id, HttpServletRequest request, Locale locale) {
    UUID actorId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toOk(
        queryService.handle(new GetSubscriptionMembersQuery(id, actorId)),
        SubscriptionsRestAssembler::toMemberResources,
        request,
        locale);
  }

  @GetMapping("/subscription-invitations")
  @Operation(summary = "List family/crew subscription invitations for current worker")
  public ResponseEntity<?> invitations(HttpServletRequest request, Locale locale) {
    UUID workerId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toOk(
        queryService.handle(new GetWorkerInvitationsQuery(workerId)),
        SubscriptionsRestAssembler::toInvitationResources,
        request,
        locale);
  }

  @PostMapping("/subscription-invitations/{id}/accept")
  @Operation(summary = "Accept a family/crew subscription invitation")
  public ResponseEntity<?> acceptInvitation(
      @PathVariable UUID id, HttpServletRequest request, Locale locale) {
    UUID actorId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toOk(
        commandService.handle(new RespondInvitationCommand(id, actorId, true)),
        SubscriptionsRestAssembler::toResource,
        request,
        locale);
  }

  @PostMapping("/subscription-invitations/{id}/reject")
  @Operation(summary = "Reject a family/crew subscription invitation")
  public ResponseEntity<?> rejectInvitation(
      @PathVariable UUID id, HttpServletRequest request, Locale locale) {
    UUID actorId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toOk(
        commandService.handle(new RespondInvitationCommand(id, actorId, false)),
        SubscriptionsRestAssembler::toResource,
        request,
        locale);
  }

  @DeleteMapping("/subscription-members/{id}")
  @Operation(summary = "Revoke a subscription member seat")
  public ResponseEntity<?> removeMember(
      @PathVariable UUID id, HttpServletRequest request, Locale locale) {
    UUID actorId = iamFacade.resolveCurrentActor().id();
    return responseAssembler.toNoContent(
        commandService.handle(new RevokeSubscriptionMemberCommand(id, actorId)), request, locale);
  }
}
