import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { LookupService } from '../core/lookup.service';
import { ToastService } from '../core/toast.service';
import { Reservation } from '../core/models';
import { Modal } from '../shared/modal';

@Component({
  selector: 'app-reservations',
  imports: [FormsModule, Modal],
  template: `
    <div class="page-head">
      <div><h1>Réservations</h1><p>{{ items().length }} réservation(s)</p></div>
      @if (auth.canWrite()) { <button class="btn primary" (click)="form.set({ bookId: null, memberId: null })">+ Réserver</button> }
    </div>
    <div class="table-wrap">
      <table>
        <thead><tr><th>Livre</th><th>Membre</th><th>Date</th><th>Statut</th><th></th></tr></thead>
        <tbody>
          @for (r of items(); track r.id) {
            <tr><td>{{ lookup.bookTitle(r.bookId) }}</td><td>{{ lookup.memberName(r.memberId) }}</td><td>{{ r.reservationDate }}</td>
              <td><span class="badge" [class.amber]="r.status === 'PENDING'" [class.grey]="r.status !== 'PENDING'">{{ r.status === 'PENDING' ? 'En attente' : 'Annulée' }}</span></td>
              <td class="actions">
                @if (r.status === 'PENDING' && auth.canWrite()) { <button class="btn sm" (click)="cancel(r)">Annuler</button> }
                @if (auth.isAdmin()) { <button class="btn sm danger" (click)="remove(r)">Supprimer</button> }
              </td></tr>
          } @empty { <tr><td colspan="5" class="empty">Aucune réservation.</td></tr> }
        </tbody>
      </table>
    </div>
    @if (form(); as f) {
      <app-modal title="Nouvelle réservation" (closed)="form.set(null)">
        <form (ngSubmit)="create()">
          <div class="field"><label for="b">Livre</label>
            <select id="b" name="b" [(ngModel)]="f.bookId" required><option [ngValue]="null" disabled>Choisir…</option>
              @for (b of lookup.books(); track b.id) { <option [ngValue]="b.id">{{ b.title }}{{ b.available ? '' : ' (emprunté)' }}</option> }</select></div>
          <div class="field"><label for="m">Membre</label>
            <select id="m" name="m" [(ngModel)]="f.memberId" required><option [ngValue]="null" disabled>Choisir…</option>
              @for (m of lookup.members(); track m.id) { <option [ngValue]="m.id">{{ m.firstName }} {{ m.lastName }}</option> }</select></div>
          <div style="display:flex;gap:8px;justify-content:flex-end;margin-top:18px">
            <button type="button" class="btn" (click)="form.set(null)">Annuler</button>
            <button class="btn primary" [disabled]="!f.bookId || !f.memberId">Réserver</button>
          </div>
        </form>
      </app-modal>
    }`
})
export class Reservations implements OnInit {
  private api = inject(ApiService);
  private toast = inject(ToastService);
  auth = inject(AuthService);
  lookup = inject(LookupService);
  items = signal<Reservation[]>([]);
  form = signal<{ bookId: number | null; memberId: number | null } | null>(null);

  ngOnInit() { this.lookup.load(); this.load(); }
  load() { this.api.reservations().subscribe({ next: i => this.items.set([...i].sort((a, b) => b.id - a.id)), error: e => this.toast.err(e) }); }
  create() {
    const f = this.form()!;
    this.api.reserve(f.bookId!, f.memberId!).subscribe({
      next: () => { this.toast.ok('Réservation créée.'); this.form.set(null); this.load(); }, error: e => this.toast.err(e) });
  }
  cancel(r: Reservation) {
    this.api.cancelReservation(r.id).subscribe({ next: () => { this.toast.ok('Réservation annulée.'); this.load(); }, error: e => this.toast.err(e) });
  }
  remove(r: Reservation) {
    if (!confirm('Supprimer cette réservation ?')) return;
    this.api.deleteReservation(r.id).subscribe({ next: () => { this.toast.ok('Réservation supprimée.'); this.load(); }, error: e => this.toast.err(e) });
  }
}
