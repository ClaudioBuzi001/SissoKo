# GeocodingClient

- Tipo: `class`
- Package: `com.pizzeria.pizzeriaservice.geocoding`
- Percorso sorgente: `backend/pizzeria-service/src/main/java/com/pizzeria/pizzeriaservice/geocoding/GeocodingClient.java`
- Annotazioni: 
- `@Component`

## Diagramma
```mermaid
classDiagram
    class GeocodingClient
    GeocodingClient : -LOGGER : Logger
    GeocodingClient : -restClient : RestClient
    GeocodingClient : -geocodingEnabled : boolean
    GeocodingClient : +geocode(String address, String city) : Optional<LatLon>
    GeocodingClient : +LatLon(double latitude, double longitude) : record
    GeocodingClient : -GeocodingResponse(String lat, String lon) : record
```


## Metodi
- `+ geocode(`String address`, `String city`) : Optional<LatLon>`
- `+ LatLon(`double latitude`, `double longitude`) : record`
- `- GeocodingResponse(`String lat`, `String lon`) : record`


---
_Documento generato automaticamente. Modifica la classe sorgente o aggiorna lo script per personalizzare il contenuto._
