package com.hello.borad.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PostCreateRequest(@NotBlank String title, @NotBlank String content) {
}
