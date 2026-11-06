package com.noctiva.noxway.shared.domain;

public class RuleViolation extends RuntimeException {
  public RuleViolation(String message) {
    super(message);
  }
}
