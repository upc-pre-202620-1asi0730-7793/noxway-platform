package com.noctiva.noxway.subscriptions.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SubscribeResource(@NotNull UUID planId) {}
