import { AsyncPipe, NgClass, NgFor, NgIf } from '@angular/common';
import { Component, inject } from '@angular/core';

import { ToastService } from './toast.service';

@Component({
  selector: 'app-toast-container',
  standalone: true,
  imports: [AsyncPipe, NgFor, NgIf, NgClass],
  template: `
    <div class="toast-container" *ngIf="(toastService.stream | async) as toasts">
      <div
        class="toast"
        *ngFor="let toast of toasts"
        [ngClass]="['toast--' + toast.type]"
        role="status"
        aria-live="polite"
      >
        <span class="toast__message">{{ toast.message }}</span>
        <button type="button" class="toast__close" (click)="toastService.dismiss(toast.id)" aria-label="Chiudi notifica">
          ×
        </button>
      </div>
    </div>
  `,
  styleUrls: ['./toast-container.component.scss']
})
export class ToastContainerComponent {
  protected readonly toastService = inject(ToastService);
}
