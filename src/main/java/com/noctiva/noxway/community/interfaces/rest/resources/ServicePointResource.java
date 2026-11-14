package com.noctiva.noxway.community.interfaces.rest.resources;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ServicePointResource(
    UUID id,
    UUID reportedBy,
    String reporterName,
    String name,
    String type,
    double latitude,
    double longitude,
    String timeZone,
    String status,
    List<ServiceOpeningIntervalResource> openingHours,
    int trustScore,
    long upvotes,
    long downvotes,
    int myVote,
    boolean openAtReference,
    Instant referenceTime,
    Instant createdAt) {}
