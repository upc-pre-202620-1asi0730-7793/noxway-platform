package com.noctiva.noxway.profiles.application.queryservices;

import com.noctiva.noxway.profiles.application.queries.ProfileQueries.*;
import com.noctiva.noxway.profiles.domain.model.aggregates.AccountProfile;
import com.noctiva.noxway.profiles.domain.model.aggregates.NotificationPreference;
import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.application.result.Result;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ProfileQueryService {

  Result<AccountProfile, ApplicationError> handle(GetProfileByIdQuery query);

  Result<List<AccountProfile>, ApplicationError> handle(GetDirectoryQuery query);

  Result<List<NotificationPreference>, ApplicationError> handle(
      GetNotificationPreferencesQuery query);

  Result<List<EnrichedTrustLink>, ApplicationError> handle(GetTrustLinksQuery query);

  record EnrichedTrustLink(
      UUID id,
      UUID workerId,
      String workerName,
      String workerEmail,
      String workerPhone,
      String workerShiftType,
      UUID contactId,
      String contactName,
      String contactEmail,
      String contactPhone,
      String status,
      Instant acceptedAt,
      Instant createdAt) {}
}
