package com.noctiva.noxway.wellness.interfaces.rest.resources;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record LogRestSessionResource(
    @NotNull Instant startTime,
    @NotNull Instant endTime,
    @Min(1) @Max(5) int quality,
    String notes) {}
