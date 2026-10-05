package com.library.borrowing.controller;

import com.library.borrowing.dto.BorrowingRequest;
import com.library.borrowing.dto.BorrowingUpdateRequest;
import com.library.borrowing.model.Borrowing;
import com.library.borrowing.service.BorrowingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrowings")
@Tag(name = "Borrowings", description = "Gestion des emprunts et des retours")
public class BorrowingController {

    private final BorrowingService service;

    public BorrowingController(BorrowingService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lister tous les emprunts")
    public List<Borrowing> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Recuperer un emprunt par son id")
    public Borrowing getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creer un emprunt (verifie le livre et le membre via HTTP)")
    public Borrowing create(@Valid @RequestBody BorrowingRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier la date d'echeance d'un emprunt")
    public Borrowing update(@PathVariable Long id, @Valid @RequestBody BorrowingUpdateRequest request) {
        return service.update(id, request);
    }

    @PutMapping("/{id}/return")
    @Operation(summary = "Retourner un livre (le livre redevient disponible)")
    public Borrowing returnBook(@PathVariable Long id) {
        return service.returnBook(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer un emprunt")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
