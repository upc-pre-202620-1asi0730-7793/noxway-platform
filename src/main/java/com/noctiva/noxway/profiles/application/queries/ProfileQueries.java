package com.noctiva.noxway.profiles.application.queries;

import java.util.UUID;

public final class ProfileQueries {
  private ProfileQueries() {}

  public record GetProfileByIdQuery(UUID accountId) {}

  public record GetDirectoryQuery() {}

  public record GetNotificationPreferencesQuery(UUID accountId) {}

  public record GetTrustLinksQuery(UUID actorId) {}
}
