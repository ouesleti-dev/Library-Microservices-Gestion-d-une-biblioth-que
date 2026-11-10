package com.library.borrowing.client;

import com.library.borrowing.dto.BookDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/** Appels REST synchrones vers Book Service via OpenFeign (résolu par Eureka). */
@FeignClient(name = "book-service")
public interface BookClient {

    /** GET /api/books/{id} */
    @GetMapping("/api/books/{id}")
    BookDto getBook(@PathVariable("id") Long id);

    /** PUT /api/books/{id} */
    @PutMapping("/api/books/{id}")
    void putBook(@PathVariable("id") Long id, @RequestBody BookDto book);

    /** Même signature qu'avant : BorrowingService n'est pas modifié. */
    default void updateAvailability(BookDto book, boolean available) {
        putBook(book.id(), book.withAvailable(available));
    }
}