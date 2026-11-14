package com.noctiva.noxway.community.interfaces.rest.resources;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record SubmitServicePointResource(
    @NotBlank @Size(max = 150) String name,
    @Pattern(regexp = "PHARMACY|GAS_STATION|HOSPITAL|POLICE_STATION|OTHER") @NotNull String type,
    double latitude,
    double longitude,
    @NotBlank String timeZone,
    @NotEmpty @Size(max = 28) List<@Valid ServiceOpeningIntervalResource> openingHours) {}
