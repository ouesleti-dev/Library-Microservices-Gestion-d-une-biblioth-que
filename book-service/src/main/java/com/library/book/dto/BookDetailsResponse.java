package com.library.book.dto;

import com.library.book.model.Book;

import java.util.List;

public record BookDetailsResponse(Book book,
                                  List<ReviewDto> reviews,
                                  double averageRating,
                                  int reviewCount) {
}