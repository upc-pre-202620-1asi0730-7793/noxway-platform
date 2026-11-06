package com.noctiva.noxway.identity.application;

import static com.noctiva.noxway.shared.domain.Rules.*;

import com.noctiva.noxway.identity.application.commands.IdentityCommands;
import com.noctiva.noxway.identity.application.results.AccountResult;
import com.noctiva.noxway.identity.domain.model.aggregates.*;
import com.noctiva.noxway.identity.domain.repositories.*;
import com.noctiva.noxway.identity.interfaces.acl.IdentityAccess;
import com.noctiva.noxway.shared.infrastructure.security.*;
import java.time.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class IdentityApplicationService implements IdentityAccess {
  private final AccountRepository accounts;
  private final AuthSessionRepository sessions;
  private final TrustLinkRepository links;
  private final NotificationPreferenceRepository preferences;
  private final PasswordEncoder encoder;
  private final long sessionHours;
  private final com.noctiva.noxway.subscriptions.interfaces.acl.ContactAllowance allowance;

  public IdentityApplicationService(
      AccountRepository a,
      AuthSessionRepository s,
      TrustLinkRepository l,
      NotificationPreferenceRepository p,
      PasswordEncoder e,
      @Value("${noxway.session-hours}") long hours,
      com.noctiva.noxway.subscriptions.interfaces.acl.ContactAllowance allowance) {
    accounts = a;
    sessions = s;
    links = l;
    preferences = p;
    encoder = e;
    sessionHours = hours;
    this.allowance = allowance;
  }

  public Account account(UUID id) {
    return accounts
        .findById(id)
        .filter(Account::active)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
  }

  public Account register(IdentityCommands.Register c) {
    require(
        c.password().getBytes(java.nio.charset.StandardCharsets.UTF_8).length <= 72,
        "Password exceeds 72 UTF-8 bytes");
    String email = c.email().trim().toLowerCase(Locale.ROOT);
    require(accounts.findByEmail(email).isEmpty(), "Email is already registered");
    String zone = c.timeZone() == null ? "America/Lima" : c.timeZone();
    validateShift(c.role(), c.shiftType(), c.shiftStart(), c.shiftEnd(), zone);
    return accounts.save(
        Account.create(
            email,
            c.fullName().trim(),
            c.phone(),
            c.role(),
            c.shiftType(),
            c.shiftStart(),
            c.shiftEnd(),
            zone,
            encoder.encode(c.password()),
            true));
  }

  public record LoginResult(
      String accessToken, String tokenType, Instant expiresAt, AccountResult account) {}

  public LoginResult login(IdentityCommands.Login c) {
    var a =
        accounts.findByEmail(c.email().trim().toLowerCase(Locale.ROOT)).stream()
            .filter(Account::active)
            .findFirst();
    if (a.isEmpty() || !encoder.matches(c.password(), a.get().passwordHash()))
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    String token = TokenHash.random();
    Instant expiry = Instant.now().plus(Duration.ofHours(sessionHours));
    sessions.save(AuthSession.create(a.get().id(), TokenHash.digest(token), expiry, true));
    return new LoginResult(token, "Bearer", expiry, AccountResult.from(a.get()));
  }

  public void logout(String header) {
    String digest = TokenHash.digest(header.substring(7));
    sessions.findByTokenHash(digest).forEach(s -> sessions.save(s.withActive(false)));
  }

  public Account profile(IdentityCommands.Profile c) {
    var a = account(Actor.current().id());
    validateShift(a.role(), c.shiftType(), c.shiftStart(), c.shiftEnd(), c.timeZone());
    return accounts.save(
        a.withFullName(c.fullName())
            .withPhone(c.phone())
            .withShiftType(c.shiftType())
            .withShiftStart(c.shiftStart())
            .withShiftEnd(c.shiftEnd())
            .withTimeZone(c.timeZone()));
  }

  public void changePassword(IdentityCommands.Password c) {
    require(
        c.newPassword().getBytes(java.nio.charset.StandardCharsets.UTF_8).length <= 72,
        "Password exceeds 72 UTF-8 bytes");
    var a = account(Actor.current().id());
    require(
        encoder.matches(c.currentPassword(), a.passwordHash()), "Current password is incorrect");
    accounts.save(a.withPasswordHash(encoder.encode(c.newPassword())));
    sessions.findByAccountId(a.id()).stream()
        .filter(AuthSession::active)
        .forEach(s -> sessions.save(s.withActive(false)));
  }

  private void validateShift(String role, String shift, String start, String end, String zone) {
    zone(zone);
    if (role.equals("WORKER")) {
      require(
          Set.of("DAY", "NIGHT", "ROTATING").contains(shift == null ? "" : shift),
          "Worker shift must be DAY, NIGHT or ROTATING");
      try {
        require(
            !LocalTime.parse(start).equals(LocalTime.parse(end)),
            "Shift start and end must differ");
      } catch (java.time.DateTimeException | NullPointerException e) {
        throw new com.noctiva.noxway.shared.domain.RuleViolation("Use HH:mm shift times");
      }
    }
  }

  public TrustLink invite(String email) {
    var actor = Actor.current();
    actor.worker();
    lockWorker(actor.id());
    var contact =
        accounts.findByEmail(email.trim().toLowerCase(Locale.ROOT)).stream()
            .filter(Account::active)
            .findFirst()
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Contact must register first"));
    require(contact.role().equals("TRUSTED_CONTACT"), "Recipient must be a trusted contact");
    var existing =
        links.findByWorkerId(actor.id()).stream()
            .filter(l -> l.contactId().equals(contact.id()))
            .findFirst();
    require(
        links.findByWorkerId(actor.id()).stream()
                .filter(l -> l.status().equals("PENDING") || l.status().equals("ACCEPTED"))
                .count()
            < allowance.maxContacts(actor.id()),
        "Contact limit reached for current plan");
    if (existing.isPresent()) {
      require(
          existing.get().status().equals("REVOKED") || existing.get().status().equals("REJECTED"),
          "An invitation or link already exists");
      return links.save(existing.get().withStatus("PENDING").withAcceptedAt(null));
    }
    return links.save(TrustLink.create(actor.id(), contact.id(), "PENDING", null));
  }

  public List<TrustLink> myLinks() {
    var id = Actor.current().id();
    return links.findAll().stream()
        .filter(l -> l.workerId().equals(id) || l.contactId().equals(id))
        .toList();
  }

  public TrustLink respond(UUID id, String action) {
    var l =
        links
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Link not found"));
    var actor = Actor.current();
    if (action.equals("REVOKE")) {
      require(
          l.workerId().equals(actor.id()) || l.contactId().equals(actor.id()), "Not a participant");
      return links.save(l.withStatus("REVOKED"));
    }
    actor.own(l.contactId());
    require(l.status().equals("PENDING"), "Invitation is no longer pending");
    return links.save(
        l.withStatus(action.equals("ACCEPT") ? "ACCEPTED" : "REJECTED")
            .withAcceptedAt(action.equals("ACCEPT") ? Instant.now() : null));
  }

  public NotificationPreference preference(IdentityCommands.Preference c) {
    zone(c.timeZone());
    try {
      LocalTime.parse(c.priorityStart());
      LocalTime.parse(c.priorityEnd());
    } catch (DateTimeException e) {
      throw new com.noctiva.noxway.shared.domain.RuleViolation("Use HH:mm priority times");
    }
    UUID id = Actor.current().id();
    var old =
        preferences.findByAccountId(id).stream()
            .filter(p -> p.channel().equals(c.channel()))
            .findFirst();
    return preferences.save(
        old.map(
                p ->
                    p.withEnabled(c.enabled())
                        .withPriorityStart(c.priorityStart())
                        .withPriorityEnd(c.priorityEnd())
                        .withTimeZone(c.timeZone()))
            .orElseGet(
                () ->
                    NotificationPreference.create(
                        id,
                        c.channel(),
                        c.enabled(),
                        c.priorityStart(),
                        c.priorityEnd(),
                        c.timeZone())));
  }

  public List<NotificationPreference> preferences() {
    return preferences.findByAccountId(Actor.current().id());
  }

  public UUID workerByEmail(String email) {
    var a =
        accounts.findByEmail(email.trim().toLowerCase(Locale.ROOT)).stream()
            .filter(x -> x.active() && x.role().equals("WORKER"))
            .findFirst()
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Worker must register first"));
    return a.id();
  }

  public void lockWorker(UUID id) {
    var a =
        accounts
            .lockById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Worker not found"));
    require(a.active() && a.role().equals("WORKER"), "Active worker required");
  }

  public void requireWorker(UUID id) {
    require(account(id).role().equals("WORKER"), "Worker not found");
  }

  public void requireAccount(UUID id) {
    account(id);
  }

  public boolean canAccompany(UUID contact, UUID worker) {
    return links.findByWorkerId(worker).stream()
        .anyMatch(l -> l.contactId().equals(contact) && l.status().equals("ACCEPTED"));
  }

  public List<UUID> contactsOf(UUID worker) {
    return links.findByWorkerId(worker).stream()
        .filter(l -> l.status().equals("ACCEPTED"))
        .map(TrustLink::contactId)
        .toList();
  }

  public boolean priorityAt(UUID contact, String channel, Instant at) {
    var pref =
        preferences.findByAccountId(contact).stream()
            .filter(p -> p.channel().equals(channel) && p.enabled())
            .findFirst();
    if (pref.isEmpty()) return true;
    var p = pref.get();
    LocalTime time = at.atZone(ZoneId.of(p.timeZone())).toLocalTime(),
        start = LocalTime.parse(p.priorityStart()),
        end = LocalTime.parse(p.priorityEnd());
    return start.equals(end)
        || (start.isBefore(end)
            ? !time.isBefore(start) && time.isBefore(end)
            : !time.isBefore(start) || time.isBefore(end));
  }

  public IdentityAccess.DeliveryContact deliveryContact(UUID id) {
    var a = account(id);
    return new IdentityAccess.DeliveryContact(a.email(), a.phone());
  }

  public List<String> channelsFor(UUID contact) {
    var p = preferences.findByAccountId(contact);
    return p.isEmpty()
        ? List.of("IN_APP")
        : p.stream()
            .filter(NotificationPreference::enabled)
            .map(NotificationPreference::channel)
            .toList();
  }
}
