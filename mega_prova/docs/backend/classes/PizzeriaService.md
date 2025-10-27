# PizzeriaService

- Tipo: `class`
- Package: `com.pizzeria.pizzeriaservice.service`
- Percorso sorgente: `backend/pizzeria-service/src/main/java/com/pizzeria/pizzeriaservice/service/PizzeriaService.java`
- Annotazioni: 
- `@Service`

## Diagramma
```mermaid
classDiagram
    class PizzeriaService
    PizzeriaService : -repository : PizzeriaRepository
    PizzeriaService : +findAll() : List<Pizzeria>
    PizzeriaService : +findById(String id) : Pizzeria
    PizzeriaService : +create(Pizzeria pizzeria) : Pizzeria
    PizzeriaService : +update(String id, Pizzeria update) : Pizzeria
    PizzeriaService : +delete(String id) : void
    PizzeriaService --> Pizzeria
    PizzeriaService --> PizzeriaNotFoundException
    PizzeriaService --> PizzeriaRepository
```


## Metodi
- `+ findAll(nessun parametro) : List<Pizzeria>`
- `+ findById(`String id`) : Pizzeria`
- `+ create(`Pizzeria pizzeria`) : Pizzeria`
- `+ update(`String id`, `Pizzeria update`) : Pizzeria`
- `+ delete(`String id`) : void`


---
_Documento generato automaticamente. Modifica la classe sorgente o aggiorna lo script per personalizzare il contenuto._
