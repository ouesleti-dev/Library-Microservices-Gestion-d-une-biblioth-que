import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-modal',
  template: `
    <div class="backdrop" (click)="closed.emit()">
      <div class="dialog" role="dialog" aria-modal="true" [attr.aria-label]="title()" (click)="$event.stopPropagation()">
        <h2>{{ title() }}</h2>
        <ng-content />
      </div>
    </div>`,
  styles: `
    .backdrop { position: fixed; inset: 0; background: rgba(20,12,6,.55); display: grid; place-items: center; padding: 16px; z-index: 50; }
    .dialog { background: var(--surface); border-radius: 12px; padding: 24px; width: 100%; max-width: 480px; max-height: 90vh; overflow: auto; }
    h2 { margin-bottom: 16px; }`
})
export class Modal {
  title = input.required<string>();
  closed = output<void>();
}
