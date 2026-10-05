package com.library.borrowing.exception;

/** Conflit metier (livre deja emprunte, deja retourne...) -> HTTP 409 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
