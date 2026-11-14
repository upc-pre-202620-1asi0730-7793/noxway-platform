package com.noctiva.noxway.shared.domain.model.aggregates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pure domain base class for aggregate roots that accumulate domain events without framework coupling.
 *
 * @param <T> aggregate root subtype
 */
public abstract class AbstractDomainAggregateRoot<T extends AbstractDomainAggregateRoot<T>> {

  private final List<Object> domainEvents = new ArrayList<>();

  /**
   * Registers a domain event to be published after the aggregate is persisted.
   *
   * @param event the domain event instance
   * @param <E> event type
   * @return the registered event
   */
  protected <E> E registerEvent(E event) {
    if (event != null) {
      domainEvents.add(event);
    }
    return event;
  }

  /**
   * Returns an unmodifiable snapshot of pending domain events.
   *
   * @return pending domain events
   */
  public List<Object> getDomainEvents() {
    return Collections.unmodifiableList(domainEvents);
  }

  /**
   * Clears all accumulated domain events.
   */
  public void clearDomainEvents() {
    domainEvents.clear();
  }

  /**
   * Atomically returns accumulated events and clears internal buffer.
   *
   * @return snapshot of domain events that were pending
   */
  public List<Object> pullDomainEvents() {
    var snapshot = List.copyOf(domainEvents);
    domainEvents.clear();
    return snapshot;
  }
}
