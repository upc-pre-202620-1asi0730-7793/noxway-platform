package com.noctiva.noxway.shared.domain;

import java.util.*;

public interface DomainRepository<T> {
  Optional<T> findById(UUID id);

  List<T> findAll();

  T save(T aggregate);

  void delete(UUID id);
}
