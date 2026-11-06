package com.noctiva.noxway.community.interfaces.acl;

import java.util.UUID;

public interface CommunityModeration {
  void decide(String targetType, UUID targetId, boolean approved);

  boolean exists(String type, UUID id);
}
