package com.library.reservation.client;

import com.library.reservation.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class LibraryClient {
    private final RestClient books;
    private final RestClient members;
    public LibraryClient(@LoadBalanced RestClient.Builder builder,
                         @Value("${services.book.url}") String bookUrl,
                         @Value("${services.member.url}") String memberUrl) {
        books = builder.baseUrl(bookUrl).build();
        members = builder.baseUrl(memberUrl).build();
    }
    public void verifyBook(Long id) { verify(books, "/api/books/" + id, "Livre", id); }
    public void verifyMember(Long id) { verify(members, "/api/members/" + id, "Membre", id); }
    private void verify(RestClient client, String uri, String label, Long id) {
        try { client.get().uri(uri).retrieve().toBodilessEntity(); }
        catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 404) throw new ResourceNotFoundException(label + " introuvable (id=" + id + ")");
            throw e;
        }
    }
}
