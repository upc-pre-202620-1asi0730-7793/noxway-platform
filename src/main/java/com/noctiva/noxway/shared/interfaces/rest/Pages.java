package com.noctiva.noxway.shared.interfaces.rest;

import java.util.*;

public record Pages<T>(List<T> items, int page, int size, long total) {
  public static <T> Pages<T> of(List<T> all, int page, int size) {
    if (page < 0 || size < 1 || size > 100)
      throw new com.noctiva.noxway.shared.domain.RuleViolation(
          "Page >= 0 and size 1..100 required");
    long offset = (long) page * size;
    int from = (int) Math.min(all.size(), offset);
    return new Pages<>(
        all.subList(from, Math.min(all.size(), from + size)), page, size, all.size());
  }
}
