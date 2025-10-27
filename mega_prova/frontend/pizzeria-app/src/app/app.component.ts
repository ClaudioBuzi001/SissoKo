import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterOutlet } from '@angular/router';
import { firstValueFrom } from 'rxjs';

import { Pizzeria, PizzeriaPayload } from './pizzerias/pizzeria.model';
import { PizzeriaService } from './pizzerias/pizzeria.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
  title = 'Pizzeria';
  searchTerm = '';
  loading = false;
  error: string | null = null;
  pizzerias: Pizzeria[] = [];
  filtered: Pizzeria[] = [];
  form: FormGroup;
  editingId: string | null = null;
  submitLoading = false;
  submitError: string | null = null;
  successMessage: string | null = null;
  deletingId: string | null = null;

  constructor(
    private readonly pizzeriaService: PizzeriaService,
    private readonly formBuilder: FormBuilder
  ) {
    this.form = this.buildForm();
  }

  ngOnInit(): void {
    void this.loadPizzerias();
    this.resetForm();
  }

  async loadPizzerias(): Promise<void> {
    this.loading = true;
    this.error = null;

    try {
      this.pizzerias = await firstValueFrom(this.pizzeriaService.list());
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
    this.submitError = null;
    this.successMessage = null;

    try {
      const payload = this.getPayloadFromForm();

      if (this.editingId) {
        await firstValueFrom(this.pizzeriaService.update(this.editingId, payload));
        this.successMessage = 'Pizzeria aggiornata con successo.';
      } else {
        await firstValueFrom(this.pizzeriaService.create(payload));
        this.successMessage = 'Pizzeria aggiunta con successo.';
      }

      await this.loadPizzerias();

      if (this.editingId) {
        const updated = this.pizzerias.find((pizzeria) => pizzeria.id === this.editingId);
        if (updated) {
          this.startEdit(updated, { skipMessages: true });
        } else {
          this.startCreate({ skipMessages: true });
        }
      } else {
        this.startCreate({ skipMessages: true });
      }
    } catch (err) {
      console.error('Failed to save pizzeria', err);
      this.submitError = 'Errore durante il salvataggio. Riprova più tardi.';
    } finally {
      this.submitLoading = false;
    }
  }

  startCreate(options: { skipMessages?: boolean } = {}): void {
    if (!options.skipMessages) {
      this.successMessage = null;
      this.submitError = null;
    }
    this.editingId = null;
    this.resetForm();
  }

  startEdit(pizzeria: Pizzeria, options: { skipMessages?: boolean } = {}): void {
    if (!options.skipMessages) {
      this.successMessage = null;
      this.submitError = null;
    }

    this.editingId = pizzeria.id;
    this.form.setValue({
      name: pizzeria.name,
      address: pizzeria.address,
      city: pizzeria.city,
      phoneNumber: pizzeria.phoneNumber,
      openingHours: pizzeria.openingHours,
      deliveryAvailable: pizzeria.deliveryAvailable
    });
    this.form.markAsPristine();
  }

  async onDelete(pizzeria: Pizzeria): Promise<void> {
    const confirmed = window.confirm(`Sei sicuro di voler eliminare "${pizzeria.name}"?`);
    if (!confirmed) {
      return;
    }

    this.deletingId = pizzeria.id;
    this.submitError = null;
    this.successMessage = null;

    try {
      await firstValueFrom(this.pizzeriaService.delete(pizzeria.id));
      this.successMessage = 'Pizzeria rimossa con successo.';
      if (this.editingId === pizzeria.id) {
        this.startCreate({ skipMessages: true });
      }
      await this.loadPizzerias();
    } catch (err) {
      console.error('Failed to delete pizzeria', err);
      this.submitError = 'Errore durante l\'eliminazione. Riprova più tardi.';
    } finally {
      this.deletingId = null;
    }
  }

  isFieldInvalid(controlName: keyof PizzeriaPayload): boolean {
    const control = this.form.get(controlName);
    return !!control && control.invalid && (control.dirty || control.touched);
  }

  private buildForm(): FormGroup {
    return this.formBuilder.group({
      name: ['', [Validators.required, Validators.maxLength(80)]],
      address: ['', [Validators.required, Validators.maxLength(120)]],
      city: ['', [Validators.required, Validators.maxLength(60)]],
      phoneNumber: ['', [Validators.required, Validators.maxLength(20)]],
      openingHours: ['', [Validators.required, Validators.maxLength(120)]],
      deliveryAvailable: [false]
    });
  }

  private resetForm(): void {
    this.form.reset({
      name: '',
      address: '',
      city: '',
      phoneNumber: '',
      openingHours: '',
      deliveryAvailable: false
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
      deliveryAvailable: !!raw.deliveryAvailable
    };
  }
}
