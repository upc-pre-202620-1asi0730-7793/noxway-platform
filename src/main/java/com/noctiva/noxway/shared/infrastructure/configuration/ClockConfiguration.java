package com.noctiva.noxway.shared.infrastructure.configuration;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides a centralized UTC {@link Clock} bean for deterministic temporal domain rules and tests.
 */
@Configuration
public class ClockConfiguration {

  @Bean
  public Clock clock() {
    return Clock.systemUTC();
  }
}
