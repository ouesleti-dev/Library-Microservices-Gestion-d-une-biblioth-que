import { Injectable, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

export interface Toast { id: number; kind: 'ok' | 'err'; text: string; }

@Injectable({ providedIn: 'root' })
export class ToastService {
  readonly toasts = signal<Toast[]>([]);
  private n = 0;
  ok(text: string) { this.push('ok', text); }
  err(e: unknown) {
    let text = 'Une erreur est survenue.';
    if (e instanceof HttpErrorResponse) {
      if (e.status === 0) text = 'Serveur injoignable (gateway démarrée ?).';
      else if (e.status === 403) text = 'Action réservée aux administrateurs.';
      else text = e.error?.message || e.error?.error || `Erreur ${e.status}`;
    }
    this.push('err', text);
  }
  private push(kind: Toast['kind'], text: string) {
    const id = ++this.n;
    this.toasts.update(t => [...t, { id, kind, text }]);
    setTimeout(() => this.toasts.update(t => t.filter(x => x.id !== id)), 4500);
  }
}
