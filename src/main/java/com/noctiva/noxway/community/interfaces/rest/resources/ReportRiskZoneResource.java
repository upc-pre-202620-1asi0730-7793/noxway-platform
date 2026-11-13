package com.noctiva.noxway.community.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ReportRiskZoneResource(
    @NotBlank @Size(max = 1000) String description,
    @Pattern(regexp = "LOW|MEDIUM|HIGH") @NotNull String riskLevel,
    double latitude,
    double longitude) {}
