# Digital Banking API

A small REST API to manage bank accounts: create an account, check its balance, deposit and withdraw. Built as a study and portfolio project with **Java 25**, **Spring Boot 4**, **MySQL** and a **React** front end.

> **Project status:** study/portfolio project. It does not include authentication, pagination or deployment automation yet.

---

## Stack

- **Backend:** Java 25, Spring Boot 4 (Web MVC, Data JPA, Validation), Maven
- **Database:** MySQL 8
- **Frontend:** React 19, TypeScript, Vite
- **Tests:** JUnit 5, Mockito, MockMvc, Vitest

---

## Features

- Create an account with a unique 8-digit number.
- List accounts and fetch one by ID.
- Check the balance.
- Deposit and withdraw with amount validation and no negative balance.
- Consistent JSON error responses for validation, business and HTTP errors.
- Small React front end for manual testing.

---

## Architecture

The backend uses a layered structure:

1. **Controller** (`controller`): HTTP endpoints and `Location` header on creation.
2. **Service** (`service`): business rules, validation and transaction boundaries.
3. **Repository** (`repository`): Spring Data JPA access, including a row lock for balance changes.
4. **DTOs** (`dto`): request and response records.
5. **Exception handling** (`handler`): a `@RestControllerAdvice` that maps exceptions to JSON errors.

```text
digital-banking-api/
├── src/main/java/com/digitalbanking/api/
│   ├── controller/       # REST controller
│   ├── dto/              # Request and response records
│   ├── entity/           # JPA entity
│   ├── exception/        # Business exceptions
│   ├── handler/          # Global exception handler
│   ├── repository/       # Spring Data JPA repository
│   └── service/          # Business rules
├── src/main/resources/
│   ├── application.properties
│   └── db/schema.sql     # MySQL schema
├── src/test/java/        # Service and controller tests
├── frontend/             # React + TypeScript + Vite app
├── pom.xml
└── README.md
```

---

## Business rules

- Account numbers have 8 digits and are generated with retry when a collision occurs.
- Deposits and withdrawals require a positive amount with at most two decimal places.
- Withdrawals cannot make the balance negative.
- Balance changes use a repository query with a row lock (`findByIdComBloqueio`).
- The database schema is applied manually with `src/main/resources/db/schema.sql`; Hibernate does not create or update tables (`ddl-auto=none`).

---

## API

Base URL: `http://localhost:8081`

| Method | Path | Description |
|--------|------|-------------|
| POST | `/contas` | Create an account |
| GET | `/contas` | List accounts |
| GET | `/contas/{id}` | Get an account |
| GET | `/contas/{id}/saldo` | Get the balance |
| POST | `/contas/{id}/deposito` | Deposit |
| POST | `/contas/{id}/saque` | Withdraw |

Example:

```http
POST /contas
Content-Type: application/json

{ "titular": "Ana Silva" }
```

```json
{
  "id": 1,
  "numero": "00000042",
  "titular": "Ana Silva",
  "saldo": 0.00
}
```

Deposit and withdraw:

```http
POST /contas/1/deposito
Content-Type: application/json

{ "valor": 150.00 }
```

```http
POST /contas/1/saque
Content-Type: application/json

{ "valor": 50.00 }
```

Both operations return the updated account with `200 OK`. The React front end is optional; the API can also be tested with curl, Postman or Insomnia.

### Validation and errors

- `ContaRequest`: `@NotBlank` and `@Size(max = 120)` for the account holder.
- `ValorRequest`: `@NotNull`, `@Positive` and `@Digits` for amounts.
- `GlobalExceptionHandler` returns a JSON body with `timestamp`, `status`, `error`, `message` and `details`.
- Handled cases include validation errors, malformed JSON, invalid path variables, missing resources, unsupported methods and unexpected errors.

---

## Running locally

Requirements: JDK 25, MySQL 8 and Node.js 20+.

1. Create the database:

   ```powershell
   Get-Content src\main\resources\db\schema.sql | mysql -u root -p
   ```

2. Set the database credentials:

   ```powershell
   $env:DB_USERNAME = "root"
   $env:DB_PASSWORD = "your_password"
   ```

3. Start the API:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

4. Start the front end:

   ```powershell
   cd frontend
   npm install
   npm run dev
   ```

   The UI runs at `http://localhost:5173` and proxies `/api` to the API.

---

## Tests

Backend tests:

```powershell
.\mvnw.cmd test
```

- `ContaServiceTest` (11 tests): account creation, unique number generation, listing, lookup, balance, deposit, withdrawal, insufficient balance and invalid amounts.
- `ContaControllerTest` (12 tests): HTTP status codes, `Location` header, validation errors, 404, 405 and business error responses.

Frontend tests:

```powershell
cd frontend
npm test
```

The frontend suite has 3 tests for the formatting helpers.

---

## Limitations and next steps

- No authentication or authorization.
- No pagination for the account list.
- No Docker or CI workflow yet.
- The documentation and screenshots are kept in `docs/screenshots`.

---

## License

MIT. See [LICENSE](LICENSE).

**Author:** Emanuel Vitor Fernandes · [GitHub](https://github.com/evfernandes-dev) · [LinkedIn](https://www.linkedin.com/in/emanuel-vitor-fernandes-6a3796421)
