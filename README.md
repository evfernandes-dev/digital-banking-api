# Digital Banking API

Small REST API to create bank accounts, check balances, deposit and withdraw. Study/portfolio project built with Java 25, Spring Boot 4, MySQL and a React front end.

> The project has no authentication, pagination or deployment automation yet.

## Features

- Create an account with a unique 8-digit number
- List accounts and fetch one by ID
- Check the balance
- Deposit and withdraw with amount validation and no negative balance
- Consistent JSON error responses
- Small React front end for manual testing

## Stack

- **Backend:** Java 25, Spring Boot 4 (Web MVC, Data JPA, Validation), Maven
- **Database:** MySQL 8
- **Frontend:** React 19, TypeScript, Vite
- **Tests:** JUnit 5, Mockito, MockMvc, Vitest

## Architecture

Controller → Service → Repository, with DTOs and a global exception handler. Balance changes use a repository query with a row lock.

```text
src/main/java/com/digitalbanking/api/
├── controller/   REST endpoints
├── dto/          Request and response records
├── entity/       JPA entity
├── exception/    Business exceptions
├── handler/      Global exception handler
├── repository/   Spring Data JPA repository
└── service/      Business rules
```

## API

Base URL: `http://localhost:8081`

| Method | Path | Description |
| --- | --- | --- |
| POST | `/contas` | Create an account |
| GET | `/contas` | List accounts |
| GET | `/contas/{id}` | Get an account |
| GET | `/contas/{id}/saldo` | Get the balance |
| POST | `/contas/{id}/deposito` | Deposit |
| POST | `/contas/{id}/saque` | Withdraw |

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

Deposits and withdrawals accept `{ "valor": 150.00 }` and return the updated account with `200 OK`.

## Business rules

- Account numbers have 8 digits and are generated with retry on collision.
- Deposits and withdrawals require a positive amount with at most two decimal places.
- Withdrawals cannot make the balance negative.
- Balance changes use a row lock (`findByIdComBloqueio`).
- The schema is applied manually with `src/main/resources/db/schema.sql`; Hibernate does not create tables (`ddl-auto=none`).

## Running locally

Requirements: JDK 25, MySQL 8 and Node.js 20+.

1. Create the database:

   ```powershell
   Get-Content src\main\resources\db\schema.sql | mysql -u root -p
   ```

2. Set the credentials:

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

## Tests

```powershell
.\mvnw.cmd test
cd frontend; npm test
```

- `ContaServiceTest` (11 tests): creation, number generation, listing, lookup, balance, deposit, withdrawal and validation.
- `ContaControllerTest` (12 tests): status codes, `Location` header, validation errors, 404, 405 and business errors.
- Frontend: 3 tests for the formatting helpers.

## Limitations

- No authentication or authorization.
- No pagination for the account list.
- No Docker or CI workflow yet.

## License

MIT. See [LICENSE](LICENSE).

**Author:** Emanuel Vitor Fernandes · [GitHub](https://github.com/evfernandes-dev) · [LinkedIn](https://www.linkedin.com/in/emanuel-vitor-fernandes-6a3796421)
