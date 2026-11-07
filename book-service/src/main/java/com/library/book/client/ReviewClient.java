package com.library.book.client;

import com.library.book.dto.ReviewDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "review-service")
public interface ReviewClient {

    @GetMapping("/api/reviews/book/{bookId}")
    List<ReviewDto> getReviewsByBook(@PathVariable("bookId") Long bookId);
}