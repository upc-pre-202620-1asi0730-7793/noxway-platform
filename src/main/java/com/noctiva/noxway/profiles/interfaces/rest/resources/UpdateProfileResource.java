package com.noctiva.noxway.profiles.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record UpdateProfileResource(
    @NotBlank String fullName,
    @NotBlank String phone,
    String shiftType,
    String shiftStart,
    String shiftEnd,
    String timeZone) {}
