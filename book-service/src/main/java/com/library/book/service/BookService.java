package com.library.book.service;

import com.library.book.client.BorrowingClient;                  // NOUVEAU
import com.library.book.exception.ConflictException;             // NOUVEAU
import com.library.book.exception.ResourceNotFoundException;
import com.library.book.model.Book;
import com.library.book.repository.BookRepository;
import org.springframework.stereotype.Service;
import com.library.book.client.ReviewClient;
import com.library.book.dto.BookDetailsResponse;
import com.library.book.dto.ReviewDto;
import com.library.book.event.BookEventPublisher;

import java.util.List;

@Service
public class BookService {
    private ReviewClient reviewClient;
    private final BookRepository repository;
    private final BorrowingClient borrowingClient;
    private final BookEventPublisher eventPublisher;

    public BookService(BookRepository repository,
                       BorrowingClient borrowingClient,
                       ReviewClient reviewClient,
                       BookEventPublisher eventPublisher) {
        this.repository = repository;
        this.borrowingClient = borrowingClient;
        this.reviewClient = reviewClient;
        this.eventPublisher = eventPublisher;
    }

    public List<Book> findAll() {
        return repository.findAll();
    }

    public Book findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livre introuvable (id=" + id + ")"));
    }
    public BookDetailsResponse getDetails(Long id) {
        Book book = findById(id);                                    // 404 si le livre n'existe pas
        List<ReviewDto> reviews = reviewClient.getReviewsByBook(id); // appel synchrone vers review-service
        double average = reviews.stream().mapToInt(ReviewDto::rating).average().orElse(0.0);
        average = Math.round(average * 10.0) / 10.0;
        return new BookDetailsResponse(book, reviews, average, reviews.size());
    }

    public Book create(Book book) {
        book.setId(null);
        Book saved = repository.save(book);
        eventPublisher.publish("BookCreated", "book.created", saved);   // NOUVEAU
        return saved;
    }

    public Book update(Long id, Book data) {
        Book existing = findById(id);
        existing.setTitle(data.getTitle());
        existing.setAuthor(data.getAuthor());
        existing.setCategory(data.getCategory());
        existing.setPublicationYear(data.getPublicationYear());
        existing.setAvailable(data.isAvailable());
        Book saved = repository.save(existing);
        eventPublisher.publish("BookUpdated", "book.updated", saved);   // NOUVEAU
        return saved;
    }

    public void delete(Long id) {
        Book existing = findById(id);

        // NOUVEAU : appel synchrone vers borrowing-service (via Feign)
        boolean borrowed = borrowingClient.getAllBorrowings().stream()
                .anyMatch(b -> id.equals(b.bookId()) && "BORROWED".equals(b.status()));
        if (borrowed) {
            throw new ConflictException(
                    "Impossible de supprimer le livre (id=" + id + ") : il est actuellement emprunte");
        }

        repository.delete(existing);
        eventPublisher.publish("BookDeleted", "book.deleted", existing); // NOUVEAU
    }
}