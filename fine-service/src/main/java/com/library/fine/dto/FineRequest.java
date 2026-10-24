package com.library.fine.dto;
import jakarta.validation.constraints.*;
public record FineRequest(@NotNull Long memberId, @NotNull Long borrowingId, @Positive double amount, @NotBlank String reason) {}
