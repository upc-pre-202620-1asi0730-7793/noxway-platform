package com.noctiva.noxway.subscriptions.interfaces.rest;

import com.noctiva.noxway.shared.interfaces.rest.Pages;
import com.noctiva.noxway.subscriptions.application.SubscriptionApplicationService;
import com.noctiva.noxway.subscriptions.application.commands.SubscriptionCommands;
import com.noctiva.noxway.subscriptions.domain.model.aggregates.*;
import jakarta.validation.Valid;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class SubscriptionController {
  private final SubscriptionApplicationService service;

  public SubscriptionController(SubscriptionApplicationService s) {
    service = s;
  }

  @GetMapping("/plans")
  public List<Plan> plans() {
    return service.plans();
  }

  @PostMapping("/subscriptions")
  @ResponseStatus(HttpStatus.CREATED)
  public Subscription subscribe(@Valid @RequestBody SubscriptionCommands.Subscribe c) {
    return service.subscribe(c.planId());
  }

  @GetMapping("/subscriptions")
  public List<Subscription> mine() {
    return service.mine();
  }

  @PostMapping("/subscriptions/{id}/cancel")
  public Subscription cancel(@PathVariable UUID id) {
    return service.cancel(id);
  }

  @PostMapping("/subscriptions/{id}/payments")
  @ResponseStatus(HttpStatus.CREATED)
  public PaymentTransaction pay(@PathVariable UUID id) {
    return service.requestPayment(id);
  }

  @GetMapping("/payments")
  public Pages<PaymentTransaction> payments(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return Pages.of(service.payments(), page, size);
  }

  @PostMapping("/payments/{id}/confirm-offline")
  public PaymentTransaction confirm(
      @PathVariable UUID id, @Valid @RequestBody SubscriptionCommands.Payment c) {
    return service.confirmPayment(id, c.reference());
  }

  @PostMapping("/payments/{id}/reject")
  public PaymentTransaction reject(@PathVariable UUID id) {
    return service.rejectPayment(id);
  }

  @GetMapping("/benefits")
  public List<CollectiveBenefit> benefits() {
    return service.benefits();
  }

  @PostMapping("/benefits")
  @ResponseStatus(HttpStatus.CREATED)
  public CollectiveBenefit benefit(@Valid @RequestBody SubscriptionCommands.Benefit c) {
    return service.benefit(c);
  }

  @PostMapping("/benefits/{id}/claims")
  @ResponseStatus(HttpStatus.CREATED)
  public BenefitClaim claim(@PathVariable UUID id) {
    return service.claim(id);
  }

  @GetMapping("/benefit-claims")
  public List<BenefitClaim> claims() {
    return service.claims();
  }

  @GetMapping("/referral-rewards")
  public List<ReferralReward> rewards() {
    return service.rewards();
  }

  @PostMapping("/referral-rewards/{id}/redeem")
  public ReferralReward redeem(@PathVariable UUID id) {
    return service.redeemReward(id);
  }

  @GetMapping("/referral-code")
  public ReferralCode code() {
    return service.code();
  }

  @PostMapping("/referrals")
  @ResponseStatus(HttpStatus.CREATED)
  public ReferralUse referral(@Valid @RequestBody SubscriptionCommands.Referral c) {
    return service.useCode(c.code());
  }

  @PostMapping("/subscriptions/{id}/members")
  @ResponseStatus(HttpStatus.CREATED)
  public SubscriptionMember invite(
      @PathVariable UUID id, @Valid @RequestBody SubscriptionCommands.InviteSubscriptionMember c) {
    return service.invite(id, c.workerEmail());
  }

  @GetMapping("/subscriptions/{id}/members")
  public List<SubscriptionMember> members(@PathVariable UUID id) {
    return service.group(id);
  }

  @GetMapping("/subscription-invitations")
  public List<SubscriptionMember> invitations() {
    return service.invitations();
  }

  @PostMapping("/subscription-invitations/{id}/accept")
  public SubscriptionMember accept(@PathVariable UUID id) {
    return service.respond(id, true);
  }

  @PostMapping("/subscription-invitations/{id}/reject")
  public SubscriptionMember rejectInvitation(@PathVariable UUID id) {
    return service.respond(id, false);
  }

  @DeleteMapping("/subscription-members/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void remove(@PathVariable UUID id) {
    service.removeMember(id);
  }
}
