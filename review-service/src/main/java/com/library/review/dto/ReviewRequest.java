package com.library.review.dto;
import jakarta.validation.constraints.*;
public record ReviewRequest(@NotNull Long bookId,@NotNull Long memberId,@Min(1) @Max(5) int rating,@NotBlank String comment){}
