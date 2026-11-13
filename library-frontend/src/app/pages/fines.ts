import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CurrencyPipe } from '@angular/common';
import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { LookupService } from '../core/lookup.service';
import { ToastService } from '../core/toast.service';
import { Borrowing, Fine } from '../core/models';
import { Modal } from '../shared/modal';

@Component({
  selector: 'app-fines',
  imports: [FormsModule, CurrencyPipe, Modal],
  template: `
    <div class="page-head">
      <div><h1>Amendes</h1><p>{{ unpaidTotal() | currency:'EUR':'symbol':'1.2-2' }} restant à payer</p></div>
      @if (auth.canWrite()) { <button class="btn primary" (click)="openNew()">+ Nouvelle amende</button> }
    </div>
    <div class="table-wrap">
      <table>
        <thead><tr><th>Membre</th><th>Emprunt</th><th>Montant</th><th>Motif</th><th>Date</th><th>Statut</th><th></th></tr></thead>
        <tbody>
          @for (f of items(); track f.id) {
            <tr><td>{{ lookup.memberName(f.memberId) }}</td><td>#{{ f.borrowingId }}</td><td>{{ f.amount | currency:'EUR':'symbol':'1.2-2' }}</td>
              <td class="wrap">{{ f.reason }}</td><td>{{ f.createdDate }}</td>
              <td><span class="badge" [class.green]="f.status === 'PAID'" [class.red]="f.status !== 'PAID'">{{ f.status === 'PAID' ? 'Payée' : 'Impayée' }}</span></td>
              <td class="actions">
                @if (f.status !== 'PAID' && auth.canWrite()) { <button class="btn sm" (click)="pay(f)">Marquer payée</button> }
                @if (auth.isAdmin()) { <button class="btn sm danger" (click)="remove(f)">Supprimer</button> }
              </td></tr>
          } @empty { <tr><td colspan="7" class="empty">Aucune amende.</td></tr> }
        </tbody>
      </table>
    </div>
    @if (form(); as f) {
      <app-modal title="Nouvelle amende" (closed)="form.set(null)">
        <form (ngSubmit)="create()">
          <div class="field"><label for="b">Emprunt concerné</label>
            <select id="b" name="b" [(ngModel)]="f.borrowingId" (ngModelChange)="pick($event)" required><option [ngValue]="null" disabled>Choisir…</option>
              @for (b of borrowings(); track b.id) { <option [ngValue]="b.id">#{{ b.id }} — {{ lookup.bookTitle(b.bookId) }} ({{ lookup.memberName(b.memberId) }})</option> }</select></div>
          <div class="grid2">
            <div class="field"><label for="a">Montant (€)</label><input id="a" name="a" type="number" min="0.01" step="0.01" [(ngModel)]="f.amount" required></div>
            <div class="field"><label for="r">Motif</label><input id="r" name="r" [(ngModel)]="f.reason" required placeholder="Retard, livre abîmé…"></div>
          </div>
          <div style="display:flex;gap:8px;justify-content:flex-end;margin-top:18px">
            <button type="button" class="btn" (click)="form.set(null)">Annuler</button>
            <button class="btn primary" [disabled]="!f.borrowingId || !(f.amount > 0) || !f.reason">Créer</button>
          </div>
        </form>
      </app-modal>
    }`
})
export class Fines implements OnInit {
  private api = inject(ApiService);
  private toast = inject(ToastService);
  auth = inject(AuthService);
  lookup = inject(LookupService);
  items = signal<Fine[]>([]);
  borrowings = signal<Borrowing[]>([]);
  form = signal<{ borrowingId: number | null; memberId: number | null; amount: number; reason: string } | null>(null);
  unpaidTotal = computed(() => this.items().filter(f => f.status !== 'PAID').reduce((s, f) => s + f.amount, 0));

  ngOnInit() { this.lookup.load(); this.load(); }
  load() { this.api.fines().subscribe({ next: i => this.items.set([...i].sort((a, b) => b.id - a.id)), error: e => this.toast.err(e) }); }
  openNew() {
    this.api.borrowings().subscribe({ next: b => this.borrowings.set(b), error: e => this.toast.err(e) });
    this.form.set({ borrowingId: null, memberId: null, amount: 0, reason: '' });
  }
  pick(id: number) { this.form.update(f => f && ({ ...f, borrowingId: id, memberId: this.borrowings().find(b => b.id === id)?.memberId ?? null })); }
  create() {
    const f = this.form()!;
    this.api.createFine(f.memberId!, f.borrowingId!, f.amount, f.reason).subscribe({
      next: () => { this.toast.ok('Amende créée.'); this.form.set(null); this.load(); }, error: e => this.toast.err(e) });
  }
  pay(f: Fine) { this.api.payFine(f.id).subscribe({ next: () => { this.toast.ok('Amende payée.'); this.load(); }, error: e => this.toast.err(e) }); }
  remove(f: Fine) {
    if (!confirm('Supprimer cette amende ?')) return;
    this.api.deleteFine(f.id).subscribe({ next: () => { this.toast.ok('Amende supprimée.'); this.load(); }, error: e => this.toast.err(e) });
  }
}
