package com.noctiva.noxway.identity.interfaces.rest;

import com.noctiva.noxway.identity.application.PasswordRecoveryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/password-reset")
public class PasswordRecoveryController {
  private final PasswordRecoveryService service;

  public PasswordRecoveryController(PasswordRecoveryService s) {
    service = s;
  }

  public record Request(@Email @NotBlank String email) {}

  public record Reset(
      @NotBlank String token, @NotNull @Size(min = 12, max = 72) String newPassword) {}

  @PostMapping
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void request(@Valid @RequestBody Request c) {
    service.request(c.email());
  }

  @PostMapping("/confirm")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void confirm(@Valid @RequestBody Reset c) {
    service.reset(c.token(), c.newPassword());
  }
}
