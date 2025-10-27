# Pizzeria

- Tipo: `class`
- Package: `com.pizzeria.pizzeriaservice.model`
- Percorso sorgente: `backend/pizzeria-service/src/main/java/com/pizzeria/pizzeriaservice/model/Pizzeria.java`
- Annotazioni: 
- `@Document`

## Diagramma
```mermaid
classDiagram
    class Pizzeria
    Pizzeria : -id : String
    Pizzeria : -name : String
    Pizzeria : -address : String
    Pizzeria : -city : String
    Pizzeria : -phoneNumber : String
    Pizzeria : -openingHours : String
    Pizzeria : -deliveryAvailable : boolean
    Pizzeria : +getId() : String
    Pizzeria : +setId(String id) : void
    Pizzeria : +getName() : String
    Pizzeria : +setName(String name) : void
    Pizzeria : +getAddress() : String
    Pizzeria : +setAddress(String address) : void
    Pizzeria : +getCity() : String
    Pizzeria : +setCity(String city) : void
    Pizzeria : +getPhoneNumber() : String
    Pizzeria : +setPhoneNumber(String phoneNumber) : void
    Pizzeria : +getOpeningHours() : String
    Pizzeria : +setOpeningHours(String openingHours) : void
    Pizzeria : +isDeliveryAvailable() : boolean
    Pizzeria : +setDeliveryAvailable(boolean deliveryAvailable) : void
```


## Metodi
- `+ getId(nessun parametro) : String`
- `+ setId(`String id`) : void`
- `+ getName(nessun parametro) : String`
- `+ setName(`String name`) : void`
- `+ getAddress(nessun parametro) : String`
- `+ setAddress(`String address`) : void`
- `+ getCity(nessun parametro) : String`
- `+ setCity(`String city`) : void`
- `+ getPhoneNumber(nessun parametro) : String`
- `+ setPhoneNumber(`String phoneNumber`) : void`
- `+ getOpeningHours(nessun parametro) : String`
- `+ setOpeningHours(`String openingHours`) : void`
- `+ isDeliveryAvailable(nessun parametro) : boolean`
- `+ setDeliveryAvailable(`boolean deliveryAvailable`) : void`


---
_Documento generato automaticamente. Modifica la classe sorgente o aggiorna lo script per personalizzare il contenuto._
