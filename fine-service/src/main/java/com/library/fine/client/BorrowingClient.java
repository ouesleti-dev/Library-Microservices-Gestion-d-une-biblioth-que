package com.library.fine.client;

import com.library.fine.dto.BorrowingDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "borrowing-service")
public interface BorrowingClient {
    @GetMapping("/api/borrowings/{id}")
    BorrowingDto getBorrowing(@PathVariable("id") Long id);
}