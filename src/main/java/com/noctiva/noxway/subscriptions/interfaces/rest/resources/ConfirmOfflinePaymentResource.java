package com.noctiva.noxway.subscriptions.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConfirmOfflinePaymentResource(@NotBlank @Size(max = 120) String reference) {}
