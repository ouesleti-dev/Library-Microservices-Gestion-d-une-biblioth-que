import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { LookupService } from '../core/lookup.service';
import { ToastService } from '../core/toast.service';
import { Borrowing } from '../core/models';
import { Modal } from '../shared/modal';

@Component({
  selector: 'app-borrowings',
  imports: [FormsModule, Modal],
  template: `
    <div class="page-head">
      <div><h1>Emprunts</h1><p>{{ ongoing() }} en cours sur {{ items().length }}</p></div>
      @if (auth.canWrite()) { <button class="btn primary" (click)="openNew()">+ Nouvel emprunt</button> }
    </div>
    <div class="toolbar">
      <select aria-label="Statut" [ngModel]="status()" (ngModelChange)="status.set($event)">
        <option value="">Tous</option><option value="BORROWED">En cours</option><option value="RETURNED">Retournés</option>
      </select>
    </div>
    <div class="table-wrap">
      <table>
        <thead><tr><th>Livre</th><th>Membre</th><th>Emprunté le</th><th>Échéance</th><th>Retour</th><th>Statut</th><th></th></tr></thead>
        <tbody>
          @for (b of filtered(); track b.id) {
            <tr>
              <td>{{ lookup.bookTitle(b.bookId) }}</td><td>{{ lookup.memberName(b.memberId) }}</td>
              <td>{{ b.borrowingDate }}</td><td>{{ b.dueDate }}</td><td>{{ b.returnDate ?? '—' }}</td>
              <td>@if (b.status === 'RETURNED') { <span class="badge green">Retourné</span> }
                  @else if (late(b)) { <span class="badge red">En retard</span> } @else { <span class="badge amber">En cours</span> }</td>
              <td class="actions">
                @if (b.status === 'BORROWED' && auth.canWrite()) {
                  <button class="btn sm" (click)="giveBack(b)">Retourner</button>
                  <button class="btn sm" (click)="editDue.set({ id: b.id, dueDate: b.dueDate })">Échéance</button>
                }
                @if (auth.isAdmin()) { <button class="btn sm danger" (click)="remove(b)">Supprimer</button> }
              </td>
            </tr>
          } @empty { <tr><td colspan="7" class="empty">Aucun emprunt.</td></tr> }
        </tbody>
      </table>
    </div>

    @if (form(); as f) {
      <app-modal title="Nouvel emprunt" (closed)="form.set(null)">
        <form (ngSubmit)="create()">
          <div class="field"><label for="b">Livre (disponibles uniquement)</label>
            <select id="b" name="b" [(ngModel)]="f.bookId" required><option [ngValue]="null" disabled>Choisir…</option>
              @for (b of availableBooks(); track b.id) { <option [ngValue]="b.id">{{ b.title }} — {{ b.author }}</option> }</select></div>
          <div class="field"><label for="m">Membre</label>
            <select id="m" name="m" [(ngModel)]="f.memberId" required><option [ngValue]="null" disabled>Choisir…</option>
              @for (m of lookup.members(); track m.id) { <option [ngValue]="m.id">{{ m.firstName }} {{ m.lastName }}</option> }</select></div>
          <div class="field"><label for="d">Échéance (optionnel)</label><input id="d" name="d" type="date" [(ngModel)]="f.dueDate"></div>
          <div style="display:flex;gap:8px;justify-content:flex-end;margin-top:18px">
            <button type="button" class="btn" (click)="form.set(null)">Annuler</button>
            <button class="btn primary" [disabled]="!f.bookId || !f.memberId">Emprunter</button>
          </div>
        </form>
      </app-modal>
    }
    @if (editDue(); as e) {
      <app-modal title="Modifier l'échéance" (closed)="editDue.set(null)">
        <form (ngSubmit)="saveDue()">
          <div class="field"><label for="dd">Nouvelle échéance</label><input id="dd" name="dd" type="date" [(ngModel)]="e.dueDate" required></div>
          <div style="display:flex;gap:8px;justify-content:flex-end;margin-top:18px">
            <button type="button" class="btn" (click)="editDue.set(null)">Annuler</button>
            <button class="btn primary" [disabled]="!e.dueDate">Enregistrer</button>
          </div>
        </form>
      </app-modal>
    }`
})
export class Borrowings implements OnInit {
  private api = inject(ApiService);
  private toast = inject(ToastService);
  auth = inject(AuthService);
  lookup = inject(LookupService);
  items = signal<Borrowing[]>([]);
  status = signal('');
  form = signal<{ bookId: number | null; memberId: number | null; dueDate: string } | null>(null);
  editDue = signal<{ id: number; dueDate: string } | null>(null);
  ongoing = computed(() => this.items().filter(b => b.status === 'BORROWED').length);
  filtered = computed(() => this.items().filter(b => !this.status() || b.status === this.status()).sort((a, b) => b.id - a.id));
  availableBooks = computed(() => this.lookup.books().filter(b => b.available));
  late = (b: Borrowing) => b.status === 'BORROWED' && b.dueDate < new Date().toISOString().slice(0, 10);

  ngOnInit() { this.lookup.load(); this.load(); }
  load() { this.api.borrowings().subscribe({ next: i => this.items.set(i), error: e => this.toast.err(e) }); }
  private refresh() { this.load(); this.lookup.load(); }
  openNew() { this.lookup.load(); this.form.set({ bookId: null, memberId: null, dueDate: '' }); }
  create() {
    const f = this.form()!;
    this.api.borrow(f.bookId!, f.memberId!, f.dueDate).subscribe({
      next: () => { this.toast.ok('Emprunt enregistré.'); this.form.set(null); this.refresh(); }, error: e => this.toast.err(e) });
  }
  saveDue() {
    const e = this.editDue()!;
    this.api.updateDueDate(e.id, e.dueDate).subscribe({
      next: () => { this.toast.ok('Échéance mise à jour.'); this.editDue.set(null); this.load(); }, error: x => this.toast.err(x) });
  }
  giveBack(b: Borrowing) {
    this.api.returnBook(b.id).subscribe({ next: () => { this.toast.ok('Livre retourné.'); this.refresh(); }, error: e => this.toast.err(e) });
  }
  remove(b: Borrowing) {
    if (!confirm('Supprimer cet emprunt ?')) return;
    this.api.deleteBorrowing(b.id).subscribe({ next: () => { this.toast.ok('Emprunt supprimé.'); this.refresh(); }, error: e => this.toast.err(e) });
  }
}
