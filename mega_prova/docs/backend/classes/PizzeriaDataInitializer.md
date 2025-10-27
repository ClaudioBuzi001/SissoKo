# PizzeriaDataInitializer

- Tipo: `class`
- Package: `com.pizzeria.pizzeriaservice.config`
- Percorso sorgente: `backend/pizzeria-service/src/main/java/com/pizzeria/pizzeriaservice/config/PizzeriaDataInitializer.java`
- Annotazioni: 
- `@Component`
- `@Profile`

## Diagramma
```mermaid
classDiagram
    class PizzeriaDataInitializer
    PizzeriaDataInitializer : -repository : PizzeriaRepository
    PizzeriaDataInitializer : +run(String... args) : void
    PizzeriaDataInitializer --> Pizzeria
    PizzeriaDataInitializer --> PizzeriaRepository
```


## Metodi
- `+ run(`String... args`) : void`


---
_Documento generato automaticamente. Modifica la classe sorgente o aggiorna lo script per personalizzare il contenuto._
