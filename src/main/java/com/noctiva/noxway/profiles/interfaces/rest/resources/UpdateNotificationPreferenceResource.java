package com.noctiva.noxway.profiles.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record UpdateNotificationPreferenceResource(
    @NotBlank String channel,
    boolean enabled,
    @NotBlank String priorityStart,
    @NotBlank String priorityEnd,
    @NotBlank String timeZone) {}
