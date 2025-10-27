# Pizzeria Platform

Applicazione full-stack per la gestione di pizzerie composta da:
- **Backend** Spring Boot (Java 21, MongoDB) che espone API RESTful e dati seed automatici.
- **Frontend** Angular 18 standalone con ricerca client-side delle pizzerie.
- **Infra** Docker Compose per MongoDB in locale e proxy dev Angular per integrare le API.

## Avvio rapido
- Backend: `cd backend/pizzeria-service && mvn spring-boot:run`
- Frontend: `cd frontend/pizzeria-app && npm install && npm run start`
- Database: `docker compose up -d mongo` (eseguito da `mega_prova`)

Le API sono raggiungibili su `http://localhost:8080/api/pizzerias`, mentre il frontend utilizza il proxy su `http://localhost:4200`.

## Documentazione
La documentazione è generata automaticamente via `node scripts/generate-docs.js`.

### Backend (classi)
- [Pizzeria](docs/backend/classes/Pizzeria.md)
- [PizzeriaController](docs/backend/classes/PizzeriaController.md)
- [PizzeriaDataInitializer](docs/backend/classes/PizzeriaDataInitializer.md)
- [PizzeriaNotFoundException](docs/backend/classes/PizzeriaNotFoundException.md)
- [PizzeriaRepository](docs/backend/classes/PizzeriaRepository.md)
- [PizzeriaService](docs/backend/classes/PizzeriaService.md)
- [PizzeriaServiceApplication](docs/backend/classes/PizzeriaServiceApplication.md)

### Frontend (entità)
- [AppComponent](docs/frontend/entities/AppComponent.md)
- [Pizzeria](docs/frontend/entities/Pizzeria.md)
- [PizzeriaService](docs/frontend/entities/PizzeriaService.md)

### Frontend (feature)
- [pizzerias](docs/frontend/features/pizzerias.md)

## Aggiornamento automatico
- Generazione manuale: `node scripts/generate-docs.js`
- Watcher continuo: `node scripts/generate-docs.js --watch`

In modalità watch, il generatore osserva backend e frontend e rigenera i file quando vengono creati o modificati i sorgenti. I file in `docs/` sono idempotenti e non vanno modificati a mano: personalizza il contenuto aggiornando i sorgenti oppure il generatore.

