package com.noctiva.noxway.subscriptions.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateBenefitResource(
    @NotBlank @Size(max = 120) String name,
    @NotBlank @Size(max = 500) String description,
    @NotBlank @Size(max = 60) String planCode) {}
