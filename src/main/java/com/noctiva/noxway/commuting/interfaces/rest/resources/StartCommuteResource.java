package com.noctiva.noxway.commuting.interfaces.rest.resources;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record StartCommuteResource(
    @NotBlank @Size(max = 500) String origin,
    @NotBlank @Size(max = 500) String destination,
    @NotBlank @Size(max = 50) String transport,
    @NotNull Instant startTime,
    @NotNull Instant estimatedArrivalTime,
    @Min(0) @Max(120) int toleranceMinutes) {}
