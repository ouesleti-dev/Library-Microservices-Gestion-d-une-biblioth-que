package com.library.borrowing.client;

import com.library.borrowing.dto.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Appels HTTP REST vers Notification Service (Node.js, port 8084).
 * Une notification est secondaire : si le service est indisponible, l'emprunt
 * ou le retour reste valide et on se contente d'ecrire un avertissement dans les logs.
 */
@Component
public class NotificationClient {

    private static final Logger log = LoggerFactory.getLogger(NotificationClient.class);

    private final RestClient restClient;

    public NotificationClient(@Value("${services.notification.url}") String baseUrl,
                              @Value("${services.timeout-ms}") int timeoutMs) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeoutMs);
        factory.setReadTimeout(timeoutMs);
        this.restClient = RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
    }

    /** POST /api/notifications */
    public void send(Long memberId, String message) {
        try {
            restClient.post()
                    .uri("/api/notifications")
                    .body(new NotificationRequest(memberId, message))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("Notification non envoyee (Notification Service indisponible ?) : {}", e.getMessage());
        }
    }
}
