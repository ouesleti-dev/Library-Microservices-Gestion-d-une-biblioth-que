package com.library.borrowing.client;

import com.library.borrowing.dto.MemberDto;
import com.library.borrowing.exception.ResourceNotFoundException;
import com.library.borrowing.exception.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

/** Appels HTTP REST vers Member Service (port 8082). */
@Component
public class MemberClient {

    private final RestClient restClient;
    private final String baseUrl;

    public MemberClient(@Value("${services.member.url}") String baseUrl,
                        @Value("${services.timeout-ms}") int timeoutMs) {
        this.baseUrl = baseUrl;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeoutMs);
        factory.setReadTimeout(timeoutMs);
        this.restClient = RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
    }

    /** GET /api/members/{id} : verifie que le membre existe et le renvoie. */
    public MemberDto getMember(Long id) {
        try {
            return restClient.get()
                    .uri("/api/members/{id}", id)
                    .retrieve()
                    .body(MemberDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Membre introuvable (id=" + id + ")");
        } catch (ResourceAccessException e) {
            throw new ServiceUnavailableException("Member Service est indisponible (" + baseUrl + ")");
        } catch (RestClientResponseException e) {
            throw new ServiceUnavailableException(
                    "Member Service a repondu avec une erreur HTTP " + e.getStatusCode().value());
        }
    }
}
