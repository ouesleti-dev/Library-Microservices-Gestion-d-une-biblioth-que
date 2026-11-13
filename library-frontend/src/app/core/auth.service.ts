import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { KEYCLOAK_CLIENT_ID, KEYCLOAK_REALM, KEYCLOAK_URL } from './config';

interface Session { token: string; username: string; roles: string[]; exp: number; }
const KEY = 'library.session';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private session = signal<Session | null>(this.restore());

  readonly isLoggedIn = computed(() => { const s = this.session(); return !!s && s.exp * 1000 > Date.now(); });
  readonly username = computed(() => this.session()?.username ?? '');
  readonly roles = computed(() => this.session()?.roles ?? []);
  readonly isAdmin = computed(() => this.roles().includes('ADMIN'));
  readonly canWrite = computed(() => this.isAdmin() || this.roles().includes('USER'));

  get token(): string | null { return this.isLoggedIn() ? this.session()!.token : null; }

  login(username: string, password: string): Observable<unknown> {
    const url = `${KEYCLOAK_URL}/realms/${KEYCLOAK_REALM}/protocol/openid-connect/token`;
    const body = new URLSearchParams({ grant_type: 'password', client_id: KEYCLOAK_CLIENT_ID, username, password });
    return this.http.post<{ access_token: string }>(url, body.toString(),
      { headers: new HttpHeaders({ 'Content-Type': 'application/x-www-form-urlencoded' }) }
    ).pipe(tap(r => {
      const p = this.decode(r.access_token);
      const s: Session = { token: r.access_token, username: p['preferred_username'] ?? username, roles: p['realm_access']?.roles ?? [], exp: p['exp'] };
      this.session.set(s);
      try { sessionStorage.setItem(KEY, JSON.stringify(s)); } catch { /* ignore */ }
    }));
  }

  logout(): void {
    this.session.set(null);
    try { sessionStorage.removeItem(KEY); } catch { /* ignore */ }
    this.router.navigate(['/login']);
  }

  private restore(): Session | null {
    try { const raw = sessionStorage.getItem(KEY); return raw ? JSON.parse(raw) : null; } catch { return null; }
  }
  private decode(jwt: string): any {
    const b = jwt.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
    return JSON.parse(decodeURIComponent(atob(b).split('').map(c => '%' + c.charCodeAt(0).toString(16).padStart(2, '0')).join('')));
  }
}
