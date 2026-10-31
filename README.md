# Digital Banking API

A small REST API to manage bank accounts: create an account, check its balance, deposit and withdraw.
Built as a study project with **Java 25**, **Spring Boot 4**, **MySQL** and a **React** front end.

![Java](https://img.shields.io/badge/Java-25-007396?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4-6DB33F?style=flat&logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?style=flat&logo=mysql&logoColor=white)
![React](https://img.shields.io/badge/React-19-61DAFB?style=flat&logo=react&logoColor=black)
![License: MIT](https://img.shields.io/badge/License-MIT-yellow?style=flat)

## Stack

- **Backend:** Java 25, Spring Boot 4 (Web MVC, Data JPA, Validation), Maven
- **Database:** MySQL 8
- **Frontend:** React 19, TypeScript, Vite

## Features

- Create an account with a unique 8-digit number
- List accounts and fetch one by id
- Check the balance
- Deposit and withdraw, with amount validation and no negative balance
- Consistent JSON error responses

## API

Base URL: `http://localhost:8081`

| Method | Path                    | Description      |
|--------|-------------------------|------------------|
| POST   | `/contas`               | Create an account |
| GET    | `/contas`               | List accounts     |
| GET    | `/contas/{id}`          | Get an account    |
| GET    | `/contas/{id}/saldo`    | Get the balance   |
| POST   | `/contas/{id}/deposito` | Deposit           |
| POST   | `/contas/{id}/saque`    | Withdraw          |

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

## Tests

```powershell
.\mvnw.cmd test        # backend
cd frontend; npm test  # frontend
```

## Screenshots

| Home | New account | Operations |
|------|-------------|------------|
| ![Home](docs/screenshots/tela-inicial.png) | ![New account](docs/screenshots/criar-conta.png) | ![Operations](docs/screenshots/operacoes.png) |

## License

MIT. See [LICENSE](LICENSE).

**Author:** Emanuel Vitor Fernandes · [GitHub](https://github.com/evfernandes-dev) · [LinkedIn](https://www.linkedin.com/in/emanuel-vitor-fernandes-6a3796421)
