package com.noctiva.noxway.profiles.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateProfileResource(
    @Email @NotBlank String email,
    @NotBlank String fullName,
    @NotBlank String phone,
    @NotBlank String role,
    String shiftType,
    String shiftStart,
    String shiftEnd,
    String timeZone) {}
