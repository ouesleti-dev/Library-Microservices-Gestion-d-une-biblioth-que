package com.library.borrowing.service;

import com.library.borrowing.client.BookClient;
import com.library.borrowing.client.MemberClient;
import com.library.borrowing.client.NotificationClient;
import com.library.borrowing.dto.BookDto;
import com.library.borrowing.dto.BorrowingRequest;
import com.library.borrowing.dto.BorrowingUpdateRequest;
import com.library.borrowing.dto.MemberDto;
import com.library.borrowing.exception.ConflictException;
import com.library.borrowing.exception.ResourceNotFoundException;
import com.library.borrowing.model.Borrowing;
import com.library.borrowing.repository.BorrowingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BorrowingService {

    private final BorrowingRepository repository;
    private final BookClient bookClient;
    private final MemberClient memberClient;
    private final NotificationClient notificationClient;
    private final int defaultDays;

    public BorrowingService(BorrowingRepository repository,
                            BookClient bookClient,
                            MemberClient memberClient,
                            NotificationClient notificationClient,
                            @Value("${borrowing.default-days}") int defaultDays) {
        this.repository = repository;
        this.bookClient = bookClient;
        this.memberClient = memberClient;
        this.notificationClient = notificationClient;
        this.defaultDays = defaultDays;
    }

    public List<Borrowing> findAll() {
        return repository.findAll();
    }

    public Borrowing findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Emprunt introuvable (id=" + id + ")"));
    }

    /** Cree un emprunt : verifie livre + membre, rend le livre indisponible, notifie. */
    public Borrowing create(BorrowingRequest request) {
        // 1) Borrowing -> Book Service : le livre existe-t-il et est-il disponible ?
        BookDto book = bookClient.getBook(request.bookId());
        if (!book.available()) {
            throw new ConflictException("Le livre '" + book.title() + "' n'est pas disponible");
        }

        // 2) Borrowing -> Member Service : le membre existe-t-il ?
        MemberDto member = memberClient.getMember(request.memberId());

        // 3) Borrowing -> Book Service : le livre devient indisponible
        bookClient.updateAvailability(book, false);

        // 4) Enregistrement local de l'emprunt
        Borrowing borrowing = new Borrowing();
        borrowing.setBookId(book.id());
        borrowing.setMemberId(member.id());
        borrowing.setBorrowingDate(LocalDate.now());
        borrowing.setDueDate(request.dueDate() != null
                ? request.dueDate()
                : LocalDate.now().plusDays(defaultDays));
        borrowing.setStatus(Borrowing.STATUS_BORROWED);

        Borrowing saved;
        try {
            saved = repository.save(borrowing);
        } catch (RuntimeException e) {
            bookClient.updateAvailability(book, true); // on annule la reservation du livre
            throw e;
        }

        // 5) Borrowing -> Notification Service
        notificationClient.send(member.id(),
                "Bonjour " + member.firstName() + ", vous avez emprunte le livre '" + book.title()
                        + "'. Date de retour prevue : " + saved.getDueDate() + ".");
        return saved;
    }

    /** Modifie uniquement la date d'echeance. */
    public Borrowing update(Long id, BorrowingUpdateRequest request) {
        Borrowing borrowing = findById(id);
        borrowing.setDueDate(request.dueDate());
        return repository.save(borrowing);
    }

    /** Retour d'un livre : le livre redevient disponible et le membre est notifie. */
    public Borrowing returnBook(Long id) {
        Borrowing borrowing = findById(id);
        if (Borrowing.STATUS_RETURNED.equals(borrowing.getStatus())) {
            throw new ConflictException("Cet emprunt a deja ete retourne");
        }

        BookDto book = bookClient.getBook(borrowing.getBookId());
        bookClient.updateAvailability(book, true);

        borrowing.setReturnDate(LocalDate.now());
        borrowing.setStatus(Borrowing.STATUS_RETURNED);
        Borrowing saved = repository.save(borrowing);

        notificationClient.send(saved.getMemberId(),
                "Le livre '" + book.title() + "' a bien ete retourne. Merci !");
        return saved;
    }

    /** Supprime un emprunt. S'il etait en cours, le livre est remis disponible (au mieux). */
    public void delete(Long id) {
        Borrowing borrowing = findById(id);
        if (Borrowing.STATUS_BORROWED.equals(borrowing.getStatus())) {
            try {
                BookDto book = bookClient.getBook(borrowing.getBookId());
                bookClient.updateAvailability(book, true);
            } catch (ResourceNotFoundException e) {
                // le livre n'existe plus : rien a remettre a jour
            }
        }
        repository.delete(borrowing);
    }
}
