package com.example.upback.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ImageRecordDTO(@NotNull String name, @NotBlank String image, @NotBlank String size, @NotBlank String type) {
}
