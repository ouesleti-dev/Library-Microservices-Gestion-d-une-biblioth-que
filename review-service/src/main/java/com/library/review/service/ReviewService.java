package com.library.review.service;

import com.library.review.client.BookClient;
import com.library.review.client.MemberClient;
import com.library.review.dto.ReviewRequest;
import com.library.review.exception.ResourceNotFoundException;
import com.library.review.model.Review;
import com.library.review.repository.ReviewRepository;
import feign.FeignException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository repository;
    private final BookClient bookClient;
    private final MemberClient memberClient;

    public ReviewService(ReviewRepository repository, BookClient bookClient, MemberClient memberClient) {
        this.repository = repository;
        this.bookClient = bookClient;
        this.memberClient = memberClient;
    }

    public List<Review> findAll() {
        return repository.findAll();
    }

    public List<Review> findByBook(Long id) {
        return repository.findByBookId(id);
    }

    public Review findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avis introuvable (id=" + id + ")"));
    }

    public Review create(ReviewRequest request) {
        verifyBook(request.bookId());
        verifyMember(request.memberId());

        Review review = new Review();
        review.setBookId(request.bookId());
        review.setMemberId(request.memberId());
        review.setRating(request.rating());
        review.setComment(request.comment());
        return repository.save(review);
    }

    public Review update(Long id, ReviewRequest request) {
        Review review = findById(id);
        verifyBook(request.bookId());
        verifyMember(request.memberId());
        review.setRating(request.rating());
        review.setComment(request.comment());
        return repository.save(review);
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }

    private void verifyBook(Long id) {
        try {
            bookClient.getBook(id);
        } catch (FeignException.NotFound exception) {
            throw new ResourceNotFoundException("Livre introuvable (id=" + id + ")");
        }
    }

    private void verifyMember(Long id) {
        try {
            memberClient.getMember(id);
        } catch (FeignException.NotFound exception) {
            throw new ResourceNotFoundException("Membre introuvable (id=" + id + ")");
        }
    }
}
