package com.library.borrowing.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/** Corps de la requete POST /api/borrowings. dueDate est optionnel. */
public record BorrowingRequest(
        @NotNull(message = "bookId est obligatoire") Long bookId,
        @NotNull(message = "memberId est obligatoire") Long memberId,
        LocalDate dueDate) {
}
