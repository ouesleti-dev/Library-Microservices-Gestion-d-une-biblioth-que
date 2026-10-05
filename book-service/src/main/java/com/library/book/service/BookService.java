package com.library.book.service;

import com.library.book.exception.ResourceNotFoundException;
import com.library.book.model.Book;
import com.library.book.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public List<Book> findAll() {
        return repository.findAll();
    }

    public Book findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livre introuvable (id=" + id + ")"));
    }

    public Book create(Book book) {
        book.setId(null);
        return repository.save(book);
    }

    public Book update(Long id, Book data) {
        Book existing = findById(id);
        existing.setTitle(data.getTitle());
        existing.setAuthor(data.getAuthor());
        existing.setCategory(data.getCategory());
        existing.setPublicationYear(data.getPublicationYear());
        existing.setAvailable(data.isAvailable());
        return repository.save(existing);
    }

    public void delete(Long id) {
        Book existing = findById(id);
        repository.delete(existing);
    }
}
