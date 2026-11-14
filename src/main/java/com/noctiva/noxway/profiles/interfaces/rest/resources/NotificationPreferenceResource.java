package com.noctiva.noxway.profiles.interfaces.rest.resources;

import java.util.UUID;

public record NotificationPreferenceResource(
    UUID id,
    UUID accountId,
    String channel,
    boolean enabled,
    String priorityStart,
    String priorityEnd,
    String timeZone) {}
