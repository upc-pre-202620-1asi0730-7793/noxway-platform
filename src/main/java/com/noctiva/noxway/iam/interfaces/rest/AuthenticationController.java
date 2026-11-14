package com.noctiva.noxway.iam.interfaces.rest;

import com.noctiva.noxway.iam.application.commands.IamCommands.*;
import com.noctiva.noxway.iam.application.commandservices.IamCommandService;
import com.noctiva.noxway.iam.application.queries.IamQueries.GetSecurityStatusQuery;
import com.noctiva.noxway.iam.application.queryservices.IamQueryService;
import com.noctiva.noxway.iam.interfaces.rest.resources.SecurityStatusResource;
import com.noctiva.noxway.iam.interfaces.rest.resources.SignInResource;
import com.noctiva.noxway.iam.interfaces.rest.resources.SignUpResource;
import com.noctiva.noxway.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Structured IAM authentication contract controller.
 * Currently deferred while {@code app.security.enabled=false}.
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication (IAM Structured)", description = "Structured IAM contracts pending full implementation")
public class AuthenticationController {

  private final IamCommandService commandService;
  private final IamQueryService queryService;
  private final ResponseEntityAssembler responseAssembler;

  public AuthenticationController(
      IamCommandService commandService,
      IamQueryService queryService,
      ResponseEntityAssembler responseAssembler) {
    this.commandService = commandService;
    this.queryService = queryService;
    this.responseAssembler = responseAssembler;
  }

  @GetMapping("/status")
  @Operation(summary = "Get IAM module enforcement status")
  public ResponseEntity<?> status(HttpServletRequest request, Locale locale) {
    return responseAssembler.toOk(
        queryService.handle(new GetSecurityStatusQuery()),
        view -> new SecurityStatusResource(view.securityEnabled(), view.mode(), view.message()),
        request,
        locale);
  }

  @PostMapping("/sign-in")
  @Operation(summary = "Structured sign-in contract (pending IAM implementation)")
  public ResponseEntity<?> signIn(
      @Valid @RequestBody SignInResource body, HttpServletRequest request, Locale locale) {
    return responseAssembler.toOk(
        commandService.handle(new SignInCommand(body.email(), body.password())),
        v -> v,
        request,
        locale);
  }

  @PostMapping("/sign-up")
  @Operation(summary = "Structured sign-up contract (pending IAM implementation)")
  public ResponseEntity<?> signUp(
      @Valid @RequestBody SignUpResource body, HttpServletRequest request, Locale locale) {
    return responseAssembler.toCreated(
        commandService.handle(
            new SignUpCommand(
                body.email(),
                body.password(),
                body.fullName(),
                body.phone(),
                body.role(),
                body.shiftType(),
                body.shiftStart(),
                body.shiftEnd(),
                body.timeZone())),
        v -> v,
        v -> URI.create("/api/v1/accounts/" + v.accountId()),
        request,
        locale);
  }

  @PostMapping("/sign-out")
  @Operation(summary = "Structured sign-out contract (pending IAM implementation)")
  public ResponseEntity<?> signOut(HttpServletRequest request, Locale locale) {
    return responseAssembler.toNoContent(
        commandService.handle(new SignOutCommand("")), request, locale);
  }

  @PostMapping("/password-reset")
  @Operation(summary = "Structured password-reset contract (pending IAM implementation)")
  public ResponseEntity<?> passwordReset(
      @RequestParam String email, HttpServletRequest request, Locale locale) {
    return responseAssembler.toNoContent(
        commandService.handle(new RequestPasswordResetCommand(email)), request, locale);
  }
}
