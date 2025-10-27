# PizzeriaService

- Tipo: `class`
- Percorso sorgente: `frontend/pizzeria-app/src/app/pizzerias/pizzeria.service.ts`
- Feature: `pizzerias`
- Decorator: 
- `@Injectable`

## Diagramma
```mermaid
classDiagram
    class PizzeriaService
    PizzeriaService : -http : inject(HttpClient)
    PizzeriaService : -baseUrl : '/api/pizzerias'
    PizzeriaService : +list() : Observable<Pizzeria[]>
    PizzeriaService : +create(PizzeriaPayload payload) : Observable<Pizzeria>
    PizzeriaService : +update(string id, PizzeriaPayload payload) : Observable<Pizzeria>
    PizzeriaService : +delete(string id) : Observable<void>
    PizzeriaService --> Pizzeria
    PizzeriaService --> PizzeriaPayload
```


## Metodi
- `+ list(nessun parametro) : Observable<Pizzeria[]>`
- `+ create(`PizzeriaPayload payload`) : Observable<Pizzeria>`
- `+ update(`string id`, `PizzeriaPayload payload`) : Observable<Pizzeria>`
- `+ delete(`string id`) : Observable<void>`

---
_Documento generato automaticamente. Modifica il file sorgente o lo script per personalizzare il contenuto._
