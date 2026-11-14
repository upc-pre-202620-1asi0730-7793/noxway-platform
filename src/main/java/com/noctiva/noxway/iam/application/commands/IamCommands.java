package com.noctiva.noxway.iam.application.commands;

/**
 * Structured IAM commands (pending full authentication implementation).
 */
public final class IamCommands {
  private IamCommands() {}

  public record SignInCommand(String email, String password) {}

  public record SignUpCommand(
      String email,
      String password,
      String fullName,
      String phone,
      String role,
      String shiftType,
      String shiftStart,
      String shiftEnd,
      String timeZone) {}

  public record SignOutCommand(String token) {}

  public record RequestPasswordResetCommand(String email) {}

  public record ConfirmPasswordResetCommand(String token, String newPassword) {}
}
