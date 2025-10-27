import { Component, Input } from '@angular/core';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-spinner',
  standalone: true,
  imports: [NgIf],
  template: `
    <span
      class="spinner"
      [class.spinner--inline]="inline"
      aria-hidden="true"
    ></span>
    <span class="spinner__sr" *ngIf="ariaLabel">{{ ariaLabel }}</span>
  `,
  styleUrls: ['./spinner.component.scss']
})
export class SpinnerComponent {
  @Input() inline = false;
  @Input() ariaLabel?: string;
}
