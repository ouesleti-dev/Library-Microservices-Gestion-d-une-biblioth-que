import { Injectable, computed, inject, signal } from '@angular/core';
import { forkJoin } from 'rxjs';
import { ApiService } from './api.service';
import { Book, Member } from './models';

/** Charge livres + membres pour afficher des noms à la place des identifiants. */
@Injectable({ providedIn: 'root' })
export class LookupService {
  private api = inject(ApiService);
  books = signal<Book[]>([]);
  members = signal<Member[]>([]);
  private bookMap = computed(() => new Map(this.books().map(b => [b.id!, b])));
  private memberMap = computed(() => new Map(this.members().map(m => [m.id!, m])));

  load(done?: () => void) {
    forkJoin({ b: this.api.books(), m: this.api.members() }).subscribe({
      next: ({ b, m }) => { this.books.set(b); this.members.set(m); done?.(); },
      error: () => done?.()
    });
  }
  bookTitle = (id: number) => this.bookMap().get(id)?.title ?? `Livre #${id}`;
  memberName = (id: number) => { const m = this.memberMap().get(id); return m ? `${m.firstName} ${m.lastName}` : `Membre #${id}`; };
}
