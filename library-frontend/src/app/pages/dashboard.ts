import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { forkJoin } from 'rxjs';
import { ApiService } from '../core/api.service';
import { ToastService } from '../core/toast.service';
import { BookStats, GlobalStats } from '../core/models';

@Component({
  selector: 'app-dashboard',
  template: `
    <div class="page-head"><div><h1>Tableau de bord</h1><p>Vue d'ensemble de la bibliothèque</p></div></div>
    @if (stats(); as s) {
      <div class="tiles">
        <div class="card"><span>Livres</span><strong>{{ s.totalBooks }}</strong></div>
        <div class="card"><span>Disponibles</span><strong class="g">{{ s.availableBooks }}</strong></div>
        <div class="card"><span>Emprunts au total</span><strong>{{ s.totalBorrowings }}</strong></div>
        <div class="card"><span>Emprunts en cours</span><strong class="a">{{ s.ongoingBorrowings }}</strong></div>
      </div>
      <div class="card">
        <h2>Livres par catégorie</h2>
        @if (categories().length) {
          <div class="bars">
            @for (c of categories(); track c.name) {
              <div class="row"><span class="name">{{ c.name }}</span>
                <div class="track"><div class="fill" [style.width.%]="c.pct"></div></div><span class="n">{{ c.count }}</span></div>
            }
          </div>
        } @else { <p class="muted">Aucune donnée.</p> }
      </div>
    } @else if (failed()) {
      <div class="card"><p>Impossible de charger les statistiques. Vérifiez que <em>statistics-service</em> et la gateway sont démarrés.</p></div>
    } @else { <p class="muted">Chargement…</p> }`,
  styles: `
    .tiles { display: grid; grid-template-columns: repeat(auto-fit, minmax(190px, 1fr)); gap: 14px; margin-bottom: 18px; }
    .tiles span { color: var(--muted); font-size: 13px; display: block; }
    .tiles strong { font: 600 34px var(--serif); } .g { color: var(--green); } .a { color: var(--amber); }
    .bars { display: flex; flex-direction: column; gap: 10px; margin-top: 14px; }
    .row { display: grid; grid-template-columns: 140px 1fr 32px; gap: 12px; align-items: center; }
    .name { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
    .track { background: var(--line); border-radius: 6px; height: 14px; }
    .fill { background: var(--brand); height: 100%; border-radius: 6px; min-width: 4px; }
    .n { text-align: right; font-variant-numeric: tabular-nums; }`
})
export class Dashboard implements OnInit {
  private api = inject(ApiService);
  private toast = inject(ToastService);
  stats = signal<GlobalStats | null>(null);
  bookStats = signal<BookStats | null>(null);
  failed = signal(false);
  categories = computed(() => {
    const e = Object.entries(this.bookStats()?.booksByCategory ?? {}).sort((a, b) => b[1] - a[1]);
    const max = Math.max(1, ...e.map(x => x[1]));
    return e.map(([name, count]) => ({ name, count, pct: (count / max) * 100 }));
  });

  ngOnInit() {
    forkJoin({ s: this.api.stats(), b: this.api.bookStats() }).subscribe({
      next: ({ s, b }) => { this.stats.set(s); this.bookStats.set(b); },
      error: e => { this.failed.set(true); this.toast.err(e); }
    });
  }
}
