package com.noctiva.noxway.commuting.interfaces.rest.resources;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record DelayCommuteResource(@Min(1) @Max(120) int minutes) {}
