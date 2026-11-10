package com.library.book.dto;

public record BookEvent(
        String eventType,     // BookCreated, BookUpdated ou BookDeleted
        Long bookId,
        String title,
        String author,
        String category,
        boolean available,
        String occurredAt) {}