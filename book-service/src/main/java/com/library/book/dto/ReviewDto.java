package com.library.book.dto;

public record ReviewDto(Long id,
                        Long bookId,
                        Long memberId,
                        int rating,
                        String comment) {
}