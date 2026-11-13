package com.noctiva.noxway.profiles.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateTrustLinkResource(@Email @NotBlank String contactEmail) {}
