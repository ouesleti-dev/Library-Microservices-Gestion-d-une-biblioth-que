
package com.library.reservation.client;

import com.library.reservation.exception.ResourceNotFoundException;
import feign.FeignException;
import org.springframework.stereotype.Component;

@Component
public class LibraryClient {

    private final BookClient bookClient;
    private final MemberClient memberClient;

    public LibraryClient(BookClient bookClient,
                         MemberClient memberClient) {
        this.bookClient = bookClient;
        this.memberClient = memberClient;
    }

    public void verifyBook(Long id) {
        try {
            bookClient.getBook(id);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException(
                    "Livre introuvable (id=" + id + ")"
            );
        }
    }

    public void verifyMember(Long id) {
        try {
            memberClient.getMember(id);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException(
                    "Membre introuvable (id=" + id + ")"
            );
        }
    }
}
