# AppComponent

- Tipo: `class`
- Percorso sorgente: `frontend/pizzeria-app/src/app/app.component.ts`
- Feature: `global`
- Decorator: 
- `@Component`

## Diagramma
```mermaid
classDiagram
    class AppComponent
    AppComponent : +title : 'Pizzeria'
    AppComponent : +searchTerm : ''
    AppComponent : +loading : false
    AppComponent : +error : string | null
    AppComponent : +pizzerias : Pizzeria[]
    AppComponent : +filtered : Pizzeria[]
    AppComponent : +form : FormGroup
    AppComponent : +editingId : string | null
    AppComponent : +submitLoading : false
    AppComponent : +submitError : string | null
    AppComponent : +successMessage : string | null
    AppComponent : +deletingId : string | null
    AppComponent : +name : pizzeria.name,
      address: pizzeria.address,
      city: pizzeria.city,
      phoneNumber: pizzeria.phoneNumber,
      openingHours: pizzeria.openingHours,
      deliveryAvailable: pizzeria.deliveryAvailable
    })
    AppComponent : +name : ['', [Validators.required, Validators.maxLength(80)]],
      address: ['', [Validators.required, Validators.maxLength(120)]],
      city: ['', [Validators.required, Validators.maxLength(60)]],
      phoneNumber: ['', [Validators.required, Validators.maxLength(20)]],
      openingHours: ['', [Validators.required, Validators.maxLength(120)]],
      deliveryAvailable: [false]
    })
    AppComponent : +name : '',
      address: '',
      city: '',
      phoneNumber: '',
      openingHours: '',
      deliveryAvailable: false
    })
    AppComponent : +name : raw.name?.trim() ?? '',
      address: raw.address?.trim() ?? '',
      city: raw.city?.trim() ?? '',
      phoneNumber: raw.phoneNumber?.trim() ?? '',
      openingHours: raw.openingHours?.trim() ?? '',
      deliveryAvailable: !!raw.deliveryAvailable
    }
    AppComponent : +ngOnInit() : void
    AppComponent : +loadPizzerias() : Promise<void>
    AppComponent : +onSearchTermChange(string term) : void
    AppComponent : +onSearchSubmit(Event event) : void
    AppComponent : +trackById(number _, Pizzeria item) : string
    AppComponent : -applyFilter() : void
    AppComponent : +onSubmit() : Promise<void>
    AppComponent : +startCreate(options arg1) : void
    AppComponent : +startEdit(Pizzeria pizzeria, options arg2) : void
    AppComponent : +onDelete(Pizzeria pizzeria) : Promise<void>
    AppComponent : +isFieldInvalid(keyof PizzeriaPayload controlName) : boolean
    AppComponent : -buildForm() : FormGroup
    AppComponent : -resetForm() : void
    AppComponent : -getPayloadFromForm() : PizzeriaPayload
    AppComponent --> Pizzeria
    AppComponent --> PizzeriaPayload
    AppComponent --> PizzeriaService
```


## Metodi
- `+ ngOnInit(nessun parametro) : void`
- `+ loadPizzerias(nessun parametro) : Promise<void>`
- `+ onSearchTermChange(`string term`) : void`
- `+ onSearchSubmit(`Event event`) : void`
- `+ trackById(`number _`, `Pizzeria item`) : string`
- `- applyFilter(nessun parametro) : void`
- `+ onSubmit(nessun parametro) : Promise<void>`
- `+ startCreate(`options arg1`) : void`
- `+ startEdit(`Pizzeria pizzeria`, `options arg2`) : void`
- `+ onDelete(`Pizzeria pizzeria`) : Promise<void>`
- `+ isFieldInvalid(`keyof PizzeriaPayload controlName`) : boolean`
- `- buildForm(nessun parametro) : FormGroup`
- `- resetForm(nessun parametro) : void`
- `- getPayloadFromForm(nessun parametro) : PizzeriaPayload`

---
_Documento generato automaticamente. Modifica il file sorgente o lo script per personalizzare il contenuto._
