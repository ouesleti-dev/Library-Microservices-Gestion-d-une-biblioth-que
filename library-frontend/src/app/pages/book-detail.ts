import { Component, effect, inject, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { LookupService } from '../core/lookup.service';
import { ToastService } from '../core/toast.service';
import { BookDetails } from '../core/models';

@Component({
  selector: 'app-book-detail',
  imports: [FormsModule, RouterLink],
  template: `
    <a routerLink="/books" class="muted">← Retour aux livres</a>
    @if (d(); as d) {
      <div class="page-head" style="margin-top:10px">
        <div><h1>{{ d.book.title }}</h1><p>{{ d.book.author }} · {{ d.book.category }} · {{ d.book.publicationYear }}</p></div>
        <span class="badge" [class.green]="d.book.available" [class.amber]="!d.book.available">{{ d.book.available ? 'Disponible' : 'Emprunté' }}</span>
      </div>
      <div class="card">
        <h2>Avis <span class="muted" style="font:400 15px var(--sans)">
          — @if (d.reviewCount) { <span class="stars">{{ stars(d.averageRating) }}</span> {{ d.averageRating.toFixed(1) }}/5 ({{ d.reviewCount }}) } @else { aucun avis }</span></h2>
        @for (r of d.reviews; track r.id) {
          <div class="review">
            <div><span class="stars">{{ stars(r.rating) }}</span> <strong>{{ lookup.memberName(r.memberId) }}</strong></div>
            <p>{{ r.comment }}</p>
            @if (auth.isAdmin()) { <button class="btn sm danger" (click)="remove(r.id)">Supprimer</button> }
          </div>
        }
        @if (auth.canWrite()) {
          <form (ngSubmit)="submit()" class="form">
            <h3>Ajouter un avis</h3>
            <div class="grid2">
              <div class="field"><label for="m">Membre</label>
                <select id="m" name="m" [(ngModel)]="memberId" required><option [ngValue]="null" disabled>Choisir…</option>
                  @for (m of lookup.members(); track m.id) { <option [ngValue]="m.id">{{ m.firstName }} {{ m.lastName }}</option> }</select></div>
              <div class="field"><label for="r">Note</label>
                <select id="r" name="r" [(ngModel)]="rating">@for (n of [5,4,3,2,1]; track n) { <option [ngValue]="n">{{ n }} ★</option> }</select></div>
            </div>
            <div class="field"><label for="c">Commentaire</label><textarea id="c" name="c" rows="3" [(ngModel)]="comment" required></textarea></div>
            <button class="btn primary" [disabled]="!memberId || !comment.trim()">Publier</button>
          </form>
        }
      </div>
    } @else if (failed()) { <p>Livre introuvable.</p> } @else { <p class="muted">Chargement…</p> }`,
  styles: `
    .review { border-bottom: 1px solid var(--line); padding: 12px 0; } .review p { margin: 4px 0 8px; }
    .form { margin-top: 20px; max-width: 520px; } .form h3 { margin-bottom: 10px; }`
})
export class BookDetail {
  id = input.required<string>();
  private api = inject(ApiService);
  private toast = inject(ToastService);
  auth = inject(AuthService);
  lookup = inject(LookupService);
  d = signal<BookDetails | null>(null);
  failed = signal(false);
  memberId: number | null = null; rating = 5; comment = '';

  constructor() {
    this.lookup.load();
    effect(() => this.load(Number(this.id())));
  }
  stars = (n: number) => '★'.repeat(Math.round(n)) + '☆'.repeat(5 - Math.round(n));
  load(id = Number(this.id())) {
    this.api.bookDetails(id).subscribe({ next: d => this.d.set(d), error: () => this.failed.set(true) });
  }
  submit() {
    this.api.addReview(Number(this.id()), this.memberId!, this.rating, this.comment).subscribe({
      next: () => { this.toast.ok('Avis publié.'); this.comment = ''; this.load(); },
      error: e => this.toast.err(e)
    });
  }
  remove(id: number) {
    this.api.deleteReview(id).subscribe({ next: () => { this.toast.ok('Avis supprimé.'); this.load(); }, error: e => this.toast.err(e) });
  }
}
