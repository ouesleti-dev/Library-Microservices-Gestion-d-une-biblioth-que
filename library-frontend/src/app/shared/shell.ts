import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../core/auth.service';
import { ToastService } from '../core/toast.service';

@Component({
  selector: 'app-shell',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <aside>
      <div class="logo">📚 <span>Bibliothèque</span></div>
      <nav>
        @for (l of links; track l.path) {
          <a [routerLink]="l.path" routerLinkActive="on">{{ l.icon }} {{ l.label }}</a>
        }
      </nav>
      <div class="user">
        <div><strong>{{ auth.username() }}</strong><br><small>{{ auth.isAdmin() ? 'Administrateur' : 'Utilisateur' }}</small></div>
        <button class="btn sm" (click)="auth.logout()">Quitter</button>
      </div>
    </aside>
    <main><router-outlet /></main>
    <div class="toasts" aria-live="polite">
      @for (t of toast.toasts(); track t.id) { <div class="toast" [class.err]="t.kind === 'err'">{{ t.text }}</div> }
    </div>`,
  styles: `
    :host { display: grid; grid-template-columns: 230px 1fr; min-height: 100vh; }
    aside { background: var(--side); color: var(--side-ink); display: flex; flex-direction: column; padding: 20px 14px; position: sticky; top: 0; height: 100vh; }
    .logo { font: 600 20px var(--serif); padding: 4px 10px 22px; }
    nav { display: flex; flex-direction: column; gap: 2px; flex: 1; }
    nav a { color: inherit; text-decoration: none; padding: 9px 12px; border-radius: 8px; }
    nav a:hover { background: rgba(255,255,255,.07); }
    nav a.on { background: var(--brand); color: var(--brand-ink); font-weight: 500; }
    .user { display: flex; justify-content: space-between; align-items: center; gap: 8px; padding: 12px 10px 0; border-top: 1px solid rgba(255,255,255,.12); font-size: 14px; }
    .user small { opacity: .7; }
    main { padding: 32px clamp(16px, 4vw, 48px); min-width: 0; max-width: 1200px; width: 100%; }
    .toasts { position: fixed; right: 16px; bottom: 16px; display: flex; flex-direction: column; gap: 8px; z-index: 100; }
    .toast { background: var(--green); color: #fff; padding: 10px 16px; border-radius: 8px; box-shadow: 0 4px 14px rgba(0,0,0,.25); max-width: 340px; }
    .toast.err { background: var(--red); color: #fff; }
    @media (max-width: 760px) {
      :host { grid-template-columns: 1fr; }
      aside { position: static; height: auto; flex-direction: row; flex-wrap: wrap; align-items: center; gap: 8px; padding: 10px; }
      .logo { padding: 0 8px; } nav { flex-direction: row; flex-wrap: wrap; flex-basis: 100%; order: 3; }
      .user { border: 0; padding: 0; margin-left: auto; }
    }`
})
export class Shell {
  auth = inject(AuthService);
  toast = inject(ToastService);
  links = [
    { path: '/dashboard', label: 'Tableau de bord', icon: '◧' },
    { path: '/books', label: 'Livres', icon: '▤' },
    { path: '/members', label: 'Membres', icon: '☺' },
    { path: '/borrowings', label: 'Emprunts', icon: '⇄' },
    { path: '/reservations', label: 'Réservations', icon: '◷' },
    { path: '/fines', label: 'Amendes', icon: '€' },
    { path: '/notifications', label: 'Notifications', icon: '✉' },
  ];
}
