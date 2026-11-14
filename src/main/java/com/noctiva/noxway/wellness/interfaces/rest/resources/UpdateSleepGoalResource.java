package com.noctiva.noxway.wellness.interfaces.rest.resources;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record UpdateSleepGoalResource(@Min(240) @Max(720) int targetMinutes) {}
