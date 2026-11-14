package com.noctiva.noxway.moderation.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record SubmitReportResource(
    @Pattern(regexp = "SERVICE|RISK") @NotNull String targetType,
    @NotNull UUID targetId,
    @NotBlank @Size(max = 1000) String reason) {}
