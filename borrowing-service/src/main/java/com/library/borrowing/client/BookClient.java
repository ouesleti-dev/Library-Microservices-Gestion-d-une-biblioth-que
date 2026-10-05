package com.library.borrowing.client;

import com.library.borrowing.dto.BookDto;
import com.library.borrowing.exception.ResourceNotFoundException;
import com.library.borrowing.exception.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

/** Appels HTTP REST vers Book Service (port 8081). */
@Component
public class BookClient {

    private final RestClient restClient;
    private final String baseUrl;

    public BookClient(@Value("${services.book.url}") String baseUrl,
                      @Value("${services.timeout-ms}") int timeoutMs) {
        this.baseUrl = baseUrl;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeoutMs);
        factory.setReadTimeout(timeoutMs);
        this.restClient = RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
    }

    /** GET /api/books/{id} : verifie que le livre existe et le renvoie. */
    public BookDto getBook(Long id) {
        try {
            return restClient.get()
                    .uri("/api/books/{id}", id)
                    .retrieve()
                    .body(BookDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Livre introuvable (id=" + id + ")");
        } catch (ResourceAccessException e) {
            throw unavailable();
        } catch (RestClientResponseException e) {
            throw new ServiceUnavailableException(
                    "Book Service a repondu avec une erreur HTTP " + e.getStatusCode().value());
        }
    }

    /** PUT /api/books/{id} : met a jour la disponibilite du livre. */
    public void updateAvailability(BookDto book, boolean available) {
        try {
            restClient.put()
                    .uri("/api/books/{id}", book.id())
                    .body(book.withAvailable(available))
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Livre introuvable (id=" + book.id() + ")");
        } catch (ResourceAccessException e) {
            throw unavailable();
        } catch (RestClientResponseException e) {
            throw new ServiceUnavailableException(
                    "Book Service a repondu avec une erreur HTTP " + e.getStatusCode().value());
        }
    }

    private ServiceUnavailableException unavailable() {
        return new ServiceUnavailableException("Book Service est indisponible (" + baseUrl + ")");
    }
}
