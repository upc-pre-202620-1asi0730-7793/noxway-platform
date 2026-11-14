package com.noctiva.noxway.profiles.application.internal.queryservices;

import com.noctiva.noxway.profiles.application.queries.ProfileQueries.*;
import com.noctiva.noxway.profiles.application.queryservices.ProfileQueryService;
import com.noctiva.noxway.profiles.domain.model.aggregates.AccountProfile;
import com.noctiva.noxway.profiles.domain.model.aggregates.NotificationPreference;
import com.noctiva.noxway.profiles.domain.model.aggregates.TrustLink;
import com.noctiva.noxway.profiles.domain.repositories.AccountProfileRepository;
import com.noctiva.noxway.profiles.domain.repositories.NotificationPreferenceRepository;
import com.noctiva.noxway.profiles.domain.repositories.TrustLinkRepository;
import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.application.result.Result;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProfileQueryServiceImpl implements ProfileQueryService {

  private final AccountProfileRepository accountRepository;
  private final TrustLinkRepository trustLinkRepository;
  private final NotificationPreferenceRepository preferenceRepository;

  public ProfileQueryServiceImpl(
      AccountProfileRepository accountRepository,
      TrustLinkRepository trustLinkRepository,
      NotificationPreferenceRepository preferenceRepository) {
    this.accountRepository = accountRepository;
    this.trustLinkRepository = trustLinkRepository;
    this.preferenceRepository = preferenceRepository;
  }

  @Override
  public Result<AccountProfile, ApplicationError> handle(GetProfileByIdQuery query) {
    return accountRepository
        .findById(query.accountId())
        .filter(AccountProfile::isActive)
        .map(Result::<AccountProfile, ApplicationError>success)
        .orElseGet(
            () ->
                Result.failure(
                    new ApplicationError.NotFoundError(
                        "profiles.account.not_found", "Account profile not found")));
  }

  @Override
  public Result<List<AccountProfile>, ApplicationError> handle(GetDirectoryQuery query) {
    return Result.success(accountRepository.findByActiveTrueOrderByCreatedAtAsc());
  }

  @Override
  public Result<List<NotificationPreference>, ApplicationError> handle(
      GetNotificationPreferencesQuery query) {
    return Result.success(preferenceRepository.findByAccountId(query.accountId()));
  }

  @Override
  public Result<List<EnrichedTrustLink>, ApplicationError> handle(GetTrustLinksQuery query) {
    Map<UUID, TrustLink> combined = new LinkedHashMap<>();
    trustLinkRepository.findByWorkerId(query.actorId()).forEach(l -> combined.put(l.getId(), l));
    trustLinkRepository.findByContactId(query.actorId()).forEach(l -> combined.put(l.getId(), l));

    var items =
        combined.values().stream()
            .sorted(Comparator.comparing(TrustLink::getCreatedAt).reversed())
            .map(
                link -> {
                  var worker = accountRepository.findById(link.getWorkerId()).orElse(null);
                  var contact = accountRepository.findById(link.getContactId()).orElse(null);
                  return new EnrichedTrustLink(
                      link.getId(),
                      link.getWorkerId(),
                      worker != null ? worker.getFullName() : "Worker",
                      worker != null ? worker.getEmail() : "",
                      worker != null ? worker.getPhone() : "",
                      worker != null ? worker.getShiftType() : null,
                      link.getContactId(),
                      contact != null ? contact.getFullName() : "Contact",
                      contact != null ? contact.getEmail() : "",
                      contact != null ? contact.getPhone() : "",
                      link.getStatus(),
                      link.getAcceptedAt(),
                      link.getCreatedAt());
                })
            .toList();
    return Result.success(items);
  }
}
