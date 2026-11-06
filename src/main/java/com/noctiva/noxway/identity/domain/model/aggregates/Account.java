package com.noctiva.noxway.identity.domain.model.aggregates;

import java.time.Instant;
import java.util.UUID;

public record Account(
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
  public static Account create(
      String email,
      String fullName,
      String phone,
      String role,
      String shiftType,
      String shiftStart,
      String shiftEnd,
      String timeZone,
      String passwordHash,
      boolean active) {
    return new Account(
        UUID.randomUUID(),
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
        Instant.now(),
        0);
  }

  public Account withEmail(String value) {
    return new Account(
        id,
        value,
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

  public Account withFullName(String value) {
    return new Account(
        id,
        email,
        value,
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

  public Account withPhone(String value) {
    return new Account(
        id,
        email,
        fullName,
        value,
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

  public Account withRole(String value) {
    return new Account(
        id,
        email,
        fullName,
        phone,
        value,
        shiftType,
        shiftStart,
        shiftEnd,
        timeZone,
        passwordHash,
        active,
        createdAt,
        version);
  }

  public Account withShiftType(String value) {
    return new Account(
        id,
        email,
        fullName,
        phone,
        role,
        value,
        shiftStart,
        shiftEnd,
        timeZone,
        passwordHash,
        active,
        createdAt,
        version);
  }

  public Account withShiftStart(String value) {
    return new Account(
        id,
        email,
        fullName,
        phone,
        role,
        shiftType,
        value,
        shiftEnd,
        timeZone,
        passwordHash,
        active,
        createdAt,
        version);
  }

  public Account withShiftEnd(String value) {
    return new Account(
        id,
        email,
        fullName,
        phone,
        role,
        shiftType,
        shiftStart,
        value,
        timeZone,
        passwordHash,
        active,
        createdAt,
        version);
  }

  public Account withTimeZone(String value) {
    return new Account(
        id,
        email,
        fullName,
        phone,
        role,
        shiftType,
        shiftStart,
        shiftEnd,
        value,
        passwordHash,
        active,
        createdAt,
        version);
  }

  public Account withPasswordHash(String value) {
    return new Account(
        id,
        email,
        fullName,
        phone,
        role,
        shiftType,
        shiftStart,
        shiftEnd,
        timeZone,
        value,
        active,
        createdAt,
        version);
  }

  public Account withActive(boolean value) {
    return new Account(
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
        value,
        createdAt,
        version);
  }
}
