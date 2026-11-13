import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { LookupService } from '../core/lookup.service';
import { ToastService } from '../core/toast.service';
import { AppNotification } from '../core/models';

@Component({
  selector: 'app-notifications',
  imports: [FormsModule, DatePipe],
  template: `
    <div class="page-head"><div><h1>Notifications</h1><p>Messages envoyés aux membres (emprunts, retours…)</p></div></div>
    <div class="toolbar">
      <select aria-label="Membre" [ngModel]="memberId()" (ngModelChange)="memberId.set($event); load()">
        <option [ngValue]="null">Tous les membres</option>
        @for (m of lookup.members(); track m.id) { <option [ngValue]="m.id">{{ m.firstName }} {{ m.lastName }}</option> }
      </select>
    </div>
    @if (auth.canWrite()) {
      <form class="card send" (ngSubmit)="send()">
        <select name="to" aria-label="Destinataire" [(ngModel)]="to" required><option [ngValue]="null" disabled>Destinataire…</option>
          @for (m of lookup.members(); track m.id) { <option [ngValue]="m.id">{{ m.firstName }} {{ m.lastName }}</option> }</select>
        <input name="msg" aria-label="Message" placeholder="Message…" [(ngModel)]="msg" required>
        <button class="btn primary" [disabled]="!to || !msg.trim()">Envoyer</button>
      </form>
    }
    <div class="table-wrap">
      <table>
        <thead><tr><th>Date</th><th>Membre</th><th>Message</th></tr></thead>
        <tbody>
          @for (n of items(); track n.id) {
            <tr><td>{{ n.createdAt | date:'dd/MM/yyyy HH:mm' }}</td><td>{{ lookup.memberName(n.memberId) }}</td><td class="wrap">{{ n.message }}</td></tr>
          } @empty { <tr><td colspan="3" class="empty">Aucune notification.</td></tr> }
        </tbody>
      </table>
    </div>`,
  styles: `.send { display: flex; gap: 10px; margin-bottom: 14px; padding: 12px; flex-wrap: wrap; } .send input { flex: 1; min-width: 200px; } .send select { width: auto; }`
})
export class Notifications implements OnInit {
  private api = inject(ApiService);
  private toast = inject(ToastService);
  auth = inject(AuthService);
  lookup = inject(LookupService);
  items = signal<AppNotification[]>([]);
  memberId = signal<number | null>(null);
  to: number | null = null; msg = '';

  ngOnInit() { this.lookup.load(); this.load(); }
  load() {
    this.api.notifications(this.memberId() ?? undefined).subscribe({
      next: i => this.items.set([...i].sort((a, b) => b.id - a.id)), error: e => this.toast.err(e) });
  }
  send() {
    this.api.notify(this.to!, this.msg).subscribe({
      next: () => { this.toast.ok('Notification envoyée.'); this.msg = ''; this.load(); }, error: e => this.toast.err(e) });
  }
}
