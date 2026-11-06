package com.noctiva.noxway.identity.interfaces.acl;

import java.util.*;

public interface IdentityAccess {
  void requireWorker(UUID id);

  void lockWorker(UUID id);

  UUID workerByEmail(String email);

  void requireAccount(UUID id);

  boolean canAccompany(UUID contact, UUID worker);

  List<UUID> contactsOf(UUID worker);

  List<String> channelsFor(UUID contact);

  record DeliveryContact(String email, String phone) {}

  DeliveryContact deliveryContact(UUID id);

  boolean priorityAt(UUID contact, String channel, java.time.Instant at);
}
