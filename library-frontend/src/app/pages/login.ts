import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../core/auth.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  template: `
    <form class="card" (ngSubmit)="submit()">
      <h1>📚 Bibliothèque</h1>
      <p class="muted">Connectez-vous avec votre compte Keycloak.</p>
      <div class="field"><label for="u">Nom d'utilisateur</label><input id="u" name="u" [(ngModel)]="username" required autocomplete="username"></div>
      <div class="field"><label for="p">Mot de passe</label><input id="p" name="p" type="password" [(ngModel)]="password" required autocomplete="current-password"></div>
      @if (error()) { <p class="err" role="alert">{{ error() }}</p> }
      <button class="btn primary" style="width:100%;justify-content:center" [disabled]="loading()">{{ loading() ? 'Connexion…' : 'Se connecter' }}</button>
    </form>`,
  styles: `
    :host { display: grid; place-items: center; min-height: 100vh; padding: 16px; }
    form { width: 100%; max-width: 380px; padding: 28px; } p { margin: 6px 0 20px; }
    .err { color: var(--red); margin: 0 0 12px; font-size: 14px; }`
})
export class Login {
  private auth = inject(AuthService);
  private router = inject(Router);
  username = ''; password = '';
  loading = signal(false);
  error = signal('');

  submit() {
    this.loading.set(true); this.error.set('');
    this.auth.login(this.username, this.password).subscribe({
      next: () => this.router.navigate(['/']),
      error: (e: HttpErrorResponse) => {
        this.loading.set(false);
        this.error.set(e.status === 0 ? 'Keycloak injoignable (http://localhost:8180).' : 'Identifiants incorrects.');
      }
    });
  }
}
