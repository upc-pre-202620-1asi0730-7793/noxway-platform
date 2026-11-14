package com.noctiva.noxway.profiles.domain.model.aggregates;

import static com.noctiva.noxway.shared.domain.Rules.require;

import com.noctiva.noxway.profiles.domain.model.events.ProfileUpdatedEvent;
import com.noctiva.noxway.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * AccountProfile aggregate root representing a Worker, Trusted Contact, Moderator, or Admin profile.
 */
public class AccountProfile extends AbstractDomainAggregateRoot<AccountProfile> {

  private static final Set<String> ALLOWED_ROLES =
      Set.of("WORKER", "TRUSTED_CONTACT", "MODERATOR", "ADMIN");
  private static final Set<String> ALLOWED_SHIFTS = Set.of("DAY", "NIGHT", "ROTATING");

  private final UUID id;
  private final String email;
  private String fullName;
  private String phone;
  private final String role;
  private String shiftType;
  private String shiftStart;
  private String shiftEnd;
  private String timeZone;
  private String passwordHash;
  private final boolean active;
  private final Instant createdAt;
  private final long version;

  private AccountProfile(
      UUID id,
      String email,
      String fullName,
      String phone,
      String role,
      String shiftType,
      String shiftStart,
      String shiftEnd,
      String timeZone,
      String passwordHash,
      boolean active,
      Instant createdAt,
      long version) {
    this.id = id;
    this.email = email;
    this.fullName = fullName;
    this.phone = phone;
    this.role = role;
    this.shiftType = shiftType;
    this.shiftStart = shiftStart;
    this.shiftEnd = shiftEnd;
    this.timeZone = timeZone;
    this.passwordHash = passwordHash;
    this.active = active;
    this.createdAt = createdAt;
    this.version = version;
  }

  public static AccountProfile create(
      UUID explicitId,
      String email,
      String fullName,
      String phone,
      String role,
      String shiftType,
      String shiftStart,
      String shiftEnd,
      String timeZone,
      String passwordHash) {
    require(email != null && !email.isBlank(), "Email is required");
    String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
    require(fullName != null && !fullName.isBlank(), "Full name is required");
    require(phone != null && phone.trim().length() >= 6, "Valid phone number is required");
    require(role != null && ALLOWED_ROLES.contains(role), "Invalid account role");
    String tz = validateTimeZone(timeZone);
    validateShift(role, shiftType, shiftStart, shiftEnd);

    String hash =
        (passwordHash != null && !passwordHash.isBlank())
            ? passwordHash
            : "{noop}STRUCTURED_IAM_PENDING";

    return new AccountProfile(
        explicitId != null ? explicitId : UUID.randomUUID(),
        normalizedEmail,
        fullName.trim(),
        phone.trim(),
        role,
        "WORKER".equals(role) ? shiftType : null,
        "WORKER".equals(role) ? shiftStart : null,
        "WORKER".equals(role) ? shiftEnd : null,
        tz,
        hash,
        true,
        Instant.now(),
        0L);
  }

  public static AccountProfile reconstitute(
      UUID id,
      String email,
      String fullName,
      String phone,
      String role,
      String shiftType,
      String shiftStart,
      String shiftEnd,
      String timeZone,
      String passwordHash,
      boolean active,
      Instant createdAt,
      long version) {
    return new AccountProfile(
        id,
        email,
        fullName,
        phone,
        role,
        shiftType,
        shiftStart,
        shiftEnd,
        timeZone,
        passwordHash,
        active,
        createdAt,
        version);
  }

  public void updateProfile(
      String fullName,
      String phone,
      String shiftType,
      String shiftStart,
      String shiftEnd,
      String timeZone) {
    require(fullName != null && !fullName.isBlank(), "Full name is required");
    require(phone != null && phone.trim().length() >= 6, "Valid phone number is required");
    String nextTz = timeZone != null ? validateTimeZone(timeZone) : this.timeZone;
    String nextShiftType = shiftType != null ? shiftType : this.shiftType;
    String nextShiftStart = shiftStart != null ? shiftStart : this.shiftStart;
    String nextShiftEnd = shiftEnd != null ? shiftEnd : this.shiftEnd;
    validateShift(this.role, nextShiftType, nextShiftStart, nextShiftEnd);

    this.fullName = fullName.trim();
    this.phone = phone.trim();
    this.timeZone = nextTz;
    if ("WORKER".equals(this.role)) {
      this.shiftType = nextShiftType;
      this.shiftStart = nextShiftStart;
      this.shiftEnd = nextShiftEnd;
    }
    registerEvent(new ProfileUpdatedEvent(this.id, this.fullName, Instant.now()));
  }

  private static String validateTimeZone(String value) {
    String tz = (value == null || value.isBlank()) ? "America/Lima" : value.trim();
    try {
      ZoneId.of(tz);
    } catch (Exception ex) {
      throw new com.noctiva.noxway.shared.domain.RuleViolation("Invalid time zone: " + tz);
    }
    return tz;
  }

  private static void validateShift(
      String role, String shiftType, String shiftStart, String shiftEnd) {
    if (!"WORKER".equals(role)) {
      return;
    }
    require(shiftType != null && ALLOWED_SHIFTS.contains(shiftType), "Worker shift type is invalid");
    parseClock(shiftStart, "Invalid shift start time");
    parseClock(shiftEnd, "Invalid shift end time");
  }

  private static void parseClock(String value, String error) {
    require(value != null && !value.isBlank(), error);
    try {
      LocalTime.parse(value);
    } catch (Exception ex) {
      throw new com.noctiva.noxway.shared.domain.RuleViolation(error);
    }
  }

  public UUID getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getFullName() {
    return fullName;
  }

  public String getPhone() {
    return phone;
  }

  public String getRole() {
    return role;
  }

  public String getShiftType() {
    return shiftType;
  }

  public String getShiftStart() {
    return shiftStart;
  }

  public String getShiftEnd() {
    return shiftEnd;
  }

  public String getTimeZone() {
    return timeZone;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public boolean isActive() {
    return active;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public long getVersion() {
    return version;
  }
}
