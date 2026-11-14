package com.noctiva.noxway.profiles.application.internal.commandservices;

import com.noctiva.noxway.profiles.application.commands.ProfileCommands.*;
import com.noctiva.noxway.profiles.application.commandservices.ProfileCommandService;
import com.noctiva.noxway.profiles.domain.model.aggregates.AccountProfile;
import com.noctiva.noxway.profiles.domain.model.aggregates.NotificationPreference;
import com.noctiva.noxway.profiles.domain.model.aggregates.TrustLink;
import com.noctiva.noxway.profiles.domain.repositories.AccountProfileRepository;
import com.noctiva.noxway.profiles.domain.repositories.NotificationPreferenceRepository;
import com.noctiva.noxway.profiles.domain.repositories.TrustLinkRepository;
import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.application.result.Result;
import com.noctiva.noxway.shared.domain.RuleViolation;
import com.noctiva.noxway.subscriptions.interfaces.acl.ContactAllowance;
import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProfileCommandServiceImpl implements ProfileCommandService {

  private static final Set<String> ALLOWED_CREATE_ROLES = Set.of("WORKER", "TRUSTED_CONTACT");

  private final AccountProfileRepository accountRepository;
  private final TrustLinkRepository trustLinkRepository;
  private final NotificationPreferenceRepository preferenceRepository;
  private final ContactAllowance contactAllowance;

  public ProfileCommandServiceImpl(
      AccountProfileRepository accountRepository,
      TrustLinkRepository trustLinkRepository,
      NotificationPreferenceRepository preferenceRepository,
      ContactAllowance contactAllowance) {
    this.accountRepository = accountRepository;
    this.trustLinkRepository = trustLinkRepository;
    this.preferenceRepository = preferenceRepository;
    this.contactAllowance = contactAllowance;
  }

  @Override
  public Result<AccountProfile, ApplicationError> handle(CreateProfileCommand cmd) {
    try {
      String email = cmd.email() == null ? "" : cmd.email().trim().toLowerCase(Locale.ROOT);
      if (accountRepository.existsByEmail(email)) {
        return Result.failure(
            new ApplicationError.ConflictError(
                "profiles.account.email_conflict", "Email already registered"));
      }
      if (!ALLOWED_CREATE_ROLES.contains(cmd.role())) {
        return Result.failure(
            new ApplicationError.ValidationError("Role must be WORKER or TRUSTED_CONTACT"));
      }
      var created =
          accountRepository.save(
              AccountProfile.create(
                  null,
                  email,
                  cmd.fullName(),
                  cmd.phone(),
                  cmd.role(),
                  cmd.shiftType(),
                  cmd.shiftStart(),
                  cmd.shiftEnd(),
                  cmd.timeZone(),
                  null));
      String tz = created.getTimeZone();
      preferenceRepository.save(
          NotificationPreference.create(created.getId(), "EMAIL", true, "22:00", "06:00", tz));
      preferenceRepository.save(
          NotificationPreference.create(created.getId(), "WHATSAPP", true, "22:00", "06:00", tz));
      return Result.success(created);
    } catch (RuleViolation ex) {
      return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
    }
  }

  @Override
  public Result<AccountProfile, ApplicationError> handle(UpdateProfileCommand cmd) {
    var found = accountRepository.findById(cmd.accountId());
    if (found.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError("profiles.account.not_found", "Account not found"));
    }
    try {
      var account = found.get();
      account.updateProfile(
          cmd.fullName(),
          cmd.phone(),
          cmd.shiftType(),
          cmd.shiftStart(),
          cmd.shiftEnd(),
          cmd.timeZone());
      return Result.success(accountRepository.save(account));
    } catch (RuleViolation ex) {
      return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
    }
  }

  @Override
  public Result<NotificationPreference, ApplicationError> handle(
      UpdateNotificationPreferenceCommand cmd) {
    var existing =
        preferenceRepository.findByAccountIdAndChannel(cmd.accountId(), cmd.channel());
    try {
      NotificationPreference pref;
      if (existing.isPresent()) {
        pref = existing.get();
        pref.update(cmd.enabled(), cmd.priorityStart(), cmd.priorityEnd(), cmd.timeZone());
      } else {
        pref =
            NotificationPreference.create(
                cmd.accountId(),
                cmd.channel(),
                cmd.enabled(),
                cmd.priorityStart(),
                cmd.priorityEnd(),
                cmd.timeZone());
      }
      return Result.success(preferenceRepository.save(pref));
    } catch (RuleViolation ex) {
      return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
    }
  }

  @Override
  public Result<TrustLink, ApplicationError> handle(CreateTrustLinkCommand cmd) {
    String email =
        cmd.contactEmail() == null ? "" : cmd.contactEmail().trim().toLowerCase(Locale.ROOT);
    var workerOpt = accountRepository.findById(cmd.workerId());
    if (workerOpt.isEmpty() || !"WORKER".equals(workerOpt.get().getRole())) {
      return Result.failure(
          new ApplicationError.BusinessRuleError("Only worker accounts can invite trusted contacts"));
    }
    var contactOpt = accountRepository.findByEmail(email).filter(AccountProfile::isActive);
    if (contactOpt.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError("Trusted contact email was not found"));
    }
    var contact = contactOpt.get();
    int limit = contactAllowance.maxContacts(cmd.workerId());
    long currentActiveOrPending =
        trustLinkRepository.findByWorkerId(cmd.workerId()).stream()
            .filter(l -> l.isActive() || l.isPending())
            .count();
    if (currentActiveOrPending >= limit) {
      return Result.failure(
          new ApplicationError.BusinessRuleError(
              "profiles.trust_link.limit_reached",
              "Current plan allows at most " + limit + " trusted contact(s)",
              limit));
    }

    var existingOpt = trustLinkRepository.findByWorkerIdAndContactId(cmd.workerId(), contact.getId());
    if (existingOpt.isPresent()) {
      var existing = existingOpt.get();
      if (existing.isActive() || existing.isPending()) {
        return Result.failure(
            new ApplicationError.ConflictError(
                "profiles.trust_link.already_exists",
                "Trust link already active or pending for this contact"));
      }
      try {
        var recreated = TrustLink.createPending(cmd.workerId(), contact.getId());
        trustLinkRepository.delete(existing.getId());
        return Result.success(trustLinkRepository.save(recreated));
      } catch (RuleViolation ex) {
        return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
      }
    }

    try {
      var link = TrustLink.createPending(cmd.workerId(), contact.getId());
      return Result.success(trustLinkRepository.save(link));
    } catch (RuleViolation ex) {
      return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
    }
  }

  @Override
  public Result<TrustLink, ApplicationError> handle(AcceptTrustLinkCommand cmd) {
    var linkOpt = trustLinkRepository.findById(cmd.linkId());
    if (linkOpt.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError(
              "profiles.trust_link.not_found", "Trust link not found"));
    }
    var link = linkOpt.get();
    if (!link.getContactId().equals(cmd.actorId())) {
      return Result.failure(
          new ApplicationError.ForbiddenError("Only the invited contact can accept this link"));
    }
    try {
      link.accept(cmd.actorId());
      return Result.success(trustLinkRepository.save(link));
    } catch (RuleViolation ex) {
      return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
    }
  }

  @Override
  public Result<TrustLink, ApplicationError> handle(RejectTrustLinkCommand cmd) {
    var linkOpt = trustLinkRepository.findById(cmd.linkId());
    if (linkOpt.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError(
              "profiles.trust_link.not_found", "Trust link not found"));
    }
    var link = linkOpt.get();
    if (!link.getContactId().equals(cmd.actorId())) {
      return Result.failure(
          new ApplicationError.ForbiddenError("Only the invited contact can reject this link"));
    }
    try {
      link.reject(cmd.actorId());
      return Result.success(trustLinkRepository.save(link));
    } catch (RuleViolation ex) {
      return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
    }
  }

  @Override
  public Result<Void, ApplicationError> handle(RevokeTrustLinkCommand cmd) {
    var linkOpt = trustLinkRepository.findById(cmd.linkId());
    if (linkOpt.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError(
              "profiles.trust_link.not_found", "Trust link not found"));
    }
    var link = linkOpt.get();
    if (!link.getWorkerId().equals(cmd.actorId()) && !link.getContactId().equals(cmd.actorId())) {
      return Result.failure(
          new ApplicationError.ForbiddenError("Only trust link participants can revoke access"));
    }
    try {
      link.revoke(cmd.actorId());
      trustLinkRepository.save(link);
      return Result.success(null);
    } catch (RuleViolation ex) {
      return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
    }
  }
}
