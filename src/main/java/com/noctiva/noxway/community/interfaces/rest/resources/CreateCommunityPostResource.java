package com.noctiva.noxway.community.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommunityPostResource(
    @NotBlank @Size(max = 150) String title,
    @NotBlank @Size(max = 2000) String content,
    @NotBlank @Size(max = 100) String district,
    @Size(max = 80) String category,
    Double latitude,
    Double longitude) {}
