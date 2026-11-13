package com.noctiva.noxway.iam.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpResource(
    @Email @NotBlank String email,
    @NotBlank @Size(min = 12, max = 72) String password,
    @NotBlank String fullName,
    @NotBlank String phone,
    @NotBlank String role,
    String shiftType,
    String shiftStart,
    String shiftEnd,
    String timeZone) {}
