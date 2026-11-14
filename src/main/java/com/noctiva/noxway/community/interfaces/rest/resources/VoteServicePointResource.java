package com.noctiva.noxway.community.interfaces.rest.resources;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record VoteServicePointResource(@Min(-1) @Max(1) int direction) {}
