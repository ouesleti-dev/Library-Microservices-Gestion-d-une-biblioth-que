
package com.library.review.controller;

import com.library.review.dto.ReviewRequest;
import com.library.review.model.Review;
import com.library.review.service.ReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Reviews", description = "Gestion des avis")
public class ReviewController {

    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    @GetMapping
    public List<Review> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Review getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/book/{bookId}")
    public List<Review> getByBook(@PathVariable Long bookId) {
        return service.findByBook(bookId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review create(@Valid @RequestBody ReviewRequest r) {
        return service.create(r);
    }

    @PutMapping("/{id}")
    public Review update(@PathVariable Long id, @Valid @RequestBody ReviewRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
