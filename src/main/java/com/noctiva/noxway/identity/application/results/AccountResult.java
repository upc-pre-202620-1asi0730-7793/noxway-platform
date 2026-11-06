package com.noctiva.noxway.identity.application.results;

import com.noctiva.noxway.identity.domain.model.aggregates.Account;
import java.util.UUID;

public record AccountResult(
    UUID id,
    String email,
    String fullName,
    String phone,
    String role,
    String shiftType,
    String shiftStart,
    String shiftEnd,
    String timeZone,
    boolean active) {
  public static AccountResult from(Account a) {
    return new AccountResult(
        a.id(),
        a.email(),
        a.fullName(),
        a.phone(),
        a.role(),
        a.shiftType(),
        a.shiftStart(),
        a.shiftEnd(),
        a.timeZone(),
        a.active());
  }
}
