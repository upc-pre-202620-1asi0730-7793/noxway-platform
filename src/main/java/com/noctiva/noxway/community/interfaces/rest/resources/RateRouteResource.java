package com.noctiva.noxway.community.interfaces.rest.resources;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record RateRouteResource(
    @NotNull UUID commuteId, @Min(1) @Max(5) int score, @NotNull @Size(max = 1000) String comments) {}
