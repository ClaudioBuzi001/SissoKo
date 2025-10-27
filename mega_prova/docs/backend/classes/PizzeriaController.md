# PizzeriaController

- Tipo: `class`
- Package: `com.pizzeria.pizzeriaservice.controller`
- Percorso sorgente: `backend/pizzeria-service/src/main/java/com/pizzeria/pizzeriaservice/controller/PizzeriaController.java`
- Annotazioni: 
- `@RestController`
- `@RequestMapping`

## Diagramma
```mermaid
classDiagram
    class PizzeriaController
    PizzeriaController : -service : PizzeriaService
    PizzeriaController : +list() : List<Pizzeria>
    PizzeriaController : +findOne(@PathVariable String id) : Pizzeria
    PizzeriaController : +create(@Valid @RequestBody Pizzeria pizzeria) : Pizzeria
    PizzeriaController : +update(@PathVariable String id, @Valid @RequestBody Pizzeria pizzeria) : Pizzeria
    PizzeriaController : +delete(@PathVariable String id) : void
    PizzeriaController --> Pizzeria
    PizzeriaController --> PizzeriaService
```


## Metodi
- `+ list(nessun parametro) : List<Pizzeria>`
- `+ findOne(`@PathVariable String id`) : Pizzeria`
- `+ create(`@Valid @RequestBody Pizzeria pizzeria`) : Pizzeria`
- `+ update(`@PathVariable String id`, `@Valid @RequestBody Pizzeria pizzeria`) : Pizzeria`
- `+ delete(`@PathVariable String id`) : void`

## Endpoint REST
- `GET /` → `list()`
- `GET /{id}` → `findOne()`
- `POST /` → `create()`
- `PUT /{id}` → `update()`
- `DELETE /{id}` → `delete()`

---
_Documento generato automaticamente. Modifica la classe sorgente o aggiorna lo script per personalizzare il contenuto._
