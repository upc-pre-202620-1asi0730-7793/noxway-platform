package com.noctiva.noxway.profiles.application.internal.outboundservices.acl;

import com.noctiva.noxway.profiles.domain.model.aggregates.AccountProfile;
import com.noctiva.noxway.profiles.domain.model.aggregates.TrustLink;
import com.noctiva.noxway.profiles.domain.repositories.AccountProfileRepository;
import com.noctiva.noxway.profiles.domain.repositories.NotificationPreferenceRepository;
import com.noctiva.noxway.profiles.domain.repositories.TrustLinkRepository;
import com.noctiva.noxway.profiles.interfaces.acl.ProfileDirectoryFacade;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProfileDirectoryFacadeImpl implements ProfileDirectoryFacade {

  private final AccountProfileRepository accountRepository;
  private final TrustLinkRepository trustLinkRepository;
  private final NotificationPreferenceRepository notificationRepository;

  public ProfileDirectoryFacadeImpl(
      AccountProfileRepository accountRepository,
      TrustLinkRepository trustLinkRepository,
      NotificationPreferenceRepository notificationRepository) {
    this.accountRepository = accountRepository;
    this.trustLinkRepository = trustLinkRepository;
    this.notificationRepository = notificationRepository;
  }

  @Override
  public Optional<AccountSummary> findById(UUID accountId) {
    if (accountId == null) return Optional.empty();
    return accountRepository.findById(accountId).map(this::toSummary);
  }

  @Override
  public Optional<AccountSummary> findByEmail(String email) {
    if (email == null || email.isBlank()) return Optional.empty();
    return accountRepository
        .findByEmail(email.trim().toLowerCase(Locale.ROOT))
        .map(this::toSummary);
  }

  @Override
  public boolean existsActiveWorker(UUID accountId) {
    return findById(accountId)
        .filter(a -> a.active() && "WORKER".equals(a.role()))
        .isPresent();
  }

  @Override
  public boolean isActiveTrustLink(UUID workerId, UUID contactId) {
    return trustLinkRepository.existsByWorkerIdAndContactIdAndStatus(workerId, contactId, "ACTIVE");
  }

  @Override
  public List<UUID> activeContactIdsForWorker(UUID workerId) {
    return trustLinkRepository.findByWorkerId(workerId).stream()
        .filter(TrustLink::isActive)
        .map(TrustLink::getContactId)
        .toList();
  }

  @Override
  public List<UUID> activeWorkerIdsForContact(UUID contactId) {
    return trustLinkRepository.findByContactId(contactId).stream()
        .filter(TrustLink::isActive)
        .map(TrustLink::getWorkerId)
        .toList();
  }

  @Override
  public List<NotificationChannelView> notificationPreferencesFor(UUID accountId) {
    return notificationRepository.findByAccountId(accountId).stream()
        .map(
            p ->
                new NotificationChannelView(
                    p.getChannel(),
                    p.isEnabled(),
                    p.getPriorityStart(),
                    p.getPriorityEnd(),
                    p.getTimeZone()))
        .toList();
  }

  private AccountSummary toSummary(AccountProfile a) {
    return new AccountSummary(
        a.getId(),
        a.getEmail(),
        a.getFullName(),
        a.getPhone(),
        a.getRole(),
        a.getShiftType(),
        a.getShiftStart(),
        a.getShiftEnd(),
        a.getTimeZone(),
        a.isActive());
  }
}
