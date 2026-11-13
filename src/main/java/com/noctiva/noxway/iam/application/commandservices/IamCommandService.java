package com.noctiva.noxway.iam.application.commandservices;

import com.noctiva.noxway.iam.application.commands.IamCommands.*;
import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.application.result.Result;
import java.util.UUID;

/**
 * Command contract for IAM operations (structured for future authentication implementation).
 */
public interface IamCommandService {

  Result<AuthenticatedSessionView, ApplicationError> handle(SignInCommand command);

  Result<RegisteredIdentityView, ApplicationError> handle(SignUpCommand command);

  Result<Void, ApplicationError> handle(SignOutCommand command);

  Result<Void, ApplicationError> handle(RequestPasswordResetCommand command);

  Result<Void, ApplicationError> handle(ConfirmPasswordResetCommand command);

  record AuthenticatedSessionView(UUID accountId, String role, String accessToken) {}

  record RegisteredIdentityView(UUID accountId, String email, String role) {}
}
