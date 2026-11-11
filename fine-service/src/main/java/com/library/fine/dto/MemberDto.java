package com.library.fine.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MemberDto(Long id, String firstName, String lastName, String email) {}