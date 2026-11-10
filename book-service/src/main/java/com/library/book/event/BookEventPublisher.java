package com.library.book.event;

import com.library.book.config.RabbitConfig;
import com.library.book.dto.BookEvent;
import com.library.book.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class BookEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(BookEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public BookEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(String eventType, String routingKey, Book book) {
        BookEvent event = new BookEvent(
                eventType,
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getCategory(),
                book.isAvailable(),
                Instant.now().toString());
        try {
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, routingKey, event);
            log.info("Evenement publie : {} (bookId={})", eventType, book.getId());
        } catch (Exception e) {
            // le CRUD ne doit pas échouer si RabbitMQ est indisponible
            log.error("Echec de publication de {} : {}", eventType, e.getMessage());
        }
    }
}