package com.library.borrowing.dto;

/** Copie locale de la structure d'un membre renvoyee par Member Service. */
public record MemberDto(Long id,
                        String firstName,
                        String lastName,
                        String email,
                        String phone) {
}
