package com.library.book.client;

import com.library.book.dto.BorrowingDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "borrowing-service")
public interface BorrowingClient {

    @GetMapping("/api/borrowings")
    List<BorrowingDto> getAllBorrowings();
}