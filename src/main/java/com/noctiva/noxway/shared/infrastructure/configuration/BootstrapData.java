package com.noctiva.noxway.shared.infrastructure.configuration;

import com.noctiva.noxway.identity.domain.model.aggregates.Account;
import com.noctiva.noxway.identity.domain.repositories.AccountRepository;
import com.noctiva.noxway.subscriptions.domain.model.aggregates.Plan;
import com.noctiva.noxway.subscriptions.domain.repositories.PlanRepository;
import java.math.BigDecimal;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BootstrapData implements ApplicationRunner {
  private final PlanRepository plans;
  private final AccountRepository accounts;
  private final PasswordEncoder encoder;
  private final String email, password;

  public BootstrapData(
      PlanRepository p,
      AccountRepository a,
      PasswordEncoder e,
      @Value("${noxway.bootstrap-admin-email}") String mail,
      @Value("${noxway.bootstrap-admin-password}") String pass) {
    plans = p;
    accounts = a;
    encoder = e;
    email = mail;
    password = pass;
  }

  @Transactional
  public void run(ApplicationArguments args) {
    seed("ESSENTIAL", "Esencial", "0.00", 1, 1);
    seed("SENTINEL_PRO", "Centinela Pro", "12.00", 100, 1);
    seed("FAMILY_CREW", "Cuadrilla Familiar", "25.00", 100, 5);
    if (!email.isBlank()) {
      if (password.length() < 12
          || password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72)
        throw new IllegalStateException(
            "Bootstrap password must be at least 12 characters and at most 72 UTF-8 bytes");
      String normalized = email.toLowerCase(Locale.ROOT).trim();
      var existing = accounts.findByEmail(normalized);
      if (existing.isEmpty())
        accounts.save(
            Account.create(
                normalized,
                "Noxway Administrator",
                "Not configured",
                "ADMIN",
                null,
                null,
                null,
                "America/Lima",
                encoder.encode(password),
                true));
      else if (!existing.get(0).role().equals("ADMIN"))
        throw new IllegalStateException("Bootstrap email already belongs to a non-admin account");
    }
  }

  private void seed(String code, String name, String price, int contacts, int workers) {
    if (plans.findByCode(code).isEmpty())
      plans.save(Plan.create(code, name, new BigDecimal(price), contacts, workers, true));
  }
}
