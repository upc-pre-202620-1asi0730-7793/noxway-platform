package com.noctiva.noxway.profiles.application.commandservices;

import com.noctiva.noxway.profiles.application.commands.ProfileCommands.*;
import com.noctiva.noxway.profiles.domain.model.aggregates.AccountProfile;
import com.noctiva.noxway.profiles.domain.model.aggregates.NotificationPreference;
import com.noctiva.noxway.profiles.domain.model.aggregates.TrustLink;
import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.application.result.Result;

public interface ProfileCommandService {

  Result<AccountProfile, ApplicationError> handle(CreateProfileCommand command);

  Result<AccountProfile, ApplicationError> handle(UpdateProfileCommand command);

  Result<NotificationPreference, ApplicationError> handle(
      UpdateNotificationPreferenceCommand command);

  Result<TrustLink, ApplicationError> handle(CreateTrustLinkCommand command);

  Result<TrustLink, ApplicationError> handle(AcceptTrustLinkCommand command);

  Result<TrustLink, ApplicationError> handle(RejectTrustLinkCommand command);

  Result<Void, ApplicationError> handle(RevokeTrustLinkCommand command);
}
