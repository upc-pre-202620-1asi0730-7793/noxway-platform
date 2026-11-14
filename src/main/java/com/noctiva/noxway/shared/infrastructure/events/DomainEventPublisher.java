package com.noctiva.noxway.shared.infrastructure.events;

import com.noctiva.noxway.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Publishes domain events registered on aggregate roots after persistence operations complete.
 */
@Component
public class DomainEventPublisher {

  private final ApplicationEventPublisher publisher;

  public DomainEventPublisher(ApplicationEventPublisher publisher) {
    this.publisher = publisher;
  }

  public void publishAndClearEvents(AbstractDomainAggregateRoot<?> aggregate) {
    if (aggregate == null) return;
    for (Object event : aggregate.pullDomainEvents()) {
      publisher.publishEvent(event);
    }
  }
}
