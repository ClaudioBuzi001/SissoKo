# ToastService

- Tipo: `class`
- Percorso sorgente: `frontend/pizzeria-app/src/app/ui/notifications/toast.service.ts`
- Feature: `ui`
- Decorator: 
- `@Injectable`

## Diagramma
```mermaid
classDiagram
    class ToastService
    ToastService : -counter : 0
    ToastService : +stream : this.toasts$.asObservable()
    ToastService : +success(string message, timeout = 4000 arg2) : void
    ToastService : +error(string message, timeout = 5000 arg2) : void
    ToastService : +info(string message, timeout = 4000 arg2) : void
    ToastService : +dismiss(number id) : void
    ToastService : -push(string message, ToastType type, number timeout) : void
    ToastService --> Toast
    ToastService --> ToastType
```


## Metodi
- `+ success(`string message`, `timeout = 4000 arg2`) : void`
- `+ error(`string message`, `timeout = 5000 arg2`) : void`
- `+ info(`string message`, `timeout = 4000 arg2`) : void`
- `+ dismiss(`number id`) : void`
- `- push(`string message`, `ToastType type`, `number timeout`) : void`

---
_Documento generato automaticamente. Modifica il file sorgente o lo script per personalizzare il contenuto._
