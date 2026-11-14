package com.noctiva.noxway.subscriptions.interfaces.acl;

import java.util.UUID;

public interface ContactAllowance {
  int maxContacts(UUID worker);
}
