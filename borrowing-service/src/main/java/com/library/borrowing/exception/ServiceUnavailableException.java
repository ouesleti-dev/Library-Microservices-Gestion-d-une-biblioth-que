package com.library.borrowing.exception;

/** Un autre microservice est injoignable ou en erreur -> HTTP 503 */
public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) {
        super(message);
    }
}
