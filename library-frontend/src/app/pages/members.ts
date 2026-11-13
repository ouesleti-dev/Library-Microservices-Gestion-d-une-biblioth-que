import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { ToastService } from '../core/toast.service';
import { Member } from '../core/models';
import { Modal } from '../shared/modal';

@Component({
  selector: 'app-members',
  imports: [FormsModule, Modal],
  template: `
    <div class="page-head">
      <div><h1>Membres</h1><p>{{ members().length }} inscrit(s)</p></div>
      @if (auth.isAdmin()) { <button class="btn primary" (click)="edit.set(blank())">+ Nouveau membre</button> }
    </div>
    <div class="toolbar"><input type="search" placeholder="Rechercher…" aria-label="Rechercher" [ngModel]="q()" (ngModelChange)="q.set($event)"></div>
    <div class="table-wrap">
      <table>
        <thead><tr><th>Nom</th><th>Email</th><th>Téléphone</th><th></th></tr></thead>
        <tbody>
          @for (m of filtered(); track m.id) {
            <tr><td>{{ m.firstName }} {{ m.lastName }}</td><td>{{ m.email }}</td><td>{{ m.phone }}</td>
              <td class="actions">
                <button class="btn sm" (click)="edit.set({ ...m })">Modifier</button>
                @if (auth.isAdmin()) { <button class="btn sm danger" (click)="remove(m)">Supprimer</button> }
              </td></tr>
          } @empty { <tr><td colspan="4" class="empty">Aucun membre.</td></tr> }
        </tbody>
      </table>
    </div>
    @if (edit(); as m) {
      <app-modal [title]="m.id ? 'Modifier le membre' : 'Nouveau membre'" (closed)="edit.set(null)">
        <form (ngSubmit)="save()">
          <div class="grid2">
            <div class="field"><label for="f">Prénom</label><input id="f" name="f" [(ngModel)]="m.firstName" required></div>
            <div class="field"><label for="l">Nom</label><input id="l" name="l" [(ngModel)]="m.lastName" required></div>
          </div>
          <div class="field"><label for="e">Email</label><input id="e" name="e" type="email" [(ngModel)]="m.email" required></div>
          <div class="field"><label for="p">Téléphone</label><input id="p" name="p" [(ngModel)]="m.phone"></div>
          <div style="display:flex;gap:8px;justify-content:flex-end;margin-top:18px">
            <button type="button" class="btn" (click)="edit.set(null)">Annuler</button>
            <button class="btn primary" [disabled]="!m.firstName || !m.lastName || !m.email">Enregistrer</button>
          </div>
        </form>
      </app-modal>
    }`
})
export class Members implements OnInit {
  private api = inject(ApiService);
  private toast = inject(ToastService);
  auth = inject(AuthService);
  members = signal<Member[]>([]);
  edit = signal<Member | null>(null);
  q = signal('');
  filtered = computed(() => {
    const q = this.q().toLowerCase();
    return this.members().filter(m => !q || `${m.firstName} ${m.lastName} ${m.email}`.toLowerCase().includes(q));
  });
  blank = (): Member => ({ firstName: '', lastName: '', email: '', phone: '' });

  ngOnInit() { this.load(); }
  load() { this.api.members().subscribe({ next: m => this.members.set(m), error: e => this.toast.err(e) }); }
  save() {
    this.api.saveMember(this.edit()!).subscribe({
      next: () => { this.toast.ok('Membre enregistré.'); this.edit.set(null); this.load(); },
      error: e => this.toast.err(e)
    });
  }
  remove(m: Member) {
    if (!confirm(`Supprimer ${m.firstName} ${m.lastName} ?`)) return;
    this.api.deleteMember(m.id!).subscribe({ next: () => { this.toast.ok('Membre supprimé.'); this.load(); }, error: e => this.toast.err(e) });
  }
}
