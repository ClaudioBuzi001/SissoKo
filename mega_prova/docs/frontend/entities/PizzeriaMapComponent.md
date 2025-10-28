# PizzeriaMapComponent

- Tipo: `class`
- Percorso sorgente: `frontend/pizzeria-app/src/app/pizzerias/pizzeria-map/pizzeria-map.component.ts`
- Feature: `pizzerias`
- Decorator: 
- `@Component`

## Diagramma
```mermaid
classDiagram
    class PizzeriaMapComponent
    PizzeriaMapComponent : +attribution : '&copy
    PizzeriaMapComponent : +detectRetina : true
		})
	],
	zoom: 6,
	center: latLng(41.8719, 12.5674) // centro Italia
}
    PizzeriaMapComponent : +layers : Layer[]
    PizzeriaMapComponent : +fitBounds : LatLngBounds
    PizzeriaMapComponent : +hasMarkers : false
    PizzeriaMapComponent : +ngOnChanges() : void
    PizzeriaMapComponent : -refreshMarkers() : void
    PizzeriaMapComponent : +pad(0.2 arg1) : FALLBACK_BOUNDS
    PizzeriaMapComponent : -hasCoordinates(Pizzeria pizzeria) : boolean
    PizzeriaMapComponent : -buildPopupContent(Pizzeria pizzeria) : string
    PizzeriaMapComponent --> Pizzeria
```


## Metodi
- `+ ngOnChanges(nessun parametro) : void`
- `- refreshMarkers(nessun parametro) : void`
- `+ pad(`0.2 arg1`) : FALLBACK_BOUNDS`
- `- hasCoordinates(`Pizzeria pizzeria`) : boolean`
- `- buildPopupContent(`Pizzeria pizzeria`) : string`

---
_Documento generato automaticamente. Modifica il file sorgente o lo script per personalizzare il contenuto._
