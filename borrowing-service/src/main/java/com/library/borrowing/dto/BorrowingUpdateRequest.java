package com.library.borrowing.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/** Corps de la requete PUT /api/borrowings/{id} : on ne modifie que la date d'echeance. */
public record BorrowingUpdateRequest(
        @NotNull(message = "dueDate est obligatoire") LocalDate dueDate) {
}
