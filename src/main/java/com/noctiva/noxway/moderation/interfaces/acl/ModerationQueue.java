package com.noctiva.noxway.moderation.interfaces.acl;

import java.util.UUID;

public interface ModerationQueue {
  void submit(UUID reporter, String type, UUID target, String reason);
}
