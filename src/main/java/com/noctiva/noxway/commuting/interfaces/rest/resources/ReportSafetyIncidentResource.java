package com.noctiva.noxway.commuting.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ReportSafetyIncidentResource(
    @Pattern(regexp = "SOS|DELAY|DEVIATION|OTHER") @NotNull String type,
    @NotBlank @Size(max = 1000) String description) {}
