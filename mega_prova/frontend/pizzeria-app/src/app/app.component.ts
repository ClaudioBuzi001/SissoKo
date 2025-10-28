import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, QueryList, ViewChildren } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterOutlet } from '@angular/router';
import { firstValueFrom } from 'rxjs';

import { Pizzeria, PizzeriaPayload } from './pizzerias/pizzeria.model';
import { PizzeriaService } from './pizzerias/pizzeria.service';
import { ToastContainerComponent } from './ui/notifications/toast-container.component';
import { ToastService } from './ui/notifications/toast.service';
import { SpinnerComponent } from './ui/spinner/spinner.component';
import { PizzeriaMapComponent } from './pizzerias/pizzeria-map/pizzeria-map.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterOutlet,
    ToastContainerComponent,
    SpinnerComponent,
    PizzeriaMapComponent
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChildren('revealEl') readonly revealElements!: QueryList<ElementRef<HTMLElement>>;

  title = 'Pizzeria';
  searchTerm = '';
  loading = false;
  error: string | null = null;
  pizzerias: Pizzeria[] = [];
  filtered: Pizzeria[] = [];
  form: FormGroup;
  editingId: string | null = null;
  submitLoading = false;
  deletingId: string | null = null;
  pendingDeleteId: string | null = null;
  private revealTimers: Array<ReturnType<typeof setTimeout>> = [];

  constructor(
    private readonly pizzeriaService: PizzeriaService,
    private readonly formBuilder: FormBuilder,
    private readonly toastService: ToastService
  ) {
    this.form = this.buildForm();
  }

  ngOnInit(): void {
    void this.loadPizzerias();
    this.resetForm();
  }

  ngAfterViewInit(): void {
    const elements = this.revealElements?.toArray().map((ref) => ref.nativeElement) ?? [];

    if (typeof window === 'undefined' || elements.length === 0) {
      return;
    }

    elements.forEach((element, index) => {
      const timer = setTimeout(() => {
        if (typeof element.animate === 'function') {
          const animation = element.animate(
            [
              { opacity: 0, transform: 'translate3d(0, 40px, 0)' },
              { opacity: 1, transform: 'translate3d(0, 0, 0)' }
            ],
            {
              duration: 900,
              easing: 'cubic-bezier(0.19, 1, 0.22, 1)',
              delay: index * 80,
              fill: 'forwards'
            }
          );

          void animation.finished
            .then(() => {
              element.style.opacity = '1';
              element.style.transform = 'translate3d(0, 0, 0)';
            })
            .catch(() => {
              element.style.opacity = '1';
              element.style.transform = 'translate3d(0, 0, 0)';
            });
        } else {
          element.style.opacity = '1';
          element.style.transform = 'translate3d(0, 0, 0)';
        }
      }, index * 100);

      this.revealTimers.push(timer);
    });
  }

  ngOnDestroy(): void {
    this.revealTimers.forEach((timer) => clearTimeout(timer));
    this.revealTimers = [];
  }

  async loadPizzerias(): Promise<void> {
    this.loading = true;
    this.error = null;

    try {
      const data = await firstValueFrom(this.pizzeriaService.list());
      this.pizzerias = data.map((pizzeria) => this.normalizePizzeria(pizzeria));
      this.applyFilter();
    } catch (err) {
      console.error('Failed to load pizzerias', err);
      this.error = 'Impossibile recuperare le pizzerie. Riprova più tardi.';
      this.pizzerias = [];
      this.filtered = [];
    } finally {
      this.loading = false;
    }
  }

  onSearchTermChange(term: string): void {
    this.searchTerm = term;
    this.applyFilter();
  }

  onSearchSubmit(event: Event): void {
    event.preventDefault();
    this.applyFilter();
  }

  trackById(_: number, item: Pizzeria): string {
    return item.id;
  }

  private applyFilter(): void {
    const normalized = this.searchTerm.trim().toLowerCase();

    if (!normalized) {
      this.filtered = [...this.pizzerias];
      return;
    }

    this.filtered = this.pizzerias.filter((pizzeria) => {
      return (
        pizzeria.name.toLowerCase().includes(normalized) ||
        pizzeria.city.toLowerCase().includes(normalized)
      );
    });
  }

  async onSubmit(): Promise<void> {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitLoading = true;

    try {
      const payload = this.getPayloadFromForm();

      if (this.editingId) {
        await firstValueFrom(this.pizzeriaService.update(this.editingId, payload));
        this.toastService.success('Pizzeria aggiornata con successo.');
      } else {
        await firstValueFrom(this.pizzeriaService.create(payload));
        this.toastService.success('Pizzeria aggiunta con successo.');
      }

      await this.loadPizzerias();

      if (this.editingId) {
        const current = this.pizzerias.find((pizzeria) => pizzeria.id === this.editingId);
        if (current) {
          this.populateForm(current);
        } else {
          this.startCreate();
        }
      } else {
        this.startCreate();
      }
    } catch (err) {
      console.error('Failed to save pizzeria', err);
      this.toastService.error('Errore durante il salvataggio. Riprova più tardi.');
    } finally {
      this.submitLoading = false;
    }
  }

  startCreate(): void {
    this.editingId = null;
    this.pendingDeleteId = null;
    this.resetForm();
  }

  startEdit(pizzeria: Pizzeria): void {
    this.editingId = pizzeria.id;
    this.pendingDeleteId = null;
    this.populateForm(pizzeria);
  }

  requestDelete(pizzeria: Pizzeria): void {
    this.pendingDeleteId = this.pendingDeleteId === pizzeria.id ? null : pizzeria.id;
  }

  async confirmDelete(pizzeria: Pizzeria): Promise<void> {
    if (this.deletingId || this.submitLoading) {
      return;
    }

    this.deletingId = pizzeria.id;
    this.pendingDeleteId = null;

    try {
      await firstValueFrom(this.pizzeriaService.delete(pizzeria.id));
      this.toastService.info(`"${pizzeria.name}" è stata rimossa.`);
      if (this.editingId === pizzeria.id) {
        this.startCreate();
      }
      await this.loadPizzerias();
    } catch (err) {
      console.error('Failed to delete pizzeria', err);
      this.toastService.error('Errore durante l\'eliminazione. Riprova più tardi.');
    } finally {
      this.deletingId = null;
    }
  }

  cancelDelete(): void {
    this.pendingDeleteId = null;
  }

  isFieldInvalid(controlName: keyof PizzeriaPayload): boolean {
    const control = this.form.get(controlName);
    return !!control && control.invalid && (control.dirty || control.touched);
  }

  private populateForm(pizzeria: Pizzeria): void {
    this.form.setValue({
      name: pizzeria.name,
      address: pizzeria.address,
      city: pizzeria.city,
      phoneNumber: pizzeria.phoneNumber,
      openingHours: pizzeria.openingHours,
      deliveryAvailable: pizzeria.deliveryAvailable,
      latitude: pizzeria.latitude,
      longitude: pizzeria.longitude
    });
    this.form.markAsPristine();
  }

  private buildForm(): FormGroup {
    return this.formBuilder.group({
      name: ['', [Validators.required, Validators.maxLength(80)]],
      address: ['', [Validators.required, Validators.maxLength(120)]],
      city: ['', [Validators.required, Validators.maxLength(60)]],
      phoneNumber: ['', [Validators.required, Validators.maxLength(20)]],
      openingHours: ['', [Validators.required, Validators.maxLength(120)]],
      deliveryAvailable: [false],
      latitude: [
        null,
        [
          Validators.min(-90),
          Validators.max(90)
        ]
      ],
      longitude: [
        null,
        [
          Validators.min(-180),
          Validators.max(180)
        ]
      ]
    });
  }

  private resetForm(): void {
    this.form.reset({
      name: '',
      address: '',
      city: '',
      phoneNumber: '',
      openingHours: '',
      deliveryAvailable: false,
      latitude: null,
      longitude: null
    });
    this.form.markAsPristine();
    this.form.markAsUntouched();
  }

  private getPayloadFromForm(): PizzeriaPayload {
    const raw = this.form.value;
    return {
      name: raw.name?.trim() ?? '',
      address: raw.address?.trim() ?? '',
      city: raw.city?.trim() ?? '',
      phoneNumber: raw.phoneNumber?.trim() ?? '',
      openingHours: raw.openingHours?.trim() ?? '',
      deliveryAvailable: !!raw.deliveryAvailable,
      latitude: this.toNumberOrNull(raw.latitude),
      longitude: this.toNumberOrNull(raw.longitude)
    };
  }

  private toNumberOrNull(value: unknown): number | null {
    if (value === null || value === undefined || value === '') {
      return null;
    }
    const parsed = Number(value);
    return Number.isFinite(parsed) ? parsed : null;
  }

  private normalizePizzeria(pizzeria: Pizzeria): Pizzeria {
    return {
      ...pizzeria,
      latitude: this.toNumberOrNull(pizzeria.latitude),
      longitude: this.toNumberOrNull(pizzeria.longitude)
    };
  }
}
