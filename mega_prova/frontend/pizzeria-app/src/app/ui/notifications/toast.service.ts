import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

import { Toast, ToastType } from './toast.model';

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  private readonly toasts$ = new BehaviorSubject<Toast[]>([]);
  private counter = 0;

  readonly stream = this.toasts$.asObservable();

  success(message: string, timeout = 4000): void {
    this.push(message, 'success', timeout);
  }

  error(message: string, timeout = 5000): void {
    this.push(message, 'error', timeout);
  }

  info(message: string, timeout = 4000): void {
    this.push(message, 'info', timeout);
  }

  dismiss(id: number): void {
    this.toasts$.next(this.toasts$.value.filter((toast) => toast.id !== id));
  }

  private push(message: string, type: ToastType, timeout: number): void {
    const id = ++this.counter;
    const toast: Toast = {
      id,
      message,
      type,
      timeout
    };

    this.toasts$.next([...this.toasts$.value, toast]);

    if (timeout > 0) {
      setTimeout(() => {
        this.dismiss(id);
      }, timeout);
    }
  }
}
