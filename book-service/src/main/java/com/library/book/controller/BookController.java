package com.library.book.controller;

import com.library.book.model.Book;
import com.library.book.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "Gestion des livres de la bibliotheque")
public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lister tous les livres")
    public List<Book> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Recuperer un livre par son id")
    public Book getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creer un livre")
    public Book create(@Valid @RequestBody Book book) {
        return service.create(book);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un livre")
    public Book update(@PathVariable Long id, @Valid @RequestBody Book book) {
        return service.update(id, book);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer un livre")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
