package com.noctiva.noxway.shared.interfaces.rest;

import com.noctiva.noxway.shared.interfaces.rest.resources.PagedResource;
import java.util.List;

public record Pages<T>(List<T> items, int page, int size, long total) {

  public static int clamp(int size) {
    return Math.max(1, Math.min(100, size));
  }

  public static <T> PagedResource<T> paginate(List<T> all, int page, int size) {
    int clampedPage = Math.max(0, page);
    int clampedSize = clamp(size);
    int from = Math.min(clampedPage * clampedSize, all.size());
    int to = Math.min(from + clampedSize, all.size());
    return new PagedResource<>(all.subList(from, to), clampedPage, clampedSize, all.size());
  }

  public static <T> Pages<T> of(List<T> all, int page, int size) {
    if (page < 0 || size < 1 || size > 100) {
      throw new com.noctiva.noxway.shared.domain.RuleViolation(
          "Page >= 0 and size 1..100 required");
    }
    long offset = (long) page * size;
    int from = (int) Math.min(all.size(), offset);
    return new Pages<>(
        all.subList(from, Math.min(all.size(), from + size)), page, size, all.size());
  }
}
