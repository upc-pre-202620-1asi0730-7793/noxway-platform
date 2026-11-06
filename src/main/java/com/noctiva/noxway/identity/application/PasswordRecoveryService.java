package com.noctiva.noxway.identity.application;

import static com.noctiva.noxway.shared.domain.Rules.*;

import com.noctiva.noxway.identity.domain.model.aggregates.*;
import com.noctiva.noxway.identity.domain.repositories.*;
import com.noctiva.noxway.shared.infrastructure.security.TokenHash;
import java.time.*;
import java.util.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class PasswordRecoveryService {
  private final AccountRepository accounts;
  private final PasswordResetRepository resets;
  private final AuthSessionRepository sessions;
  private final PasswordEncoder encoder;
  private final ObjectProvider<JavaMailSender> mail;
  private final String from;

  public PasswordRecoveryService(
      AccountRepository a,
      PasswordResetRepository r,
      AuthSessionRepository s,
      PasswordEncoder e,
      ObjectProvider<JavaMailSender> m,
      @Value("${noxway.mail-from:noreply@example.invalid}") String f) {
    accounts = a;
    resets = r;
    sessions = s;
    encoder = e;
    mail = m;
    from = f;
  }

  public void request(String email) {
    var sender = mail.getIfAvailable();
    if (sender == null)
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "SMTP is not configured");
    var account =
        accounts.findByEmail(email.trim().toLowerCase(Locale.ROOT)).stream()
            .filter(Account::active)
            .findFirst();
    if (account.isEmpty()) return;
    var a = account.get();
    require(
        resets.findByAccountId(a.id()).stream()
            .noneMatch(r -> r.createdAt().isAfter(Instant.now().minusSeconds(60))),
        "Wait before requesting another recovery email");
    String token = TokenHash.random();
    resets.save(
        PasswordReset.create(
            a.id(), TokenHash.digest(token), Instant.now().plusSeconds(1800), false));
    var msg = new SimpleMailMessage();
    msg.setFrom(from);
    msg.setTo(a.email());
    msg.setSubject("Noxway password recovery");
    msg.setText(
        "Your single-use recovery token (expires in 30 minutes): "
            + token
            + "\n"
            + "Use it in the Noxway password reset form. If you did not request this, ignore"
            + " it.");
    try {
      sender.send(msg);
    } catch (org.springframework.mail.MailException e) {
      throw new ResponseStatusException(
          HttpStatus.SERVICE_UNAVAILABLE, "Recovery delivery unavailable");
    }
  }

  public void reset(String token, String password) {
    var r =
        resets.findByTokenHash(TokenHash.digest(token)).stream()
            .findFirst()
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Invalid or expired recovery token"));
    require(!r.used() && r.expiresAt().isAfter(Instant.now()), "Invalid or expired recovery token");
    require(
        password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length <= 72,
        "Password exceeds 72 UTF-8 bytes");
    var account =
        accounts
            .lockById(r.accountId())
            .filter(Account::active)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account unavailable"));
    accounts.save(account.withPasswordHash(encoder.encode(password)));
    for (var reset : resets.findByAccountId(account.id()))
      if (!reset.used()) resets.save(reset.withUsed(true));
    for (var session : sessions.findByAccountId(account.id()))
      if (session.active()) sessions.save(session.withActive(false));
  }
}
