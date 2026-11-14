package com.noctiva.noxway.subscriptions.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UseReferralCodeResource(@NotBlank @Size(max = 40) String code) {}
