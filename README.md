# Digital Banking API

API REST simples para estudo, com interface web básica em HTML, CSS e JavaScript puro.

Permite criar contas, listar contas, consultar uma conta, depositar e sacar.

## Funcionalidades

- Criar conta com número aleatório de 8 dígitos
- Listar contas cadastradas
- Consultar uma conta pelo id
- Depositar e sacar com validação
- Interface web servida pelo próprio Spring Boot

## Tecnologias

- Java 25
- Spring Boot 4 (Web MVC, Data JPA e Validation)
- MySQL 8
- HTML, CSS e JavaScript puro
- JUnit 5, Mockito e MockMvc

## Como rodar

Pré-requisitos: JDK 25 e MySQL 8.

1. Crie o banco:

   ```powershell
   Get-Content src\main\resources\db\schema.sql | mysql -u root -p
   ```

   Se o comando `mysql` não estiver no PATH, abra o MySQL Workbench e execute o arquivo `src/main/resources/db/schema.sql`.

2. Configure o usuário e a senha do banco (opcional; o padrão é `root` sem senha):

   ```powershell
   $env:DB_USERNAME = "root"
   $env:DB_PASSWORD = "sua_senha"
   ```

3. Rode a aplicação:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

4. Abra a interface em [http://localhost:8081](http://localhost:8081).

A interface e a API rodam juntas no mesmo endereço.

## Endpoints

Base: `http://localhost:8081`

| Método | Caminho | Descrição |
| --- | --- | --- |
| POST | `/contas` | Cria uma conta |
| GET | `/contas` | Lista todas as contas |
| GET | `/contas/{id}` | Busca uma conta pelo id |
| POST | `/contas/{id}/deposito` | Deposita um valor |
| POST | `/contas/{id}/saque` | Saca um valor |

### Exemplos

Criar uma conta:

```http
POST /contas
Content-Type: application/json

{ "titular": "Ana Silva" }
```

Resposta:

```json
{
  "id": 1,
  "numero": "00000042",
  "titular": "Ana Silva",
  "saldo": 0.00
}
```

Depositar ou sacar:

```http
POST /contas/1/deposito
Content-Type: application/json

{ "valor": 150.00 }
```

Erro de negócio:

```json
{
  "status": 409,
  "mensagem": "Saldo insuficiente para realizar o saque."
}
```

## Estrutura do projeto

```text
src/main/java/com/digitalbanking/api/
├── controller/ContaController.java
├── dto/            ContaRequest, ContaResponse, ValorRequest, ErroResponse
├── entity/Conta.java
├── handler/GlobalExceptionHandler.java
├── repository/ContaRepository.java
└── service/ContaService.java

src/main/resources/
├── static/         index.html, style.css, app.js
├── db/schema.sql
└── application.properties

src/test/java/com/digitalbanking/api/
├── controller/ContaControllerTest.java
└── service/ContaServiceTest.java
```

## Regras de negócio

- O número da conta tem 8 dígitos e é gerado aleatoriamente. O banco impede números duplicados.
- Depósitos e saques validam valor positivo e com no máximo duas casas decimais.
- O saque não pode deixar o saldo negativo.
- Depósitos e saques usam bloqueio de linha (`findByIdComBloqueio`).
- O banco é criado manualmente com `src/main/resources/db/schema.sql`.

## Testes

```powershell
.\mvnw.cmd test
```

- `ContaServiceTest` (7 testes): criação, listagem, busca, depósito, saque, conta não encontrada e saldo insuficiente.
- `ContaControllerTest` (8 testes): status HTTP, header `Location`, validação, 404 e 409.

## Limitações

- Sem autenticação ou autorização.
- Sem paginação na listagem.
- Sem Docker ou CI.

## Licença

MIT. Veja [LICENSE](LICENSE).

**Autor:** Emanuel Vitor Fernandes · [GitHub](https://github.com/evfernandes-dev) · [LinkedIn](https://www.linkedin.com/in/emanuel-vitor-fernandes-6a3796421)
