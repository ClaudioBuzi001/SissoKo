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
    AppComponent : +deletingId : string | null
    AppComponent : +pendingDeleteId : string | null
    AppComponent : -revealTimers : Array<ReturnType<typeof setTimeout>>
    AppComponent : +duration : 900,
              easing: 'cubic-bezier(0.19, 1, 0.22, 1)',
              delay: index * 80,
              fill: 'forwards'
            }
          )
    AppComponent : +name : pizzeria.name,
      address: pizzeria.address,
      city: pizzeria.city,
      phoneNumber: pizzeria.phoneNumber,
      openingHours: pizzeria.openingHours,
      deliveryAvailable: pizzeria.deliveryAvailable,
      latitude: pizzeria.latitude,
      longitude: pizzeria.longitude
    })
    AppComponent : +name : ['', [Validators.required, Validators.maxLength(80)]],
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
    })
    AppComponent : +name : '',
      address: '',
      city: '',
      phoneNumber: '',
      openingHours: '',
      deliveryAvailable: false,
      latitude: null,
      longitude: null
    })
    AppComponent : +name : raw.name?.trim() ?? '',
      address: raw.address?.trim() ?? '',
      city: raw.city?.trim() ?? '',
      phoneNumber: raw.phoneNumber?.trim() ?? '',
      openingHours: raw.openingHours?.trim() ?? '',
      deliveryAvailable: !!raw.deliveryAvailable,
      latitude: this.toNumberOrNull(raw.latitude),
      longitude: this.toNumberOrNull(raw.longitude)
    }
    AppComponent : +latitude : this.toNumberOrNull(pizzeria.latitude),
      longitude: this.toNumberOrNull(pizzeria.longitude)
    }
    AppComponent : +ngOnInit() : void
    AppComponent : +ngAfterViewInit() : void
    AppComponent : +ngOnDestroy() : void
    AppComponent : +loadPizzerias() : Promise<void>
    AppComponent : +onSearchTermChange(string term) : void
    AppComponent : +onSearchSubmit(Event event) : void
    AppComponent : +trackById(number _, Pizzeria item) : string
    AppComponent : -applyFilter() : void
    AppComponent : +onSubmit() : Promise<void>
    AppComponent : +startCreate() : void
    AppComponent : +startEdit(Pizzeria pizzeria) : void
    AppComponent : +requestDelete(Pizzeria pizzeria) : void
    AppComponent : +confirmDelete(Pizzeria pizzeria) : Promise<void>
    AppComponent : +cancelDelete() : void
    AppComponent : +isFieldInvalid(keyof PizzeriaPayload controlName) : boolean
    AppComponent : -populateForm(Pizzeria pizzeria) : void
    AppComponent : -buildForm() : FormGroup
    AppComponent : -resetForm() : void
    AppComponent : -getPayloadFromForm() : PizzeriaPayload
    AppComponent : -toNumberOrNull(unknown value) : number
    AppComponent : -normalizePizzeria(Pizzeria pizzeria) : Pizzeria
    AppComponent --> Pizzeria
    AppComponent --> PizzeriaMapComponent
    AppComponent --> PizzeriaPayload
    AppComponent --> PizzeriaService
    AppComponent --> SpinnerComponent
    AppComponent --> ToastContainerComponent
    AppComponent --> ToastService
```


## Metodi
- `+ ngOnInit(nessun parametro) : void`
- `+ ngAfterViewInit(nessun parametro) : void`
- `+ ngOnDestroy(nessun parametro) : void`
- `+ loadPizzerias(nessun parametro) : Promise<void>`
- `+ onSearchTermChange(`string term`) : void`
- `+ onSearchSubmit(`Event event`) : void`
- `+ trackById(`number _`, `Pizzeria item`) : string`
- `- applyFilter(nessun parametro) : void`
- `+ onSubmit(nessun parametro) : Promise<void>`
- `+ startCreate(nessun parametro) : void`
- `+ startEdit(`Pizzeria pizzeria`) : void`
- `+ requestDelete(`Pizzeria pizzeria`) : void`
- `+ confirmDelete(`Pizzeria pizzeria`) : Promise<void>`
- `+ cancelDelete(nessun parametro) : void`
- `+ isFieldInvalid(`keyof PizzeriaPayload controlName`) : boolean`
- `- populateForm(`Pizzeria pizzeria`) : void`
- `- buildForm(nessun parametro) : FormGroup`
- `- resetForm(nessun parametro) : void`
- `- getPayloadFromForm(nessun parametro) : PizzeriaPayload`
- `- toNumberOrNull(`unknown value`) : number`
- `- normalizePizzeria(`Pizzeria pizzeria`) : Pizzeria`

---
_Documento generato automaticamente. Modifica il file sorgente o lo script per personalizzare il contenuto._
