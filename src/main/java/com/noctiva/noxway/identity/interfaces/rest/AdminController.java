package com.noctiva.noxway.identity.interfaces.rest;

import static com.noctiva.noxway.shared.domain.Rules.*;

import com.noctiva.noxway.identity.application.results.AccountResult;
import com.noctiva.noxway.identity.domain.model.aggregates.Account;
import com.noctiva.noxway.identity.domain.repositories.AccountRepository;
import com.noctiva.noxway.shared.infrastructure.security.Actor;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@Transactional
public class AdminController {
  private final AccountRepository accounts;
  private final PasswordEncoder encoder;

  public AdminController(AccountRepository a, PasswordEncoder p) {
    accounts = a;
    encoder = p;
  }

  public record Staff(
      @Email @NotBlank String email,
      @NotBlank @Size(min = 12, max = 72) String password,
      @NotBlank @Size(max = 150) String fullName,
      @Pattern(regexp = "MODERATOR|ADMIN") @NotNull String role) {}

  @PostMapping("/staff")
  @ResponseStatus(HttpStatus.CREATED)
  public AccountResult staff(@Valid @RequestBody Staff c) {
    if (!Actor.current().role().equals("ADMIN"))
      throw new org.springframework.web.server.ResponseStatusException(
          HttpStatus.FORBIDDEN, "Admin role required");
    String email = c.email().trim().toLowerCase(Locale.ROOT);
    require(accounts.findByEmail(email).isEmpty(), "Email already registered");
    require(
        c.password().getBytes(java.nio.charset.StandardCharsets.UTF_8).length <= 72,
        "Password exceeds 72 UTF-8 bytes");
    return AccountResult.from(
        accounts.save(
            Account.create(
                email,
                c.fullName(),
                "Not configured",
                c.role(),
                null,
                null,
                null,
                "America/Lima",
                encoder.encode(c.password()),
                true)));
  }
}
