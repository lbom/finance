# Trades Monorepo

Full-stack finance app with a Spring Boot API and a React (Vite) frontend.

## What’s inside
- `backend/`: Spring Boot 3 REST API, JPA, Flyway, Spring Security
- `frontend/`: React 19 app with Vite, MUI, React Query, React Router
- `docker-compose.yaml`: Postgres database for local development

## Tech stack
- Backend: Java 21, Spring Boot, Spring Security, Spring Data JPA, MapStruct, Flyway, OpenAPI
- Frontend: React, Vite, MUI, React Query, React Router, Axios
- Database: PostgreSQL

## Getting started

### 1) Start the database
From repo root:
```
docker compose up -d
```

Default local DB settings (from `backend/src/main/resources/application.yaml`):
- Host: `localhost`
- Port: `5432`
- Database: `trades`
- User: `lbom`
- Password: `123456`

### 2) Run the backend
```
cd backend
./gradlew bootRun
```

Backend runs on `http://localhost:8080`.

### 3) Run the frontend
```
cd frontend
npm install
npm run dev
```

Frontend runs on `http://localhost:5173`.

## API docs
When the backend is running, OpenAPI docs are available at:
- `http://localhost:8080/v3/api-docs`
- `http://localhost:8080/swagger-ui.html`

## Useful commands

### Backend
- Tests: `./gradlew test`
- OpenAPI JSON (requires backend running): `./gradlew generateOpenApiSpec`

### Frontend
- Build: `npm run build`
- Lint: `npm run lint`
- Preview: `npm run preview`

## Project structure (backend domains)
- `auth`: authentication endpoints, security setup
- `business`: business entities and transactions
- `dictionary`: currencies, institutions, symbols
- `fx`: FX rates and pairs
- `personal`: personal balances, investments, trades, transactions

## Notes
- Flyway migrations live in `backend/src/main/resources/db/migration`.
- The frontend has its own README in `frontend/README.md`.
