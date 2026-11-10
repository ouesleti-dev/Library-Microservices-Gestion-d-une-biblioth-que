package com.library.borrowing.exception;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

@Component
public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        boolean book = methodKey.startsWith("BookClient");
        boolean member = methodKey.startsWith("MemberClient");

        if (response.status() == 404) {
            if (book)   return new ResourceNotFoundException("Livre introuvable");
            if (member) return new ResourceNotFoundException("Membre introuvable");
        }
        String service = book ? "Book Service" : member ? "Member Service" : "Service distant";
        return new ServiceUnavailableException(
                service + " a repondu avec une erreur HTTP " + response.status());
    }
}