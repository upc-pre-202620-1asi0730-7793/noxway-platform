package com.noctiva.noxway.profiles.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record AccountProfileResource(
    UUID id,
    String email,
    String fullName,
    String phone,
    String role,
    String shiftType,
    String shiftStart,
    String shiftEnd,
    String timeZone,
    boolean active,
    Instant createdAt) {}
