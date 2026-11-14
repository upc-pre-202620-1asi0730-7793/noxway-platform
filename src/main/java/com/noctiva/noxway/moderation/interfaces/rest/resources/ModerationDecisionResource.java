package com.noctiva.noxway.moderation.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ModerationDecisionResource(
    @Pattern(regexp = "APPROVE|REJECT") @NotNull String decision,
    @NotBlank @Size(max = 1000) String notes) {}
