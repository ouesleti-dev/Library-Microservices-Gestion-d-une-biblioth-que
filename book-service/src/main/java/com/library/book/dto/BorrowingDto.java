package com.library.book.dto;

import java.time.LocalDate;

public record BorrowingDto(Long id,
                           Long bookId,
                           Long memberId,
                           LocalDate borrowingDate,
                           LocalDate dueDate,
                           LocalDate returnDate,
                           String status) {
}