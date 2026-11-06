package com.noctiva.noxway.commuting.interfaces.acl;

import java.util.UUID;

public interface CommuteAccess {
  void requireCompletedOwn(UUID commuteId, UUID workerId);
}
