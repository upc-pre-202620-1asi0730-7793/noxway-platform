package com.noctiva.noxway.profiles.interfaces.rest.transform;

import com.noctiva.noxway.profiles.application.commands.ProfileCommands.*;
import com.noctiva.noxway.profiles.application.queryservices.ProfileQueryService.EnrichedTrustLink;
import com.noctiva.noxway.profiles.domain.model.aggregates.AccountProfile;
import com.noctiva.noxway.profiles.domain.model.aggregates.NotificationPreference;
import com.noctiva.noxway.profiles.domain.model.aggregates.TrustLink;
import com.noctiva.noxway.profiles.interfaces.rest.resources.*;
import java.util.UUID;

public final class ProfileRestAssembler {
  private ProfileRestAssembler() {}

  public static CreateProfileCommand toCommand(CreateProfileResource r) {
    return new CreateProfileCommand(
        r.email(),
        r.fullName(),
        r.phone(),
        r.role(),
        r.shiftType(),
        r.shiftStart(),
        r.shiftEnd(),
        r.timeZone());
  }

  public static UpdateProfileCommand toCommand(UUID accountId, UpdateProfileResource r) {
    return new UpdateProfileCommand(
        accountId,
        r.fullName(),
        r.phone(),
        r.shiftType(),
        r.shiftStart(),
        r.shiftEnd(),
        r.timeZone());
  }

  public static UpdateNotificationPreferenceCommand toCommand(
      UUID accountId, UpdateNotificationPreferenceResource r) {
    return new UpdateNotificationPreferenceCommand(
        accountId, r.channel(), r.enabled(), r.priorityStart(), r.priorityEnd(), r.timeZone());
  }

  public static AccountProfileResource toResource(AccountProfile a) {
    return new AccountProfileResource(
        a.getId(),
        a.getEmail(),
        a.getFullName(),
        a.getPhone(),
        a.getRole(),
        a.getShiftType(),
        a.getShiftStart(),
        a.getShiftEnd(),
        a.getTimeZone(),
        a.isActive(),
        a.getCreatedAt());
  }

  public static NotificationPreferenceResource toResource(NotificationPreference p) {
    return new NotificationPreferenceResource(
        p.getId(),
        p.getAccountId(),
        p.getChannel(),
        p.isEnabled(),
        p.getPriorityStart(),
        p.getPriorityEnd(),
        p.getTimeZone());
  }

  public static TrustLinkResource toResource(TrustLink l) {
    return new TrustLinkResource(
        l.getId(),
        l.getWorkerId(),
        "",
        "",
        "",
        null,
        l.getContactId(),
        "",
        "",
        "",
        l.getStatus(),
        l.getAcceptedAt(),
        l.getCreatedAt());
  }

  public static TrustLinkResource toResource(EnrichedTrustLink l) {
    return new TrustLinkResource(
        l.id(),
        l.workerId(),
        l.workerName(),
        l.workerEmail(),
        l.workerPhone(),
        l.workerShiftType(),
        l.contactId(),
        l.contactName(),
        l.contactEmail(),
        l.contactPhone(),
        l.status(),
        l.acceptedAt(),
        l.createdAt());
  }
}
