package com.noctiva.noxway.community.interfaces.acl;

import java.util.Optional;
import java.util.UUID;

public interface CommunityModeration {

  void decide(String targetType, UUID targetId, boolean approved);

  boolean exists(String type, UUID id);

  default Optional<TargetSummary> summarize(String targetType, UUID targetId) {
    return Optional.empty();
  }

  record TargetSummary(UUID id, String targetType, String title, String status) {}
}
