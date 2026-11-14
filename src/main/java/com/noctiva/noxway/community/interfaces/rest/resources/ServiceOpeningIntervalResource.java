package com.noctiva.noxway.community.interfaces.rest.resources;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ServiceOpeningIntervalResource(
    @Min(1) @Max(7) int dayOfWeek,
    @NotBlank String opensAt,
    @NotBlank String closesAt,
    boolean closesNextDay,
    boolean is24Hours) {}
