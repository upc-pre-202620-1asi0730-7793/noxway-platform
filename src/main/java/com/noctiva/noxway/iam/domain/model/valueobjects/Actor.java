package com.noctiva.noxway.iam.domain.model.valueobjects;

import java.util.UUID;

/**
 * Represents the currently resolved platform actor across request processing.
 *
 * @param id account identifier
 * @param role account role ({@code WORKER}, {@code TRUSTED_CONTACT}, {@code MODERATOR}, or {@code ADMIN})
 */
public record Actor(UUID id, String role) {
  public boolean isStaff() {
    return SecurityRole.ADMIN.name().equals(role) || SecurityRole.MODERATOR.name().equals(role);
  }
}
