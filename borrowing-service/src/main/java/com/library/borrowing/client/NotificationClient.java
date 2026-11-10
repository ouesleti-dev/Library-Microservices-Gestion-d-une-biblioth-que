package com.library.borrowing.client;

import com.library.borrowing.dto.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/** Notification Service (Node.js) : un échec ne doit jamais bloquer l'emprunt. */
@FeignClient(name = "notification-service")
public interface NotificationClient {

    Logger log = LoggerFactory.getLogger(NotificationClient.class);

    /** POST /api/notifications */
    @PostMapping("/api/notifications")
    void post(@RequestBody NotificationRequest request);

    default void send(Long memberId, String message) {
        try {
            post(new NotificationRequest(memberId, message));
        } catch (Exception e) {
            log.warn("Notification non envoyee (Notification Service indisponible ?) : {}", e.getMessage());
        }
    }
}