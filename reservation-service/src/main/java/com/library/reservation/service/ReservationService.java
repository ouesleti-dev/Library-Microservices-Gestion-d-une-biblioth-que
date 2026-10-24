package com.library.reservation.service;

import com.library.reservation.client.LibraryClient;
import com.library.reservation.dto.ReservationRequest;
import com.library.reservation.exception.ResourceNotFoundException;
import com.library.reservation.model.Reservation;
import com.library.reservation.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationService {
    private final ReservationRepository repository;
    private final LibraryClient client;
    public ReservationService(ReservationRepository repository, LibraryClient client) {
        this.repository = repository; this.client = client;
    }
    public List<Reservation> findAll() { return repository.findAll(); }
    public List<Reservation> findByMember(Long memberId) { return repository.findByMemberId(memberId); }
    public Reservation findById(Long id) { return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Reservation introuvable (id=" + id + ")")); }
    public Reservation create(ReservationRequest request) {
        client.verifyBook(request.bookId()); client.verifyMember(request.memberId());
        Reservation reservation = new Reservation();
        reservation.setBookId(request.bookId()); reservation.setMemberId(request.memberId());
        reservation.setReservationDate(LocalDate.now());
        return repository.save(reservation);
    }
    public Reservation cancel(Long id) {
        Reservation reservation = findById(id); reservation.setStatus(Reservation.CANCELLED);
        return repository.save(reservation);
    }
    public void delete(Long id) { repository.delete(findById(id)); }
}
