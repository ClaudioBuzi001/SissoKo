import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Pizzeria, PizzeriaPayload } from './pizzeria.model';

@Injectable({
  providedIn: 'root'
})
export class PizzeriaService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/pizzerias';

  list(): Observable<Pizzeria[]> {
    return this.http.get<Pizzeria[]>(this.baseUrl);
  }

  create(payload: PizzeriaPayload): Observable<Pizzeria> {
    return this.http.post<Pizzeria>(this.baseUrl, payload);
  }

  update(id: string, payload: PizzeriaPayload): Observable<Pizzeria> {
    return this.http.put<Pizzeria>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
