package com.library.borrowing.dto;

/** Corps envoye a Notification Service (POST /api/notifications). */
public record NotificationRequest(Long memberId, String message) {
}
