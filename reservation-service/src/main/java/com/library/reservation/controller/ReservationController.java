package com.library.reservation.controller;

import com.library.reservation.dto.ReservationRequest;
import com.library.reservation.model.Reservation;
import com.library.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservations", description = "Gestion des reservations")
public class ReservationController {
    private final ReservationService service;
    public ReservationController(ReservationService service) { this.service = service; }
    @GetMapping public List<Reservation> getAll() { return service.findAll(); }
    @GetMapping("/{id}") public Reservation getById(@PathVariable Long id) { return service.findById(id); }
    @GetMapping("/member/{memberId}") public List<Reservation> getByMember(@PathVariable Long memberId) { return service.findByMember(memberId); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) @Operation(summary = "Creer une reservation")
    public Reservation create(@Valid @RequestBody ReservationRequest request) { return service.create(request); }
    @PutMapping("/{id}/cancel") public Reservation cancel(@PathVariable Long id) { return service.cancel(id); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id) { service.delete(id); }
}
