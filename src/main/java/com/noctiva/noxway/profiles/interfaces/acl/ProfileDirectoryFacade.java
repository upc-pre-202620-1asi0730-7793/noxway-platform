package com.noctiva.noxway.profiles.interfaces.acl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Anti-Corruption Layer facade exposing profile, trust-link, and notification preference lookups
 * to sibling bounded contexts.
 */
public interface ProfileDirectoryFacade {

  Optional<AccountSummary> findById(UUID accountId);

  Optional<AccountSummary> findByEmail(String email);

  boolean existsActiveWorker(UUID accountId);

  boolean isActiveTrustLink(UUID workerId, UUID contactId);

  List<UUID> activeContactIdsForWorker(UUID workerId);

  List<UUID> activeWorkerIdsForContact(UUID contactId);

  List<NotificationChannelView> notificationPreferencesFor(UUID accountId);

  record AccountSummary(
      UUID id,
      String email,
      String fullName,
      String phone,
      String role,
      String shiftType,
      String shiftStart,
      String shiftEnd,
      String timeZone,
      boolean active) {}

  record NotificationChannelView(
      String channel, boolean enabled, String priorityStart, String priorityEnd, String timeZone) {}
}
