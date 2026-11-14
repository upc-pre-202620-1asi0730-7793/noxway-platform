package com.noctiva.noxway.shared.domain.model.valueobjects;

import static com.noctiva.noxway.shared.domain.Rules.require;

import java.util.Locale;

/**
 * Normalized email address value object.
 *
 * @param value lower-cased trimmed email address
 */
public record EmailAddress(String value) {
  public EmailAddress {
    require(value != null && !value.isBlank(), "Email is required");
    String normalized = value.trim().toLowerCase(Locale.ROOT);
    int at = normalized.indexOf('@');
    require(at > 0 && at < normalized.length() - 3 && normalized.indexOf('.', at) > at + 1, "Invalid email format");
    value = normalized;
  }
}
