package com.noctiva.noxway.commuting.domain.services;

public interface AlertDeliveryGateway {
  enum Result {
    DELIVERED,
    UNCONFIGURED,
    FAILED
  }

  Result deliver(
      String channel, String email, String phone, String message, String eventId, boolean priority);
}
