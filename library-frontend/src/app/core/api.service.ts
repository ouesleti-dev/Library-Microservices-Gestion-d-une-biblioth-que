import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URL } from './config';
import * as M from './models';

/** Tous les appels passent par l'API Gateway : /{service}/api/... */
@Injectable({ providedIn: 'root' })
export class ApiService {
  private http = inject(HttpClient);
  private u = (svc: string, path: string) => `${API_URL}/${svc}${path}`;

  // Livres
  books = () => this.http.get<M.Book[]>(this.u('book-service', '/api/books'));
  bookDetails = (id: number) => this.http.get<M.BookDetails>(this.u('book-service', `/api/books/${id}/details`));
  saveBook(b: M.Book): Observable<M.Book> {
    return b.id ? this.http.put<M.Book>(this.u('book-service', `/api/books/${b.id}`), b)
                : this.http.post<M.Book>(this.u('book-service', '/api/books'), b);
  }
  deleteBook = (id: number) => this.http.delete<void>(this.u('book-service', `/api/books/${id}`));

  // Membres
  members = () => this.http.get<M.Member[]>(this.u('member-service', '/api/members'));
  saveMember(m: M.Member): Observable<M.Member> {
    return m.id ? this.http.put<M.Member>(this.u('member-service', `/api/members/${m.id}`), m)
                : this.http.post<M.Member>(this.u('member-service', '/api/members'), m);
  }
  deleteMember = (id: number) => this.http.delete<void>(this.u('member-service', `/api/members/${id}`));

  // Emprunts
  borrowings = () => this.http.get<M.Borrowing[]>(this.u('borrowing-service', '/api/borrowings'));
  borrow = (bookId: number, memberId: number, dueDate: string | null) =>
    this.http.post<M.Borrowing>(this.u('borrowing-service', '/api/borrowings'), { bookId, memberId, dueDate: dueDate || null });
  updateDueDate = (id: number, dueDate: string) =>
    this.http.put<M.Borrowing>(this.u('borrowing-service', `/api/borrowings/${id}`), { dueDate });
  returnBook = (id: number) => this.http.put<M.Borrowing>(this.u('borrowing-service', `/api/borrowings/${id}/return`), {});
  deleteBorrowing = (id: number) => this.http.delete<void>(this.u('borrowing-service', `/api/borrowings/${id}`));

  // Réservations
  reservations = () => this.http.get<M.Reservation[]>(this.u('reservation-service', '/api/reservations'));
  reserve = (bookId: number, memberId: number) =>
    this.http.post<M.Reservation>(this.u('reservation-service', '/api/reservations'), { bookId, memberId });
  cancelReservation = (id: number) => this.http.put<M.Reservation>(this.u('reservation-service', `/api/reservations/${id}/cancel`), {});
  deleteReservation = (id: number) => this.http.delete<void>(this.u('reservation-service', `/api/reservations/${id}`));

  // Amendes
  fines = () => this.http.get<M.Fine[]>(this.u('fine-service', '/api/fines'));
  createFine = (memberId: number, borrowingId: number, amount: number, reason: string) =>
    this.http.post<M.Fine>(this.u('fine-service', '/api/fines'), { memberId, borrowingId, amount, reason });
  payFine = (id: number) => this.http.put<M.Fine>(this.u('fine-service', `/api/fines/${id}/pay`), {});
  deleteFine = (id: number) => this.http.delete<void>(this.u('fine-service', `/api/fines/${id}`));

  // Avis
  addReview = (bookId: number, memberId: number, rating: number, comment: string) =>
    this.http.post<M.Review>(this.u('review-service', '/api/reviews'), { bookId, memberId, rating, comment });
  deleteReview = (id: number) => this.http.delete<void>(this.u('review-service', `/api/reviews/${id}`));

  // Notifications (Node.js)
  notifications = (memberId?: number) =>
    this.http.get<M.AppNotification[]>(this.u('notification-service', '/api/notifications'),
      { params: memberId ? { memberId } : {} });
  notify = (memberId: number, message: string) =>
    this.http.post<M.AppNotification>(this.u('notification-service', '/api/notifications'), { memberId, message });

  // Statistiques (Python)
  stats = () => this.http.get<M.GlobalStats>(this.u('statistics-service', '/api/statistics'));
  bookStats = () => this.http.get<M.BookStats>(this.u('statistics-service', '/api/statistics/books'));
}
