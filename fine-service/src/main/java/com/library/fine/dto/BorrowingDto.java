package com.library.fine.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BorrowingDto(Long id, Long bookId, Long memberId) {}