# Digital Banking API

Simple REST API to create bank accounts, check balances, deposit and withdraw. A basic web page is served by Spring Boot.

## Stack

Java 25 · Spring Boot 4 · Spring Data JPA · MySQL 8 · JUnit 5 · Mockito

## Run

Requirements: JDK 25 and MySQL 8.

1. Create the database with `src/main/resources/db/schema.sql`.
2. Optionally set `DB_USERNAME` and `DB_PASSWORD`.
3. Start the app:

```powershell
.\mvnw.cmd spring-boot:run
```

Open http://localhost:8081.

## Endpoints

`POST /contas`, `GET /contas`, `GET /contas/{id}`, `POST /contas/{id}/deposito`, `POST /contas/{id}/saque`

## Tests

```powershell
.\mvnw.cmd test
```
