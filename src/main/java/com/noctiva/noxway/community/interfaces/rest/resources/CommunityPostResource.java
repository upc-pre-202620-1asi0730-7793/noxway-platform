package com.noctiva.noxway.community.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record CommunityPostResource(
    UUID id,
    UUID authorId,
    String authorName,
    String authorRole,
    String title,
    String content,
    String district,
    String category,
    Double latitude,
    Double longitude,
    Instant createdAt) {}
