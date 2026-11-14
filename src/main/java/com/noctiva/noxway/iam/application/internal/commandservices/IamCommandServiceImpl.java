package com.noctiva.noxway.iam.application.internal.commandservices;

import com.noctiva.noxway.iam.application.commands.IamCommands.*;
import com.noctiva.noxway.iam.application.commandservices.IamCommandService;
import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.application.result.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Default IAM command handler. When {@code app.security.enabled=false}, authentication endpoints
 * remain cleanly structured and return a deferred implementation status rather than enforcing login.
 */
@Service
public class IamCommandServiceImpl implements IamCommandService {

  private final boolean securityEnabled;

  public IamCommandServiceImpl(@Value("${app.security.enabled:false}") boolean securityEnabled) {
    this.securityEnabled = securityEnabled;
  }

  @Override
  public Result<AuthenticatedSessionView, ApplicationError> handle(SignInCommand command) {
    return notImplemented();
  }

  @Override
  public Result<RegisteredIdentityView, ApplicationError> handle(SignUpCommand command) {
    return notImplemented();
  }

  @Override
  public Result<Void, ApplicationError> handle(SignOutCommand command) {
    return notImplemented();
  }

  @Override
  public Result<Void, ApplicationError> handle(RequestPasswordResetCommand command) {
    return notImplemented();
  }

  @Override
  public Result<Void, ApplicationError> handle(ConfirmPasswordResetCommand command) {
    return notImplemented();
  }

  private <T> Result<T, ApplicationError> notImplemented() {
    String message =
        securityEnabled
            ? "Full IAM authentication implementation is pending"
            : "IAM authentication is structured in the project and pending implementation (app.security.enabled=false).";
    return Result.failure(new ApplicationError.NotImplementedError("iam.not_implemented", message));
  }
}
