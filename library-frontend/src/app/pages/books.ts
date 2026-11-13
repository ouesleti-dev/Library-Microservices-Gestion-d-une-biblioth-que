import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { ToastService } from '../core/toast.service';
import { Book } from '../core/models';
import { Modal } from '../shared/modal';

const blank = (): Book => ({ title: '', author: '', category: '', publicationYear: null, available: true });

@Component({
  selector: 'app-books',
  imports: [FormsModule, RouterLink, Modal],
  template: `
    <div class="page-head">
      <div><h1>Livres</h1><p>{{ books().length }} titre(s) au catalogue</p></div>
      @if (auth.isAdmin()) { <button class="btn primary" (click)="edit.set(blank())">+ Nouveau livre</button> }
    </div>
    <div class="toolbar">
      <input type="search" placeholder="Rechercher titre, auteur…" aria-label="Rechercher" [ngModel]="q()" (ngModelChange)="q.set($event)">
      <select aria-label="Catégorie" [ngModel]="cat()" (ngModelChange)="cat.set($event)">
        <option value="">Toutes les catégories</option>
        @for (c of categories(); track c) { <option>{{ c }}</option> }
      </select>
      <select aria-label="Disponibilité" [ngModel]="avail()" (ngModelChange)="avail.set($event)">
        <option value="">Tous</option><option value="y">Disponibles</option><option value="n">Empruntés</option>
      </select>
    </div>
    <div class="table-wrap">
      <table>
        <thead><tr><th>Titre</th><th>Auteur</th><th>Catégorie</th><th>Année</th><th>Statut</th><th></th></tr></thead>
        <tbody>
          @for (b of filtered(); track b.id) {
            <tr>
              <td><a [routerLink]="['/books', b.id]">{{ b.title }}</a></td><td>{{ b.author }}</td><td>{{ b.category }}</td><td>{{ b.publicationYear }}</td>
              <td><span class="badge" [class.green]="b.available" [class.amber]="!b.available">{{ b.available ? 'Disponible' : 'Emprunté' }}</span></td>
              <td class="actions">
                <a class="btn sm" [routerLink]="['/books', b.id]">Détails</a>
                <button class="btn sm" (click)="edit.set({ ...b })">Modifier</button>
                @if (auth.isAdmin()) { <button class="btn sm danger" (click)="remove(b)">Supprimer</button> }
              </td>
            </tr>
          } @empty { <tr><td colspan="6" class="empty">Aucun livre.</td></tr> }
        </tbody>
      </table>
    </div>

    @if (edit(); as b) {
      <app-modal [title]="b.id ? 'Modifier le livre' : 'Nouveau livre'" (closed)="edit.set(null)">
        <form (ngSubmit)="save()">
          <div class="field"><label for="t">Titre</label><input id="t" name="title" [(ngModel)]="b.title" required></div>
          <div class="field"><label for="a">Auteur</label><input id="a" name="author" [(ngModel)]="b.author" required></div>
          <div class="grid2">
            <div class="field"><label for="c">Catégorie</label><input id="c" name="category" [(ngModel)]="b.category" list="cats"></div>
            <div class="field"><label for="y">Année</label><input id="y" name="year" type="number" [(ngModel)]="b.publicationYear"></div>
          </div>
          <datalist id="cats">@for (c of categories(); track c) { <option [value]="c"></option> }</datalist>
          <label><input type="checkbox" name="available" [(ngModel)]="b.available" style="width:auto"> Disponible</label>
          <div style="display:flex;gap:8px;justify-content:flex-end;margin-top:18px">
            <button type="button" class="btn" (click)="edit.set(null)">Annuler</button>
            <button class="btn primary" [disabled]="!b.title || !b.author">Enregistrer</button>
          </div>
        </form>
      </app-modal>
    }`
})
export class Books implements OnInit {
  private api = inject(ApiService);
  private toast = inject(ToastService);
  auth = inject(AuthService);
  blank = blank;
  books = signal<Book[]>([]);
  edit = signal<Book | null>(null);
  q = signal(''); cat = signal(''); avail = signal('');
  categories = computed(() => [...new Set(this.books().map(b => b.category).filter(Boolean))].sort());
  filtered = computed(() => {
    const q = this.q().toLowerCase();
    return this.books().filter(b =>
      (!q || b.title.toLowerCase().includes(q) || b.author.toLowerCase().includes(q)) &&
      (!this.cat() || b.category === this.cat()) &&
      (!this.avail() || (this.avail() === 'y') === b.available));
  });

  ngOnInit() { this.load(); }
  load() { this.api.books().subscribe({ next: b => this.books.set(b), error: e => this.toast.err(e) }); }
  save() {
    this.api.saveBook(this.edit()!).subscribe({
      next: () => { this.toast.ok('Livre enregistré.'); this.edit.set(null); this.load(); },
      error: e => this.toast.err(e)
    });
  }
  remove(b: Book) {
    if (!confirm(`Supprimer « ${b.title} » ?`)) return;
    this.api.deleteBook(b.id!).subscribe({ next: () => { this.toast.ok('Livre supprimé.'); this.load(); }, error: e => this.toast.err(e) });
  }
}
