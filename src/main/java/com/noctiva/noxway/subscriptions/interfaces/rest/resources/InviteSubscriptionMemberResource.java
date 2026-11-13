package com.noctiva.noxway.subscriptions.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record InviteSubscriptionMemberResource(@NotBlank @Email String workerEmail) {}
